package com.heavybox.jtix.math;

import org.jetbrains.annotations.NotNull;

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
    public final boolean containsPoint(float x, float y) {
        return x * x + y * y <= r * r;
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
    public void centerOfMass(@NotNull Vector2 out) {
        out.set(0,0);
    }

}
