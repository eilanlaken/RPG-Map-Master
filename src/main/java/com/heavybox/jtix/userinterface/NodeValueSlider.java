package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

public class NodeValueSlider extends Node implements NodeValue<Float> {

    public float min = 0;
    public float max = 1;
    public float val = 0.5f; // sliding will change this fraction.

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    protected void drawBar(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    protected void drawThumb(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    @Override
    protected float getWidth() {
        return 0;
    }

    @Override
    protected float getHeight() {
        return 0;
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
