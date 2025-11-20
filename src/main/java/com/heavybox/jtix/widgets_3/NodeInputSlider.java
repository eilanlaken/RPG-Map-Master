package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public class NodeInputSlider extends Node implements NodeInput<Float> {

    public float width     = 200;

    public float thumbSize = 18;
    public float barSize   = 7.5f;

    public float min = 0;
    public float max = 1;
    public float fraction = 0.5f; // sliding will change this fraction.

    protected final Color colorBar = Color.GRAY.clone();
    protected final Color colorThumb = Color.valueOf("0075FF");

    /*** LOGIC ***/
    @Override
    protected void fixedUpdate(float delta) {

    }

    @Override
    protected void frameUpdate(float delta) {

    }

    /*** RENDERING ***/
    @Override
    public void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderBar(renderer2D, x, y, deg, sclX, sclY);
        renderThumb(renderer2D, x, y, deg, sclX, sclY);
        region.render(renderer2D);
    }

    protected void renderBar(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(colorBar);
        renderer2D.drawLineFilled(-width * 0.5f, 0, width * 0.5f, 0, barSize, x, y, deg, sclX, sclY);
    }

    protected void renderThumb(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        // calculate offset
        float offset_x = width * (fraction - 0.5f);
        float offset_y = 0;
        Vector2 offset_transformed = new Vector2(offset_x, offset_y);
        offset_transformed.scl(sclX, sclY);
        offset_transformed.rotateDeg(deg);

        renderer2D.setColor(colorThumb);
        renderer2D.drawCircleFilled(thumbSize * 0.5f,10, x + offset_transformed.x, y + offset_transformed.y, deg, sclX, sclY);
    }

    @Override
    protected float getWidth() {
        return width;
    }

    @Override
    protected float getHeight() {
        return Math.max(barSize, thumbSize);
    }

    @Override
    public Float getValue() {
        return min + fraction * (max - min);
    }

    @Override
    public void setValue(Float value) {
        this.fraction = value != null ? MathUtils.clampFloat(value, 0, 1) : 0.5f;
    }
}
