package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Renderer2D;

public class NodeCheckbox extends Node {

    private boolean value = false;

    public float size         = 27;
    public float borderSize   = 5;

    public NodeCheckbox() {
        onMouseClickDefault(e -> {
            if (e.buttonLeft) setValue(!value);
        });
    }

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

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

}
