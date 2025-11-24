package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TextureRegion;

// TODO.
public class WidgetImage extends Widget {

    public TextureRegion img = null;
    public float width = 0;
    public float height = 0;
    public Color tint = Color.WHITE.clone();
    public boolean border = false;
    public Color borderColor = Color.WHITE.clone();
    public float borderThickness = 14;

    // TODO: implement multiple constructors
    public WidgetImage(final String src) {
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
        renderer2D.drawTextureRegion(img, x, y, deg, stretchFactorX * sclX, stretchFactorY * sclY);
        if (border) {
            renderer2D.setColor(borderColor);
            renderer2D.drawRectangleBorder(targetWidth + borderThickness, targetHeight + borderThickness, borderThickness,
                    x,y,deg,sclX,sclY);
        }
        region.render(renderer2D);

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
        float imageWidth = getImageWidth();
        return imageWidth + (border ? 2 * borderThickness : 0);
    }

    @Override
    protected float getHeight() {
        float imageHeight = getImageHeight();
        return imageHeight + (border ? 2 * borderThickness : 0);
    }

    @Override
    protected void fixedUpdate(float delta) {

    }

}
