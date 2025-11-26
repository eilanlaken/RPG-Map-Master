package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class WidgetShapeLine extends Widget implements WidgetShape {

    public Type type = Type.OUTLINE;
    public float length;
    public float thickness;
    public Color color;

    public WidgetShapeLine(float length, float thickness, final Color color) {
        this.length = length;
        this.thickness = thickness;
        this.color = color.clone();
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        if (type == Type.FILLED || type == Type.BORDER) {
            renderer2D.drawLineFilled(-length * 0.5f, 0, length * 0.5f, 0, thickness,
                    x, y, deg, sclX, sclY);
            return;
        }

        if (type == Type.OUTLINE) {
            renderer2D.drawLineThin(-length * 0.5f, 0, length * 0.5f, 0,
                    x, y, deg, sclX, sclY);
            return;
        }
    }

    @Override
    protected float getWidth() {
        return length;
    }

    @Override
    protected float getHeight() {
        return thickness;
    }

}
