package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
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

}
