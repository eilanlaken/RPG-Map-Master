package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.z.tools_new.Tool_1_Terrain;
import com.heavybox.jtix.z.tools_new.Tool_new;

public class CommandTerrain extends Command {

    public float tint = Color.WHITE.toFloatBits();
    public Tool_1_Terrain.Target target;
    public Tool_new.Mode mode;
    public int groundIndex;
    public int liquidIndex;
    public int brushIndex;

    public CommandTerrain(float x, float y, float deg, float sclX, float sclY, boolean anchor) {
        super(0, x, y,deg, sclX, sclY, anchor);
    }

    @Override
    public Surface getSurface() {
        return Surface.TERRAIN;
    }
}
