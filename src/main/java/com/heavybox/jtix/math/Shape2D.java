package com.heavybox.jtix.math;

public interface Shape2D {

    boolean         containsPoint(float x, float y);
    boolean         containsPoint(float x, float y, final Transform2D transform2D);
    default boolean containsPoint(final Vector2 point) { return containsPoint(point.x, point.y); }
    default boolean containsPoint(final Vector2 point, final Transform2D transform2D) { return containsPoint(point.x, point.y, transform2D);}
    float           area();
    float           perimeter();
    void            centroid(Vector2 out);

}

/*
Vec2 centroid()

AABB bounds()

boolean intersects(Shape2D other)

boolean intersectsAABB(AABB box)

float distanceTo(float x, float y) (signed if possible)

Vec2 closestPoint(float x, float y)
 */
