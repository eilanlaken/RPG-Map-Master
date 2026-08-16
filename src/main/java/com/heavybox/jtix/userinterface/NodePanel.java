package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Renderer2D;

public class NodePanel extends Node {

    /* state */

    /* settings */
    public Sizing widthSizing  = Sizing.STATIC;
    public float  width        = 100;
    public float  widthMin     = 0;
    public float  widthMax     = Float.POSITIVE_INFINITY;
    public Sizing heightSizing = Sizing.DYNAMIC;
    public float  height       = 100;
    public float  heightMin    = 0;
    public float  heightMax    = Float.POSITIVE_INFINITY;

    public Overflow overflow = Overflow.HIDDEN;

    /* theme */

    @Override
    protected boolean maskChildren() {
        return overflow == null || overflow == Overflow.HIDDEN;
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

    @Override
    protected final void onFixedUpdate(float delta) {

        onFixedUpdatePanel(delta);
    }

    protected void onFixedUpdatePanel(float delta) {

    }

    public enum Sizing {
        STATIC, // constant width
        DYNAMIC, // resize to fit children
        ;
    }

    public enum Overflow {
        HIDDEN,
        VISIBLE,
        ;
    }

}
