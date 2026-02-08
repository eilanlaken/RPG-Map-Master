package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;

public class Token {

    // TODO
    public Class<? extends Tool> sourceTool;

    public Color tint = Color.WHITE;
    public Type type = Type.UNSPECIFIED; // maybe deprecate.
    public final int layer;
    public float x, y, deg, sclX, sclY;
    public final float width, height;
    public TextureRegion[] regions;

    public Token(int layer, float x, float y, float deg, float sclX, float sclY, TextureRegion... regions) {
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.deg = deg;
        this.sclX = sclX;
        this.sclY = sclY;
        this.regions = regions;

        float maxWidth = 0;
        float maxHeight = 0;
        for (TextureRegion region : regions) {
            maxWidth = Math.max(maxWidth, region.packedWidth);
            maxHeight = Math.max(maxHeight, region.packedHeight);
        }
        width = maxWidth;
        height = maxHeight;
    }

    public void render(Renderer2D renderer2D) {
        renderer2D.setColor(tint);
        for (TextureRegion region : regions) {
            renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY);
        }
        renderer2D.setColor(Color.WHITE);
    }

    public void renderPreview(Renderer2D renderer2D, float toolX, float toolY) {
        renderer2D.setColor(tint);
        for (TextureRegion region : regions) {
            renderer2D.drawTextureRegion(region, x + toolX, y + toolY, deg, sclX, sclY);
        }
        renderer2D.setColor(Color.WHITE);
    }

    public enum Type {
        UNSPECIFIED,
        TREE,
        ROCK,
        PROP,
        BLOCK,
        DECORATION,
        ;
    }

}
