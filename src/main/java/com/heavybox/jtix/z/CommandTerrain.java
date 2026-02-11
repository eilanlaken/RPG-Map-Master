package com.heavybox.jtix.z;

public class CommandTerrain extends Command {

    public final z_ToolBrushTerrain.Mode mode;
    public final z_ToolBrushTerrain.Target target;
    public final float size;

    public CommandTerrain(float x, float y, float deg, float sclX, float sclY, float size, boolean anchor, z_ToolBrushTerrain.Mode mode, z_ToolBrushTerrain.Target target) {
        super(0, x, y, deg, sclX, sclY, anchor);
        this.size = size;
        this.mode = mode;
        this.target = target;
    }

}
