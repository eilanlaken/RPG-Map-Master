package com.heavybox.jtix.math;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    // TODO: TEST
    public static void calculateGlobal(final @Nullable Transform2D parent, final @NotNull Transform2D local,
                                       final @NotNull Transform2D outGlobal) {
        float refX = parent == null ? 0 : parent.x;
        float refY = parent == null ? 0 : parent.y;
        float refDeg = parent == null ? 0 : parent.deg;
        float refSclX = parent == null ? 1 : parent.sclX;
        float refSclY = parent == null ? 1 : parent.sclY;
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = local.x * cos - local.y * sin;
        float y = local.x * sin + local.y * cos;
        outGlobal.x = refX + x * refSclX;
        outGlobal.y = refY + y * refSclY;
        outGlobal.deg  = local.deg + refDeg;
        outGlobal.sclX = local.sclX * refSclX;
        outGlobal.sclY = local.sclY * refSclY;
    }

    // TODO: TEST
    public static void calculateGlobal(final @Nullable Transform2D parent, final @NotNull Transform2D local,
                                       final float localOffsetX, final float localOffsetY,
                                       final @NotNull Transform2D outGlobal) {
        float refX = parent == null ? 0 : parent.x;
        float refY = parent == null ? 0 : parent.y;
        float refDeg = parent == null ? 0 : parent.deg;
        float refSclX = parent == null ? 1 : parent.sclX;
        float refSclY = parent == null ? 1 : parent.sclY;
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = local.x * cos - local.y * sin;
        float y = local.x * sin + local.y * cos;
        outGlobal.x = refX + x * refSclX + localOffsetX * cos - localOffsetY * sin; // add the rotated offset vector x component
        outGlobal.y = refY + y * refSclY + localOffsetX * sin + localOffsetY * cos; // add the rotated offset vector y component
        outGlobal.deg  = local.deg + refDeg;
        outGlobal.sclX = local.sclX * refSclX;
        outGlobal.sclY = local.sclY * refSclY;
    }

}
