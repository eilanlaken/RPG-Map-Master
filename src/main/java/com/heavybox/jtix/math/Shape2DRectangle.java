package com.heavybox.jtix.math;

public class Shape2DRectangle implements Shape2D {

    public  final float  w;
    public  final float  h;
    private final float  area;
    private final float  perimeter;

    public Shape2DRectangle(float w, float h) {
        this.w = w;
        this.h = h;
        this.area = w * h;
        this.perimeter = 2 * (w + h);
    }

    @Override
    public boolean containsPoint(float x, float y) {
        return Math.abs(x) <= w * 0.5f && Math.abs(y) <= h * 0.5f;
    }

    @Override
    public float area() {
        return area;
    }

    @Override
    public float perimeter() {
        return perimeter;
    }

    @Override
    public void centerOfMass(Vector2 out) {
        out.set(0,0);
    }
}
