package com.heavybox.jtix.math;

import org.jetbrains.annotations.NotNull;

/*
Represents a fixed canonical shape
 */
public interface Shape2D {

    boolean         containsPoint(float x, float y);
    default boolean containsPoint(final Vector2 point) { return containsPoint(point.x, point.y); }
    default boolean containsPoint(final Vector2 point, @NotNull final Transform2D transform2D) { return containsPoint(point.x, point.y, transform2D);}
    default boolean containsPoint(float x, float y, @NotNull final Transform2D transform2D) {
        // translate to local origin
        float dx = x - transform2D.x;
        float dy = y - transform2D.y;
        // inverse rotation
        float rad = -MathUtils.degreesToRadians * transform2D.deg;
        float cos = MathUtils.cosRad(rad);
        float sin = MathUtils.sinRad(rad);
        float lx = dx * cos - dy * sin;
        float ly = dx * sin + dy * cos;
        // inverse scale
        lx /= transform2D.sclX;
        ly /= transform2D.sclY;
        return containsPoint(lx, ly);
    }

    float           area();
    default float   area(@NotNull final Transform2D transform2D) { return Math.abs(transform2D.sclX * transform2D.sclY) * area(); }

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
