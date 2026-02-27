package com.heavybox.jtix.math;

public class Transform2D {

    public float x    = 0;
    public float y    = 0;
    public float deg  = 0;
    public float sclX = 1;
    public float sclY = 1;

    public Transform2D() {}

    public Transform2D(float x, float y, float deg, float sclX, float sclY) {
        this.x = x;
        this.y = y;
        this.deg = deg;
        this.sclX = sclX;
        this.sclY = sclY;
    }

    public Transform2D(final Transform2D other) {
        this.x = other.x;
        this.y = other.y;
        this.deg = other.deg;
        this.sclX = other.sclX;
        this.sclY = other.sclY;
    }

    public Transform2D idt() {
        x    = 0;
        y    = 0;
        deg  = 0;
        sclX = 1;
        sclY = 1;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transform2D other = (Transform2D) obj;
        return MathUtils.floatsEqual(x, other.x)
                && MathUtils.floatsEqual(y, other.y)
                && MathUtils.floatsEqual(deg, other.deg)
                && MathUtils.floatsEqual(sclX, other.sclX)
                && MathUtils.floatsEqual(sclY, other.sclY);
    }

}
