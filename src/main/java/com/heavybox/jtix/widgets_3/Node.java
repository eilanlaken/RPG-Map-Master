package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.graphics.Renderer2D;

public abstract class Node {

    public boolean active = true;
    protected NodeContainer container = null;
    protected final Region region = new Region();
    public Transform localTransform = new Transform();

    // set by container.
    float offsetX = 0; // set by container
    float offsetY = 0; // set by container
    Transform globalTransform = new Transform(); // calculated -

    protected abstract void  render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();

    protected abstract void fixedUpdate(float delta);
    protected abstract void frameUpdate(float delta);

    public final void update(float delta) {
        configHitZone(region);
        region.calculatePointsTransformed(globalTransform);
        fixedUpdate(delta);
    }

    protected void configHitZone(final Region region) {
        region.setToRectangle(getWidth(), getHeight());
    }

}
