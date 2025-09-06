package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.widgets.NodeContainer;

public class MapToken {

    public Type type = Type.UNSPECIFIED;
    public final int layer;
    public float x, y, deg, sclX, sclY;
    public TextureRegion[] regions;

    public MapToken(int layer, float x, float y, float deg, float sclX, float sclY, TextureRegion... regions) {
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.deg = deg;
        this.sclX = sclX;
        this.sclY = sclY;
        this.regions = regions;
    }

    public MapToken(int layer, float x, float y, TextureRegion... regions) {
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.deg = 0;
        this.sclX = 1;
        this.sclY = 1;
        this.regions = regions;
    }

    public void render(Renderer2D renderer2D) {
        for (TextureRegion region : regions) {
            renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY);
        }
    }

    public enum Type {
        UNSPECIFIED,
        TREE,
        ROCK,
        PROP,
        ;
    }

}
