package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;

public class CommandCreateWheatField extends Command {

    public float[] polygon;
    public int baseType;
    public boolean addLines;
    public float linesAngle;
    @Deprecated public int harvestType;
    @Deprecated public Color harvestTint;

    public CommandCreateWheatField(float x, float y, float deg, float sclX, float sclY, boolean anchor) {
        super(1, x, y, deg, sclX, sclY, anchor);
    }

}
