package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeText extends Node {

    /* state */
    public String  text         = null;

    /* props */
    public Color   color        = Widgets.themeTextColor;
    public Font    font         = Widgets.themeTextFont;
    public boolean antialiasing = Widgets.themeTextAntialiasing;
    public int     size         = Widgets.themeTextSize;

    public NodeText(String text) {
        this.text = text;
    }

    /*** RENDERING ***/
    @Override
    public void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        renderer2D.setFont(font);
        renderer2D.drawStringLine(text, size, antialiasing, x, y, deg, sclX, sclY);
    }

    @Override
    public float getWidth() {
        return Renderer2D.calculateStringLineWidth(text, font, size, antialiasing);
    }

    @Override
    public float getHeight() {
        return size;
    }
}
