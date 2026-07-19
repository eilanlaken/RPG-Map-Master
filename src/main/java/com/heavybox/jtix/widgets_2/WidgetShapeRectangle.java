package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public final class WidgetShapeRectangle extends Widget implements WidgetShape {

    public DrawMode drawMode = DrawMode.FILLED;

    public float color = Color.randomOpaque().toFloatBits();
    public float width;
    public float height;

    public WidgetShapeRectangle(float width, float height) {
        this.width = width;
        this.height = height;
    }

    // TODO: add corners, refinement, border radius etc.
    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        renderer2D.drawRectangleFilled(width, height, x, y, deg, sclX, sclY);
    }

    @Override
    public boolean maskChildren() {
        return false;
    }

    @Override
    protected float getWidth() {
        return width;
    }

    @Override
    protected float getHeight() {
        return height;
    }

}
