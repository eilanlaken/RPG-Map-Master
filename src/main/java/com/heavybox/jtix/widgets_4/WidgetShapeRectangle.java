package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class WidgetShapeRectangle extends Widget implements WidgetShape {

    public Type type = Type.FILLED;
    public float width;
    public float height;
    public float borderSize = 0; // default
    public Color color = Color.WHITE.clone();

    public WidgetShapeRectangle(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public WidgetShapeRectangle(float width, float height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color.clone();
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        if (type == Type.FILLED) {
            renderer2D.drawRectangleFilled(width, height, x, y, deg, sclX, sclY);
            return;
        }

        if (type == Type.OUTLINE) {
            renderer2D.drawRectangleThin(width, height, x, y, deg, sclX, sclY);
            return;
        }

        if (type == Type.BORDER) {
            renderer2D.drawRectangleBorder(width, height, borderSize, x, y, deg, sclX, sclY);
            return;
        }
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
