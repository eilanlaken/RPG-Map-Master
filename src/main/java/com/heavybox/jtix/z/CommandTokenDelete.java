package com.heavybox.jtix.z;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

// TODO: see how layers affect this.
public class CommandTokenDelete extends Command {

    public final Enum<?> type;

    public CommandTokenDelete(final Enum<?> type, int layer, float x, float y, boolean anchor) {
        super(layer, x, y, 0, 1, 1, anchor);
        this.type = type;
    }

    @Override
    public Surface getSurface() {
        return Surface.TOKENS;
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = (JsonObject) super.serialize();
        if (type != null) json.addProperty("type", type.name());
        return json;
    }

}
