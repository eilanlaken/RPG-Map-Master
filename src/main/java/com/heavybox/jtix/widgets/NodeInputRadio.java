package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class NodeInputRadio extends Node implements NodeInput<Boolean> {

    // state
    public boolean on = true;

    public Color borderColorUnchecked = Color.valueOf("767676");
    public Color borderColorChecked = Color.valueOf("0075FF");
    public Color checkmarkBackgroundColor = Color.valueOf("0075FF");

    public int size = 10;
    public int checkedSize = 7;

    public int borderSize = 2;


    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

        renderBorder(renderer2D, size, borderSize, x, y, deg, sclX, sclY);

        if (on) {
            renderCheckmark(renderer2D, size, checkedSize, x, y, deg, sclX, sclY);
        }
    }

    protected void renderBorder(Renderer2D renderer2D, int size, float borderSize, float x, float y, float deg, float sclX, float sclY) {
        if (on) renderer2D.setColor(borderColorChecked);
        else renderer2D.setColor(borderColorUnchecked);
        renderer2D.drawCircleBorder(size, borderSize, 22, x,y,deg,sclX,sclY);
    }

    protected void renderCheckmark(Renderer2D renderer2D, float size, float radius, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(checkmarkBackgroundColor);
        renderer2D.drawCircleFilled(checkedSize, 22, x,y,deg,sclX,sclY);
    }

    @Override
    protected void fixedUpdate(float delta) {

    }

    @Override
    public float calculateWidth() {
        return size * 2 + borderSize * 2;
    }

    @Override
    public float calculateHeight() {
        return size * 2 + borderSize * 2;
    }

    @Override
    public Boolean getValue() {
        return on;
    }

    @Override
    public void setValue(Boolean value) {
        this.on = value != null && value;
    }
}
