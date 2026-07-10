package com.heavybox.jtix.z;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    public abstract Surface getSurface();

    public JsonElement serialize() {

        JsonObject json = new JsonObject();

        json.addProperty("type", getClass().getSimpleName());
        json.addProperty("layer", layer);
        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("deg", deg);
        json.addProperty("sclX", sclX);
        json.addProperty("sclY", sclY);
        json.addProperty("anchor", anchor);

        return json;
    }

    public enum Surface {
        TERRAIN,
        TOKENS,
        ;
    }

}
