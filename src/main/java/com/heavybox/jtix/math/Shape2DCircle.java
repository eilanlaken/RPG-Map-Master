package com.heavybox.jtix.math;

public class Shape2DCircle implements Shape2D {

    public final float r;
    public final float area;
    public final float circumference;

    public Shape2DCircle(float r) {
        this.r = r;
        this.area = MathUtils.PI * r * r;
        this.circumference = MathUtils.PI_TWO * r;
    }

    @Override
    public boolean containsPoint(float x, float y) {
        return x * x + y * y <= r * r;
    }

    // TODO: test
    @Override
    public boolean containsPoint(float x, float y, Transform2D t) {
        // translate to local origin
        float dx = x - t.x;
        float dy = y - t.y;

        // inverse rotation
        float rad = (float) Math.toRadians(-t.deg);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float lx = dx * cos - dy * sin;
        float ly = dx * sin + dy * cos;

        // inverse scale
        lx /= t.sclX;
        ly /= t.sclY;

        return lx * lx + ly * ly <= r * r;
    }

    @Override
    public float area() {
        return area;
    }

    @Override
    public float perimeter() {
        return circumference;
    }

    @Override
    public void centroid(Vector2 out) {
        out.set(0,0);
    }
}
