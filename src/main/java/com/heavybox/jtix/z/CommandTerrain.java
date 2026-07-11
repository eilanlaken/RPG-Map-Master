package com.heavybox.jtix.z;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
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

    @Override
    public JsonElement serialize() {
        JsonObject json = (JsonObject) super.serialize();

        json.addProperty("tint", tint);
        if (target != null) json.addProperty("target", target.name());
        if (mode != null) json.addProperty("mode", mode.name());
        json.addProperty("groundIndex", groundIndex);
        json.addProperty("liquidIndex", liquidIndex);
        json.addProperty("brushIndex", brushIndex);

        return json;
    }

}
