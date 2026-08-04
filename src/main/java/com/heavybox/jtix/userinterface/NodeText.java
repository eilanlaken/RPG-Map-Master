package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeText extends Node {

    public Color   color        = Color.WHITE.clone();
    public String  text         = "";
    public Font    font         = null;
    public int     size         = 18;
    public boolean antialiasing = true;

    public NodeText(String text) {
        this.text = text;
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        renderer2D.setFont(font);
        renderer2D.drawStringLine(text, size, antialiasing, x, y, deg, sclX, sclY);
    }

    @Override
    protected float getWidth() {
        return Renderer2D.calculateStringLineWidth(text, font, size, antialiasing);
    }

    @Override
    protected float getHeight() {
        return size;
    }

}
