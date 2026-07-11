package com.heavybox.jtix.z;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.math.Transform2D;

public class Token {

    public Enum<?> tokenType;
    public Color tint = Color.WHITE;
    public final int layer;
    public Transform2D transform;
    public final float width, height;
    public TextureRegion[] regions;

    public Token(int layer, float x, float y, float deg, float sclX, float sclY, TextureRegion... regions) {
        this.layer = layer;
        this.transform = new Transform2D(x, y, deg, sclX, sclY);
        this.regions = regions;

        float maxWidth = Float.NEGATIVE_INFINITY;
        float maxHeight = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < regions.length; i++) {
            TextureRegion region = regions[i];
            if (region == null) continue;
            maxWidth = Math.max(region.packedWidth, maxWidth);
            maxHeight = Math.max(region.packedHeight, maxHeight);
        }
        width = maxWidth;
        height = maxHeight;
    }

    public void render(Renderer2D renderer2D) {
        renderer2D.setColor(tint);
        for (int i = 0; i < regions.length; i++) {
            TextureRegion region = regions[i];
            if (region == null) continue;
            renderer2D.drawTextureRegion(region, transform.x, transform.y, transform.deg, transform.sclX, transform.sclY);
        }
        renderer2D.setColor(Color.WHITE);
    }

    public void renderPreview(Renderer2D renderer2D, float toolX, float toolY) {
        renderer2D.setColor(tint);
        for (int i = 0; i < regions.length; i++) {
            TextureRegion region = regions[i];
            if (region == null) continue;
            renderer2D.drawTextureRegion(region,
                    transform.x + toolX, transform.y + toolY,
                    transform.deg,
                    transform.sclX, transform.sclY);
        }
        renderer2D.setColor(Color.WHITE);
    }

    public float getX() {
        return transform.x;
    }
    public float getY() {
        return transform.y;
    }

    public JsonElement serialize() {
        JsonObject json = new JsonObject();

        if (tokenType != null)
            json.addProperty("tokenType", tokenType.name());

        json.addProperty("tint", tint.toFloatBits());

        json.addProperty("layer", layer);

        JsonObject transformJson = new JsonObject();
        transformJson.addProperty("x", transform.x);
        transformJson.addProperty("y", transform.y);
        transformJson.addProperty("deg", transform.deg);
        transformJson.addProperty("sclX", transform.sclX);
        transformJson.addProperty("sclY", transform.sclY);
        json.add("transform", transformJson);

        JsonArray regions = new JsonArray();
        for (TextureRegion region : this.regions) {
            TexturePack texturePack = region.texturePack;
            String regionName = texturePack.getName(region);
            if (regionName != null) regions.add(regionName);
        }
        json.add("regions", regions);

        return json;
    }

}
