package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.Transform2D;

public final class WidgetShapeRectangle extends Widget implements WidgetShape {

    public DrawMode drawMode = DrawMode.FILLED;

    private float color = Color.RED.toFloatBits();
    private float width;
    private float height;

    // TODO: add corners, refinement, border radius etc.
    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        renderer2D.drawRectangleFilled(width, height, x, y, deg, sclX, sclY);
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
