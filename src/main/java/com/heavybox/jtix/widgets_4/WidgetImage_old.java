package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TextureRegion;

// TODO.
public class WidgetImage_old extends Widget {

    public TextureRegion img = null;
    public float zoom = 3;
    public float width = 0;
    public float height = 0;
    public Color tint = Color.WHITE.clone();
    public Color borderColor = Color.WHITE.clone();
    public float borderThickness = 14;

    // TODO: implement multiple constructors
    public WidgetImage_old(final String src) {
        Texture texture = null;
        boolean loaded = Assets.isLoaded(src);
        if (loaded) {
            texture = Assets.get(src);
        } else {
            texture = new Texture(src);
        }
        img = new TextureRegion(texture);
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        float targetWidth = getImageWidth();
        float targetHeight = getImageHeight();
        float stretchFactorX = targetWidth / img.originalWidth;
        float stretchFactorY = targetHeight / img.originalHeight;
        renderer2D.setColor(tint);
        renderer2D.drawTextureRegion(img, x, y, deg, zoom * stretchFactorX * sclX, zoom * stretchFactorY * sclY);
        if (borderThickness > 0) {
            renderer2D.setColor(borderColor);
            renderer2D.drawRectangleBorder(targetWidth + borderThickness, targetHeight + borderThickness, borderThickness,
                    x,y,deg,sclX,sclY);
        }
    }

    protected float getImageWidth() {
        if (width == 0 && height == 0) {
            return img.originalWidth;
        }
        if (width == 0) {
            float aspectRatio = img.originalWidth / img.originalHeight;
            return height * aspectRatio;
        }
        return width;
    }

    protected float getImageHeight() {
        if (width == 0 && height == 0) {
            return img.originalHeight;
        }
        if (height == 0) {
            float aspectRatio = img.originalHeight / img.originalWidth;
            return width * aspectRatio;
        }
        return height;
    }

    @Override
    protected float getWidth() {
        return getImageWidth() + 2 * borderThickness;
    }

    @Override
    protected float getHeight() {
        return getImageHeight() + 2 * borderThickness;
    }

}
