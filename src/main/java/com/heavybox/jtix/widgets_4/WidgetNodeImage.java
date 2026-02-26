package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture2D;
import com.heavybox.jtix.graphics.TextureRegion;

public class WidgetNodeImage extends WidgetNodeContainerStack {

    private final WidgetNodeImageContent imageContent;
    public TextureRegion img = null;

    public float imgWidth = 0;
    public float imgHeight = 0;
    public float zoom = 1f;
    public Color tint = Color.WHITE.clone();

    public WidgetNodeImage(final String src) {
        boolean loaded = Assets.isLoaded(src);
        Texture2D texture = loaded ? Assets.get(src) : new Texture2D(src);
        img = new TextureRegion(texture);
        this.imageContent = new WidgetNodeImageContent(this);
        init();
    }

    public WidgetNodeImage(TextureRegion region) {
        this.img = region;
        this.imageContent = new WidgetNodeImageContent(this);
        init();
    }

    private void init() {
        boxBackgroundVisible = false;
        boxPaddingTop = 0;
        boxPaddingBottom = 0;
        boxPaddingRight = 0;
        boxPaddingLeft = 0;
        overflowX = Overflow.HIDDEN;
        overflowY = Overflow.HIDDEN;
        widthSizing = Sizing.STATIC;
        heightSizing = Sizing.STATIC;

        addChild(imageContent);
    }

    @Override
    public void fixedUpdateContainer(float delta) {
        widthValue = imageContent.getWidth();
        heightValue = imageContent.getHeight();
    }

    private static class WidgetNodeImageContent extends WidgetNode {

        final WidgetNodeImage container;

        public WidgetNodeImageContent(final WidgetNodeImage container) {
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
