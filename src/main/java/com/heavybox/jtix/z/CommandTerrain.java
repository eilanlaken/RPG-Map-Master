package com.heavybox.jtix.z;

public class CommandTerrain extends Command {

    public final ToolTerrain.Mode mode;

    public CommandTerrain(float x, float y, float deg, float sclX, float sclY, boolean anchor, ToolTerrain.Mode mode) {
        super(0, x, y, deg, sclX, sclY, anchor);
        this.mode = mode;
    }

}
