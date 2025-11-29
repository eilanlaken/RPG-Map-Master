package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public class WidgetInputSlider extends Widget implements WidgetInput<Float> {

    public float width     = 200;
    public float thickness = 7.5f;
    public float thumbSize = 18;

    public float min = 0;
    public float max = 1;
    public float val = 0.5f; // sliding will change this fraction.

    protected final Color colorBar = Color.GRAY.clone();
    protected final Color colorThumb = Color.valueOf("0075FF");
    protected final Color colorFill  = Color.valueOf("0075FF");

    /*** DEFAULT INPUT HANDLERS ***/
    @Override
    protected void onMouseLeftClickDefault(Event.EventMouseLeftClick e) {
        float value = 0.5f + e.mouseLocalX / width;
        setValue(value);
    }

    /*** RENDERING ***/
    @Override
    public void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        drawBar(renderer2D, x, y, deg, sclX, sclY);
        drawThumb(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void drawBar(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(colorBar);
        renderer2D.drawLineFilled(-width * 0.5f, 0, width * 0.5f, 0, thickness, x, y, deg, sclX, sclY);

        renderer2D.setColor(colorFill);
        renderer2D.drawLineFilled(-width * 0.5f, 0, -width * 0.5f + width * val, 0, thickness, x, y, deg, sclX, sclY);
    }

    protected void drawThumb(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        // calculate offset
        float offset_x = width * (val - 0.5f);
        float offset_y = 0;
        Vector2 offset_transformed = new Vector2(offset_x, offset_y);
        offset_transformed.scl(sclX, sclY);
        offset_transformed.rotateDeg(deg);

        renderer2D.setColor(colorThumb);
        renderer2D.drawCircleFilled(thumbSize * 0.5f,10, x + offset_transformed.x, y + offset_transformed.y, deg, sclX, sclY);
    }

    @Override
    protected float getWidth() {
        return width + thumbSize * 0.75f;
    }

    @Override
    protected float getHeight() {
        return Math.max(thickness, thumbSize);
    }

    @Override
    public Float getValue() {
        return min + val * (max - min);
    }

    @Override
    public void setValue(Float value) {
        this.val = value != null ? MathUtils.clampFloat(value, 0, 1) : 0.5f;
    }
}
