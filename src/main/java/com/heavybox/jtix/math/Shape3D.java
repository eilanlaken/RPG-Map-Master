package com.heavybox.jtix.math;

public interface Shape3D {

    boolean         containsPoint(float x, float y, float z);
    boolean         containsPoint(float x, float y, float z, final Transform3D transform3D);
    default boolean containsPoint(final Vector3 point) { return containsPoint(point.x, point.y, point.z); }
    default boolean containsPoint(final Vector3 point, final Transform3D transform3D) { return containsPoint(point.x, point.y, point.z, transform3D);}
    float           volume();
    float           surfaceArea();
    Vector2         centroid();

}
