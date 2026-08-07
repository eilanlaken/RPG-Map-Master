package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.widgets.Widgets;

public class NodeCheckbox extends Node {

    private boolean value = false;

    /* rendering - themes and dims */
    public TextureRegion imageChecked   = Widgets.themeCheckboxImageChecked;
    public TextureRegion imageUnchecked = Widgets.themeCheckboxImageUnchecked;
    public Color         colorBorderUnchecked        = Widgets.themeCheckboxBorderColorUnchecked.clone();
    public Color         colorBorderChecked          = Widgets.themeCheckboxBorderColorChecked.clone();
    public Color         colorCheckmarkBackground    = Widgets.themeCheckboxBackgroundColorCheckmark.clone();
    public Color         colorCheckmark              = Widgets.themeCheckboxColorCheckmark.clone();
    public float size         = 27;
    public float sizeBorder = 5;
    public float cornerRadius = 5;

    public NodeCheckbox() {
        onMouseClickDefault(e -> {
            if (e.buttonLeft) setValue(!value);
        });
    }

    @Override
    protected final void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        drawBackground(renderer2D, x, y, deg, sclX, sclY);
        if (value) drawCheckmark(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void drawBackground(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    protected void drawCheckmark(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    @Override
    protected float getWidth() {
        return 0;
    }

    @Override
    protected float getHeight() {
        return 0;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

}
