package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class Node {

    public boolean active = true;
    protected NodeContainer container = null;
    protected Region region = new Region();
    public Transform localTransform = new Transform();

    // package private.
    float offsetX = 0; // set by container
    float offsetY = 0; // set by container
    Transform globalTransform = new Transform(); // calculated -

    protected abstract void  render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();

}
