package com.heavybox.jtix.math;

public class Transform2D {

    public float x    = 0;
    public float y    = 0;
    public float deg  = 0;
    public float sclX = 1;
    public float sclY = 1;

    public Transform2D idt() {
        x    = 0;
        y    = 0;
        deg  = 0;
        sclX = 1;
        sclY = 1;
        return this;
    }

    public Transform2D inv() {
        x *= -1;
        y *= -1;
        deg *= -1;
        sclX = 1f / sclX;
        sclY = 1f / sclY;
        return this;
    }

}
