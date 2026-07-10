package com.heavybox.jtix.z;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

import java.util.Arrays;
import java.util.Objects;

public class CommandTokenCreate extends Command {

    public Color tint = Color.WHITE;
    public Enum<?> tokenType;
    public TextureRegion[] regions;

    public CommandTokenCreate(int layer, float x, float y, float deg, float sclX, float sclY, boolean anchor, TextureRegion... regions) {
        super(layer, x, y, deg, sclX, sclY, anchor);
        this.regions = Arrays.stream(regions).filter(Objects::nonNull).toArray(TextureRegion[]::new);
    }

    @Override
    public Surface getSurface() {
        return Surface.TOKENS;
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = (JsonObject) super.serialize();

        if (tokenType != null) json.addProperty("tokenType", tokenType.name());

        JsonArray regionsArray = new JsonArray();
        for (TextureRegion region : regions) {
            TexturePack texturePack = region.texturePack;
            String regionName = texturePack.getName(region);
            if (regionName != null) regionsArray.add(regionName);
        }
        json.add("regions", regionsArray);

        JsonObject tintJson = new JsonObject();
        tintJson.addProperty("r", tint.r);
        tintJson.addProperty("g", tint.g);
        tintJson.addProperty("b", tint.b);
        tintJson.addProperty("a", tint.a);
        json.add("tint", tintJson);

        return json;
    }

}
