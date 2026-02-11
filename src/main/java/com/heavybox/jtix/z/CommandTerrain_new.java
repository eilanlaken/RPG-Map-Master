package com.heavybox.jtix.z;

public class CommandTerrain_new extends Command {

    public ToolBrush_Terrain.Target target;
    public Tool.Mode mode;
    public int groundIndex;
    public int liquidIndex;

    public CommandTerrain_new(float x, float y, float sclX, float sclY, boolean anchor) {
        super(0, x, y,0, sclX, sclY, anchor);
    }

}
