package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapDecoration {

    public final int layer;
    public float x, y, deg, sclX, sclY;
    public Texture texture;

    public MapDecoration(int layer, float x, float y, float deg, float sclX, float sclY, Texture texture) {
        this.layer = layer;
        this.x = x;
        this.y = y;
        this.deg = deg;
        this.sclX = sclX;
        this.sclY = sclY;
        this.texture = texture;
    }

    public void render(Renderer2D renderer2D) {
        renderer2D.setColor(Color.WHITE);
        renderer2D.drawTexture(texture, x, y, deg, sclX, sclY);
    }

}
