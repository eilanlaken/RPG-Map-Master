package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Renderer2D;

public class WidgetScrollbar extends Widget {

    public float width;
    public float height;

    /* internal state */
    public float value;

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    protected void drawThumb(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    protected void drawBar(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    protected void drawForwardButton(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

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
