package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeText extends Node {

    public String  text         = null;
    public Color   color        = Theme.textColor;
    public Font    font         = Theme.textFont;
    public boolean antialiasing = Theme.textAntialiasing;
    public int     size         = Theme.textSize;

    // TODO: max width, wrapping, etc. Currently represent a single line of text.
    // TODO: selectable, selected, selected background, ...

    public NodeText(String text) {
        this.text = text;
    }

    @Override
    protected void fixedUpdate(float delta) {
        // TODO: consider selectable text etc.
    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        renderer2D.setFont(font);
        renderer2D.drawStringLine(text, size, antialiasing, x, y, deg, sclX, sclY);
    }

    @Override
    public float calculateWidth() {
        return Renderer2D.calculateStringLineWidth(text, font, size, antialiasing);
    }

    @Override
    public float calculateHeight() {
        return size;
    }

}
