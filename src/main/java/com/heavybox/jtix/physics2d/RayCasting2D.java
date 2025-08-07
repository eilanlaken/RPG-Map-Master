package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;
import org.jetbrains.annotations.NotNull;

final class RayCasting2D {

    private final World2D world;
    private final MemoryPool<RayCasting2DIntersection> intersectionsPool;

    RayCasting2D(final World2D world) {
        this.world = world;
        this.intersectionsPool = world.intersectionsPool;
    }

    void calculateIntersections(RayCasting2DRay ray, Array<Body2D> bodies, @NotNull Array<RayCasting2DIntersection> intersections) {
        for (Body2D body : bodies) {
            for (Body2DCollider collider : body.colliders) {
                if (collider instanceof Body2DColliderCircle) {
                    rayVsCircle(ray, (Body2DColliderCircle) collider, intersections);
                }
                if (collider instanceof Body2DColliderRectangle) {
                    rayVsRectangle(ray, (Body2DColliderRectangle) collider, intersections);
                }
                // TODO: implement polygon
            }

        }
    }

    private void rayVsCircle(RayCasting2DRay ray, Body2DColliderCircle circle, @NotNull Array<RayCasting2DIntersection> intersections) {
        Vector2 m = new Vector2(ray.originX, ray.originY).sub(circle.worldCenter.x, circle.worldCenter.y);
        float b = 2 * m.dot(ray.dirX, ray.dirY);
        float c = m.len2() - circle.r * circle.r;
        float det = b * b - 4 * c;
        if (det < 0) return;

        if (MathUtils.isZero(det)) {
            float t = -b / 2.0f;
            if (t < 0 || t > ray.dst) return;
            RayCasting2DIntersection result = intersectionsPool.allocate();
            result.collider = circle;
            result.point.set(ray.originX, ray.originY).add(t * ray.dirX, t * ray.dirY);
            result.direction.set(result.point).sub(circle.worldCenter.x, circle.worldCenter.y);
            intersections.add(result);
            return;
        }

        float t1 = (-b + (float) Math.sqrt(det)) / 2.0f;
        if (t1 > 0 && t1 < ray.dst) {
            RayCasting2DIntersection result1 = intersectionsPool.allocate();
            result1.collider = circle;
            result1.point.set(ray.originX, ray.originY).add(t1 * ray.dirX, t1 * ray.dirY);
            result1.direction.set(result1.point).sub(circle.worldCenter.x, circle.worldCenter.y);
            intersections.add(result1);
        }

        float t2 = (-b - (float) Math.sqrt(det)) / 2.0f;
        if (t2 > 0 && t2 < ray.dst) {
            RayCasting2DIntersection result2 = intersectionsPool.allocate();
            result2.collider = circle;
            result2.point.set(ray.originX, ray.originY).add(t2 * ray.dirX, t2 * ray.dirY);
            result2.direction.set(result2.point).sub(circle.worldCenter.x, circle.worldCenter.y);
            intersections.add(result2);
        }
    }

    private void rayVsRectangle(RayCasting2DRay ray, Body2DColliderRectangle rectangle, @NotNull Array<RayCasting2DIntersection> intersections) {
        // broad phase
        float boundingRadius = rectangle.boundingRadius();
        Vector2 m = new Vector2(ray.originX, ray.originY).sub(rectangle.worldCenter.x, rectangle.worldCenter.y);
        float b = 2 * m.dot(ray.dirX, ray.dirY);
        float c = m.len2() - boundingRadius * boundingRadius;
        float det = b * b - 4 * c;
        if (det < 0) return;

        // narrow phase
        float dst = ray.dst == Float.POSITIVE_INFINITY ? 1.0f : ray.dst;
        float x3 = ray.originX;
        float y3 = ray.originY;
        float x4 = ray.originX + dst * ray.dirX;
        float y4 = ray.originY + dst * ray.dirY;

        Vector2 c0 = rectangle.c0;
        Vector2 c1 = rectangle.c1;
        Vector2 c2 = rectangle.c2;
        Vector2 c3 = rectangle.c3;

        Array<Vector2> edges = new Array<>(true, 4);
        edges.add(c0, c1, c2, c3);

        for (int i = 0; i < 4; i++) {
            Vector2 p = edges.getCyclic(i);
            float x1 = p.x;
            float y1 = p.y;

            Vector2 q = edges.getCyclic(i+1);
            float x2 = q.x;
            float y2 = q.y;

            float den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
            if (MathUtils.isZero(den)) continue;

            float t =  ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
            float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / den;

            if (ray.dst == Float.POSITIVE_INFINITY) {
                if (t > 0 && t < 1 && u > 0) {
                    RayCasting2DIntersection result = intersectionsPool.allocate();
                    result.collider = rectangle;
                    result.point.set(x1 + t * (x2 - x1), y1 + t * (y2 - y1));
                    result.direction.set(x4 - x3, y4 - y3);
                    intersections.add(result);
                }
            } else if (t > 0 && t < 1 && u > 0 && u < 1) {
                RayCasting2DIntersection result = intersectionsPool.allocate();
                result.collider = rectangle;
                result.point.set(x1 + t * (x2 - x1), y1 + t * (y2 - y1));
                result.direction.set(x4 - x3, y4 - y3);
                intersections.add(result);
            }
        }
    }

}
