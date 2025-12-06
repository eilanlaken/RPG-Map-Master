package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TextureRegion;

public class WidgetImage extends WidgetContainer {

    private final WidgetImageContent imageContent;
    public String src;
    public TextureRegion img = null;

    public float imgWidth = 110;
    public float imgHeight = 110;
    public float zoom = 1f;
    public Color tint = Color.WHITE.clone();

    public WidgetImage(final String src) {
        this.src = src;
        Texture texture = null;
        boolean loaded = Assets.isLoaded(src);
        if (loaded) {
            texture = Assets.get(src);
        } else {
            texture = new Texture(src);
        }
        img = new TextureRegion(texture);

        this.imageContent = new WidgetImageContent(this);
        layout = Layout.STACK;
        layoutAddScrollbar = false;
        boxBackgroundVisible = false;
        boxPaddingTop = 0;
        boxPaddingBottom = 0;
        boxPaddingRight = 0;
        boxPaddingLeft = 0;
        layoutOverflowX = Overflow.HIDDEN;
        layoutOverflowY = Overflow.HIDDEN;
        layoutWidthSizing = Sizing.STATIC;
        layoutHeightSizing = Sizing.STATIC;

        addChild(imageContent);
    }

    @Override
    protected void fixedUpdateContainer(float delta) {
        layoutWidth = imageContent.getWidth();
        layoutHeight = imageContent.getHeight();
    }

    private static class WidgetImageContent extends Widget {

        final WidgetImage container;


        // TODO: implement multiple constructors
        public WidgetImageContent(final WidgetImage container) {
            this.container = container;
        }

        @Override
        protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
            float targetWidth = getImageWidth();
            float targetHeight = getImageHeight();
            float stretchFactorX = targetWidth / container.img.originalWidth;
            float stretchFactorY = targetHeight / container.img.originalHeight;
            renderer2D.setColor(container.tint);
            renderer2D.drawTextureRegion(container.img, x, y, deg, container.zoom * stretchFactorX * sclX, container.zoom * stretchFactorY * sclY);
        }

        protected float getImageWidth() {
            if (container.imgWidth == 0 && container.imgHeight == 0) {
                return container.img.originalWidth;
            }
            if (container.imgWidth == 0) {
                float aspectRatio = container.img.originalWidth / container.img.originalHeight;
                return container.imgHeight * aspectRatio;
            }
            return container.imgWidth;
        }

        protected float getImageHeight() {
            if (container.imgWidth == 0 && container.imgHeight == 0) {
                return container.img.originalHeight;
            }
            if (container.imgHeight == 0) {
                float aspectRatio = container.img.originalHeight / container.img.originalWidth;
                return container.imgWidth * aspectRatio;
            }
            return container.imgHeight;
        }

        @Override
        protected float getWidth() {
            return getImageWidth();
        }

        @Override
        protected float getHeight() {
            return getImageHeight();
        }

    }
}
