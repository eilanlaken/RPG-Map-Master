package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

public class NodeDebug extends Node {

    public float width = MathUtils.randomUniformFloat(50,200);
    public float height = 150;//MathUtils.randomUniformFloat(50,200);
    public Color color = Color.random();

    public NodeDebug(float x, float w) {
        this.x = x;
        this.width = w;
        color.a = 0.5f;
    }

    @Override
    protected void fixedUpdate(float delta) {

    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        renderer2D.drawRectangleFilled(width, height, x, y, deg, sclX, sclY);
    }

    @Override
    public float calculateWidth() {
        return width;
    }

    @Override
    public float calculateHeight() {
        return height;
    }

}
