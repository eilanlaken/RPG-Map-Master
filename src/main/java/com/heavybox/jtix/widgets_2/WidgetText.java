package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;

public class WidgetText extends Widget {

    public String  text         = null;
    public Color   color        = Theme.textColor;
    public Font    font         = Theme.textFont;
    public boolean antialiasing = Theme.textAntialiasing;
    public int     size         = Theme.textSize;

    @Override
    protected void renderPrimitive(Renderer2D renderer2D, Transform globalTransform) {
        renderer2D.setColor(color);
        renderer2D.setFont(font);
        renderer2D.drawStringLine(text, size, antialiasing, globalTransform.x, globalTransform.y, globalTransform.deg, globalTransform.sclX, globalTransform.sclY);
    }

    @Override
    protected float getInnerWidth() {
        return Renderer2D.calculateStringLineWidth(text, font, size, antialiasing);
    }

    @Override
    protected float getInnerHeight() {
        return size;
    }

}
