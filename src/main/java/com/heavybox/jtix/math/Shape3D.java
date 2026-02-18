package com.heavybox.jtix.math;

public interface Shape3D {

    boolean containsPoint(float x, float y, float z);
    default boolean containsPoint(final Vector3 point) { return containsPoint(point.x, point.y, point.z); }
    float volume();
    float surfaceArea();
    Vector2 centroid();

}
