package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import org.jetbrains.annotations.NotNull;

public abstract class Node {

    public boolean active = true;
    protected NodeContainer container = null;
    protected final Region region = new Region();
    public Transform localTransform = new Transform();

    // set by container.
    float offsetX = 0; // set by container
    float offsetY = 0; // set by container
    Transform globalTransform = new Transform(); // calculated -

    public final void render(Renderer2D renderer2D) {
        draw(renderer2D, globalTransform.x, globalTransform.y, globalTransform.deg, globalTransform.sclX, globalTransform.sclY);
    }

    protected abstract void  draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();

    protected abstract void fixedUpdate(float delta);
    protected abstract void frameUpdate(float delta);

    public final void update(float delta) {
        calculateGlobalTransform();
        setRegionShape(region);
        region.transform(globalTransform);
        fixedUpdate(delta);
    }

    protected void setRegionShape(final @NotNull Region region) {
        region.setToRectangle(getWidth(), getHeight());
    }

    private void calculateGlobalTransform() {
        float refX = container == null ? 0 : container.globalTransform.x;
        float refY = container == null ? 0 : container.globalTransform.y;
        float refDeg = container == null ? 0 : container.globalTransform.deg;
        float refSclX = container == null ? 1 : container.globalTransform.sclX;
        float refSclY = container == null ? 1 : container.globalTransform.sclY;
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = this.localTransform.x * cos - this.localTransform.y * sin;
        float y = this.localTransform.x * sin + this.localTransform.y * cos;
        globalTransform.x = refX + x * refSclX + offsetX * cos - offsetY * sin; // add the rotated offset vector x component
        globalTransform.y = refY + y * refSclY + offsetX * sin + offsetY * cos; // add the rotated offset vector y component
        globalTransform.deg  = localTransform.deg + refDeg;
        globalTransform.sclX = localTransform.sclX * refSclX;
        globalTransform.sclY = localTransform.sclY * refSclY;
    }

}
