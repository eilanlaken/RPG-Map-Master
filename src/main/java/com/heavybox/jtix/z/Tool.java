package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class Tool {

    public float x, y;
    public float deg;
    public float sclX = 1, sclY = 1;
    protected final Map map;

    public Tool(Map map) {
        this.map = map;
    }

    public abstract void update(float delta);
    public abstract void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);

    public abstract void activate();
    public abstract void deactivate();

}
