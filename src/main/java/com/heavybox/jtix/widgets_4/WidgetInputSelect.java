package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Renderer2D;

public class WidgetInputSelect extends Widget implements WidgetInput<String> {

    private String selected;
    private Array<String> options = new Array<>(true, 3);

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

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
    public String getValue() {
        return selected;
    }

    @Override
    public void setValue(String value) {
        this.selected = value;
    }
}
