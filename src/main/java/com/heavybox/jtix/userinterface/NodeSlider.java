package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

// TODO: improve and complete following the checkbox example.
public class NodeSlider extends Node {

    public boolean horizontal = true; // TODO

    public boolean integers = false;
    public float min = 0;
    public float max = 1;
    public float val = 0.5f; // sliding will change this fraction.

    /* TODO: theme. See how to make it flexible */
    public float size = 200;
    public float thickness = 7.5f;
    public float thumbSize = 18;
    public final Color colorBar = Color.GRAY.clone();
    public final Color colorThumb = Color.valueOf("0075FF");
    public final Color colorFill  = Color.valueOf("0075FF");

    public NodeSlider() {
        onMouseDragStartDefault(e -> {
            float value = 0.5f + e.mouseLocalX / size;
            setValue(value);
        });

        onMouseDragDefault(e -> {
            setValue(val + (e.mouseLocalX - e.mouseLocalXPrev) / size);
        });
    }

    @Override
    protected final void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        drawBar(renderer2D, x, y, deg, sclX, sclY);
        drawThumb(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void drawBar(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(colorBar);
        renderer2D.drawLineFilled(-size * 0.5f, 0, size * 0.5f, 0, thickness, x, y, deg, sclX, sclY);
        renderer2D.setColor(colorFill);
        renderer2D.drawLineFilled(-size * 0.5f, 0, -size * 0.5f + size * val, 0, thickness, x, y, deg, sclX, sclY);
    }

    protected void drawThumb(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        // calculate offset
        float offset_x = size * (val - 0.5f);
        float offset_y = 0;
        Vector2 offset_transformed = new Vector2(offset_x, offset_y);
        offset_transformed.scl(sclX, sclY);
        offset_transformed.rotateDeg(deg);

        renderer2D.setColor(colorThumb);
        renderer2D.drawCircleFilled(thumbSize * 0.5f,15, x + offset_transformed.x, y + offset_transformed.y, deg, sclX, sclY);
    }

    @Override
    protected float getWidth() {
        return size + thumbSize * 0.75f;
    }

    @Override
    protected float getHeight() {
        return Math.max(thickness, thumbSize);
    }

    public float getValue() {
        return min + val * (max - min);
    }

    public void setValue(float value) {
        this.val = MathUtils.clampFloat(value, 0, 1);
    }

}
