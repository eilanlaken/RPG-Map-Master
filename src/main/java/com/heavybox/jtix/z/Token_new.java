package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TextureRegion;

public class Token_new {

    public Enum<?> tokenType;
    public Color tint = Color.WHITE;
    public final int layer;
    public final float minY;
    public TokenTransform[] transforms;
    public final float width, height;
    public TextureRegion[] regions;

    public Token_new(int layer, float x, float y, float deg, float sclX, float sclY, TextureRegion... regions) {
        this.layer = layer;
        this.transforms = new TokenTransform[regions.length];
        for (int i = 0; i < transforms.length; i++) {
            this.transforms[i].x = x;
            this.transforms[i].y = y;
            this.transforms[i].deg = deg;
            this.transforms[i].sclX = sclX;
            this.transforms[i].sclY = sclY;
        }

        this.regions = regions;

        float minY = Float.POSITIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float minX = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float minimumY = Float.POSITIVE_INFINITY;
        for (int i = 0; i < transforms.length; i++) {
            TextureRegion region = regions[i];
            if (region == null) continue;
            maxX = Math.max(maxX, transforms[i].x + 0.5f * region.packedWidth * Math.abs(transforms[i].sclX));
            minX = Math.min(minX, transforms[i].x - 0.5f * region.packedWidth * Math.abs(transforms[i].sclX));
            maxY = Math.max(maxY, transforms[i].y + 0.5f * region.packedHeight * Math.abs(transforms[i].sclY));
            minY = Math.min(minY, transforms[i].y - 0.5f * region.packedHeight * Math.abs(transforms[i].sclY));
            minimumY = Math.min(minimumY, transforms[i].y);
        }
        this.minY = minimumY;
        width = maxX - minX;
        height = maxY - minY;
    }

    public Token_new(int layer, TokenTransform[] transforms, TextureRegion[] regions) {
        this.layer = layer;
        this.transforms = transforms;
        this.regions = regions;

        float minY = Float.POSITIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float minX = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float minimumY = Float.POSITIVE_INFINITY;
        for (int i = 0; i < transforms.length; i++) {
            TextureRegion region = regions[i];
            if (region == null) continue;
            maxX = Math.max(maxX, transforms[i].x + 0.5f * region.packedWidth * Math.abs(transforms[i].sclX));
            minX = Math.min(minX, transforms[i].x - 0.5f * region.packedWidth * Math.abs(transforms[i].sclX));
            maxY = Math.max(maxY, transforms[i].y + 0.5f * region.packedHeight * Math.abs(transforms[i].sclY));
            minY = Math.min(minY, transforms[i].y - 0.5f * region.packedHeight * Math.abs(transforms[i].sclY));
            minimumY = Math.min(minimumY, transforms[i].y);
        }
        this.minY = minimumY;
        width = maxX - minX;
        height = maxY - minY;
    }

    public void render(Renderer2D renderer2D) {
        renderer2D.setColor(tint);
        for (int i = 0; i < regions.length; i++) {
            TextureRegion region = regions[i];
            if (region == null) continue;
            renderer2D.drawTextureRegion(region, transforms[i].x, transforms[i].y, transforms[i].deg, transforms[i].sclX, transforms[i].sclY);
        }
        renderer2D.setColor(Color.WHITE);
    }

    public void renderPreview(Renderer2D renderer2D, float toolX, float toolY) {
        renderer2D.setColor(tint);
        for (int i = 0; i < regions.length; i++) {
            TextureRegion region = regions[i];
            if (region == null) continue;
            renderer2D.drawTextureRegion(region,
                    transforms[i].x + toolX, transforms[i].y + toolY,
                    transforms[i].deg,
                    transforms[i].sclX, transforms[i].sclY);
        }
        renderer2D.setColor(Color.WHITE);
    }

}
