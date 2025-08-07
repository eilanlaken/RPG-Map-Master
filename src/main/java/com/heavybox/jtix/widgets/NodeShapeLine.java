package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeShapeLine extends Node implements NodeShape {

    public float length;
    public float thickness;
    public Color color;

    public NodeShapeLine(float length, float thickness, Color color) {
        this.length = length;
        this.thickness = thickness;
        this.color = color.clone();
    }

    @Override
    protected void fixedUpdate(float delta) {

    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        renderer2D.drawLineFilled(-length * 0.5f, 0, length * 0.5f, 0, thickness,
                x, y, deg, sclX, sclY);
    }

    @Override
    public float calculateWidth() {
        return length;
    }

    @Override
    public float calculateHeight() {
        return thickness;
    }

}
