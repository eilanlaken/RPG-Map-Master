package com.heavybox.jtix.z;

public abstract class Command {

    public final int layer;
    public float x, y, deg, sclX, sclY;
    public boolean anchor; // for undo-redo: a----a--a--aa------a  (every ctrl-z will rewind from anchor 'a' to the previous anchor

    public Command(int layer, float x, float y, float deg, float sclX, float sclY, boolean anchor) {
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.deg = deg;
        this.sclX = sclX;
        this.sclY = sclY;
        this.anchor = anchor;
    }
}
