package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class Node {

    protected NodeContainer container = null;
    protected Region region = new Region();
    public Transform localTransform = new Transform();
    public float offsetX = 0; // set by container
    public float offsetY = 0; // set by container
    private Transform globalTransform = new Transform(); // calculated -
    public    boolean       active    = true;

    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    public abstract float getWidth();
    public abstract float getHeight();

}
