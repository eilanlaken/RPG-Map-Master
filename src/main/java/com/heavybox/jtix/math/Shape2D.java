package com.heavybox.jtix.math;

import org.jetbrains.annotations.NotNull;

public interface Shape2D {

    boolean         containsPoint(float x, float y);
    default boolean containsPoint(final Vector2 point) { return containsPoint(point.x, point.y); }
    default boolean containsPoint(final Vector2 point, @NotNull final Transform2D transform2D) { return containsPoint(point.x, point.y, transform2D);}
    float           area();
    float           perimeter();
    void            centerOfMass(Vector2 out);

    // works by transforming the *point* instead of the shape by the inverse transform and then
    // checking if the transformed back point lies within the original shape.
    // TODO: test for circles, rectangles and polygons.
    default boolean containsPoint(float x, float y, @NotNull final Transform2D transform2D) {
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

}

/*
Vec2 centroid()

AABB bounds()

boolean intersects(Shape2D other)

boolean intersectsAABB(AABB box)

float distanceTo(float x, float y) (signed if possible)

Vec2 closestPoint(float x, float y)
 */
