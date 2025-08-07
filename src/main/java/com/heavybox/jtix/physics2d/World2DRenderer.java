package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

import java.util.Set;

// TODO: switch to newer 2d renderer.
public class World2DRenderer {

    private static final float TINT_STATIC     = new Color(1,1,0,1).toFloatBits();
    private static final float TINT_KINEMATIC  = new Color(1,0,1,1).toFloatBits();
    private static final float TINT_NEWTONIAN  = new Color(0,1,1,1).toFloatBits();
    private static final float TINT_CENTER_OF_MASS = new Color(1,0.1f,0.2f,1).toFloatBits();
    private static final float TINT_CONSTRAINT = new Color(0.9f, 0.1f, 0.3f, 1).toFloatBits();
    private static final float TINT_RAY_HIT    = new Color(0.2f,1,1,1).toFloatBits();
    private static final float TINT_RAY        = new Color(0.4f, 0.2f, 0.8f, 1).toFloatBits();

    private final World2D world;

    World2DRenderer(final World2D world) {
        this.world = world;
    }

    public void render(Renderer2D renderer) {
        final float pointPixelRadius = 3;
        float scaleX = renderer.getCurrentCamera().getViewportWidth()  * pointPixelRadius / Graphics.getWindowWidth();
        float scaleY = renderer.getCurrentCamera().getViewportHeight() * pointPixelRadius / Graphics.getWindowHeight();

        // render bodies
        if (world.renderBodies) {
            Array<Body2D> bodies = world.allBodies;
            for (Body2D body : bodies) {
                // render center of mass

                Array<Body2DCollider> colliders = body.colliders;
                for (Body2DCollider collider : colliders) {
                    /* render a circle */
                    if (collider instanceof Body2DColliderCircle) {
                        Body2DColliderCircle circle = (Body2DColliderCircle) collider;
                        float tint = body.motionType == Body2D.MotionType.STATIC ? TINT_STATIC : body.motionType == Body2D.MotionType.KINEMATIC ? TINT_KINEMATIC : TINT_NEWTONIAN;
                        Vector2 worldCenter = circle.worldCenter();
                        float r = circle.r;
                        float angleRad = body.radians;
                        float x1 = worldCenter.x;
                        float y1 = worldCenter.y;
                        float x2 = x1 + r * MathUtils.cosRad(angleRad);
                        float y2 = y1 + r * MathUtils.sinRad(angleRad);
                        renderer.setColor(tint);
                        renderer.drawCircleThin(r, 15, worldCenter.x, worldCenter.y, body.radians * MathUtils.radiansToDegrees,1, 1);
                        renderer.drawLineThin(x1,y1,x2,y2);
                        continue;
                    }

                    /* render a rectangle */
                    if (collider instanceof Body2DColliderRectangle) {
                        Body2DColliderRectangle rectangle = (Body2DColliderRectangle) collider;
                        float tint = body.motionType == Body2D.MotionType.STATIC ? TINT_STATIC : body.motionType == Body2D.MotionType.KINEMATIC ? TINT_KINEMATIC : TINT_NEWTONIAN;
                        float angleRad = body.radians;
                        float x0 = rectangle.c0.x;
                        float y0 = rectangle.c0.y;
                        float x1 = rectangle.c1.x;
                        float y1 = rectangle.c1.y;
                        float x2 = rectangle.c2.x;
                        float y2 = rectangle.c2.y;
                        float x3 = rectangle.c3.x;
                        float y3 = rectangle.c3.y;
                        renderer.setColor(tint);
                        renderer.drawRectangleThin(x0,y0, x1,y1, x2,y2, x3,y3);
                        renderer.drawLineThin(
                                rectangle.worldCenter().x,
                                rectangle.worldCenter().y,
                                rectangle.worldCenter().x + 0.5f * rectangle.width * MathUtils.cosRad(angleRad + collider.offsetAngleRad),
                                rectangle.worldCenter().y + 0.5f * rectangle.width * MathUtils.sinRad(angleRad + collider.offsetAngleRad));
                        continue;
                    }

                    /* render a polygon */
                    if (collider instanceof Body2DColliderPolygon) {
                        Body2DColliderPolygon polygon = (Body2DColliderPolygon) collider;
                        float tint = body.motionType == Body2D.MotionType.STATIC ? TINT_STATIC : body.motionType == Body2D.MotionType.KINEMATIC ? TINT_KINEMATIC : TINT_NEWTONIAN;
                        renderer.setColor(tint);
                        renderer.drawPolygonThin(polygon.vertices, true, body.x, body.y, body.radians * MathUtils.radiansToDegrees, 1, 1);
                        continue;
                    }
                }
                renderer.setColor(TINT_CENTER_OF_MASS);
                renderer.drawCircleFilled(1, 10, body.cmX, body.cmY,body.radians * MathUtils.radiansToDegrees, scaleX, scaleY);
            }
        }

        // TODO: render velocities
        if (world.renderVelocities) {

        }

        // TODO: render joints
        if (world.renderConstraints) {

        }

        // TODO: render contact points
        if (world.renderContacts) {

        }

        // TODO: render rays and ray casting results
        if (world.renderRays) {
            Set<RayCasting2DRay> rays = world.allRays.keySet();
            for (RayCasting2DRay ray : rays) {
                float scl = ray.dst == Float.POSITIVE_INFINITY || Float.isNaN(ray.dst) ? 100 : ray.dst;
                renderer.setColor(TINT_RAY);
                renderer.drawLineThin(ray.originX, ray.originY, ray.originX + scl * ray.dirX, ray.originY + scl * ray.dirY);
            }

            Array<RayCasting2DIntersection> intersections = world.intersections;
            for (RayCasting2DIntersection intersection : intersections) {
                Vector2 point = intersection.point;
                renderer.setColor(TINT_RAY_HIT);
                renderer.drawCircleFilled(1, 10, point.x, point.y, 0, scaleX, scaleY);
            }
        }

    }

}
