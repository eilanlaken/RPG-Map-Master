package com.heavybox.jtix.math;

public class Shape2DRectangle implements Shape2D {

    public final float  w;
    public final float  h;
    private final float area;
    private final float perimeter;

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

    // TODO: test
    @Override
    public boolean containsPoint(float x, float y, Transform2D t) {
        // 1. translate to local origin
        float dx = x - t.x;
        float dy = y - t.y;

        // 2. inverse rotate
        float rad = (float) Math.toRadians(-t.deg);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float lx = dx * cos - dy * sin;
        float ly = dx * sin + dy * cos;

        // 3. inverse scale
        lx /= t.sclX;
        ly /= t.sclY;

        // 4. rectangle test
        return Math.abs(lx) <= w * 0.5f &&
                Math.abs(ly) <= h * 0.5f;
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
    public void centroid(Vector2 out) {
        out.set(0,0);
    }
}
