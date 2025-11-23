package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TextureRegion;

// TODO.
public class WidgetImage extends Widget {

    public TextureRegion region = null;
    public float width = 0;
    public float height = 0;
    public Color tint = Color.WHITE.clone();
    public boolean border = false;
    public Color borderColor = Color.WHITE.clone();
    public float borderThickness = 4;

    // TODO: implement multiple constructors
    public WidgetImage(final String src) {
        Texture texture = null;
        boolean loaded = Assets.isLoaded(src);
        if (loaded) {
            texture = Assets.get(src);
        } else {
            texture = new Texture(src);
        }
        region = new TextureRegion(texture);
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        float targetWidth = getWidth();
        float targetHeight = getHeight();
        float stretchFactorX = targetWidth / region.originalWidth;
        float stretchFactorY = targetHeight / region.originalHeight;
        renderer2D.setColor(tint);
        renderer2D.drawTextureRegion(region, x, y, deg, stretchFactorX * sclX, stretchFactorY * sclY);
    }

    @Override
    protected float getWidth() {
        if (width == 0 && height == 0) {
            return region.originalWidth;
        }
        if (width == 0) {
            float aspectRatio = region.originalWidth / region.originalHeight;
            return height * aspectRatio;
        }
        return width;
    }

    @Override
    protected float getHeight() {
        if (width == 0 && height == 0) {
            return region.originalHeight;
        }
        if (height == 0) {
            float aspectRatio = region.originalHeight / region.originalWidth;
            return width * aspectRatio;
        }
        return height;
    }

    @Override
    protected void fixedUpdate(float delta) {

    }

}
