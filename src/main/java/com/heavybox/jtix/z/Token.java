package com.heavybox.jtix.z;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.math.Transform2D;

public class Token {

    public Enum<?> tokenType;
    public Color tint = Color.WHITE;
    public int layer;
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

        if (tokenType != null) {
            json.addProperty("tokenTypeClass", tokenType.getDeclaringClass().getName());
            json.addProperty("tokenType", tokenType.name());
        }

        JsonObject tintObject = new JsonObject();
        tintObject.addProperty("r", tint.r);
        tintObject.addProperty("g", tint.g);
        tintObject.addProperty("b", tint.b);
        tintObject.addProperty("a", tint.a);
        json.add("tint", tintObject);

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

    @SuppressWarnings("unchecked")
    public static Token deserialize(JsonObject json) {
        JsonArray regionsJson = json.getAsJsonArray("regions");

        TextureRegion[] regions = new TextureRegion[regionsJson.size()];
        TexturePack pack = Assets.get("assets/texture-packs/layer_3.yml");
        for (int i = 0; i < regions.length; i++) {
            String name = regionsJson.get(i).getAsString();
            regions[i] =  pack.getRegion(name);
        }

        JsonObject transform = json.getAsJsonObject("transform");
        Token token = new Token(
                json.get("layer").getAsInt(),
                transform.get("x").getAsFloat(),
                transform.get("y").getAsFloat(),
                transform.get("deg").getAsFloat(),
                transform.get("sclX").getAsFloat(),
                transform.get("sclY").getAsFloat(),
                regions
        );

        if (json.has("tokenType")) {
            try {
                String className = json.get("tokenTypeClass").getAsString();
                String value = json.get("tokenType").getAsString();
                Class<?> clazz = Class.forName(className);
                token.tokenType = Enum.valueOf((Class<? extends Enum>) clazz, value);
            } catch (Exception e) {

            }
        }

        JsonObject tint = json.getAsJsonObject("tint");
        token.tint = new Color(
                tint.get("r").getAsFloat(),
                tint.get("g").getAsFloat(),
                tint.get("b").getAsFloat(),
                tint.get("a").getAsFloat()
        );

        return token;
    }

}
