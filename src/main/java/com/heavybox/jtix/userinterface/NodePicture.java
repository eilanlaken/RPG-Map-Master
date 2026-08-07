package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TextureRegion;
import org.jetbrains.annotations.NotNull;

public class NodePicture extends Node {

    public Color         tint;
    public TextureRegion region;

    public NodePicture(@NotNull TextureRegion region) {
        this.region = region;
    }

    public NodePicture(@NotNull Texture texture) {
        this.region = new TextureRegion(texture);
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(tint);
        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY);
    }

    @Override
    protected float getWidth() {
        if (region == null) return 0;
        return region.originalWidth;
    }

    @Override
    protected float getHeight() {
        if (region == null) return 0;
        return region.originalHeight;
    }

}
