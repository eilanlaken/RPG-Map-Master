package com.heavybox.jtix.z;

public class CommandTerrain extends Command {

    public final ToolDrawTerrain.Mode mode;
    public final ToolDrawTerrain.Target target;
    public final float size;

    public CommandTerrain(float x, float y, float deg, float sclX, float sclY, float size, boolean anchor, ToolDrawTerrain.Mode mode, ToolDrawTerrain.Target target) {
        super(0, x, y, deg, sclX, sclY, anchor);
        this.size = size;
        this.mode = mode;
        this.target = target;
    }

}
