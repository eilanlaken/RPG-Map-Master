package com.heavybox.jtix.math;

import org.jetbrains.annotations.NotNull;

public interface Shape2D {

    boolean         containsPoint(float x, float y);
    default boolean containsPoint(final Vector2 point) { return containsPoint(point.x, point.y); }
    default boolean containsPoint(final Vector2 point, @NotNull final Transform2D transform2D) { return containsPoint(point.x, point.y, transform2D);}

    float           area();
    default float   area(@NotNull final Transform2D transform2D) { return Math.abs(transform2D.sclX * transform2D.sclY) * area(); }
    default boolean containsPoint(float x, float y, @NotNull final Transform2D transform2D) {
        // TODO: test for circles, rectangles and polygons.
        // translate to local origin
        float dx = x - transform2D.x;
        float dy = y - transform2D.y;
        // inverse rotation
        float rad = (float) Math.toRadians(-transform2D.deg);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float lx = dx * cos - dy * sin;
        float ly = dx * sin + dy * cos;
        // inverse scale
        lx /= transform2D.sclX;
        ly /= transform2D.sclY;
        return containsPoint(lx, ly);
    }

    float           perimeter();
    void            centerOfMass(@NotNull Vector2 out);
    default void    centerOfMass(@NotNull Transform2D transform2D, @NotNull Vector2 out) { centerOfMass(out); out.add(transform2D.x, transform2D.y); }

}

/*
Vec2 centroid()

AABB bounds()

boolean intersects(Shape2D other)

boolean intersectsAABB(AABB box)

float distanceTo(float x, float y) (signed if possible)

Vec2 closestPoint(float x, float y)
 */
