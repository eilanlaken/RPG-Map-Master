package com.heavybox.jtix.z;

public abstract class Command {

    public float x, y, angle, sclX, sclY;
    public boolean anchor = false; // for undo-redo: a----a--a--aa------a  (every ctrl-z will rewind from anchor 'a' to the previous anchor

}
