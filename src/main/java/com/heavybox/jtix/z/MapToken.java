package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class MapToken {

    public final int layer;
    public float x, y, deg, sclX, sclY;

    public MapToken(int layer, float x, float y, float deg, float sclX, float sclY) {
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.deg = deg;
        this.sclX = sclX;
        this.sclY = sclY;
    }

    public MapToken(int layer, float x, float y) {
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.deg = 0;
        this.sclX = 1;
        this.sclY = 1;
    }

    public abstract void render(Renderer2D renderer2D);

}
