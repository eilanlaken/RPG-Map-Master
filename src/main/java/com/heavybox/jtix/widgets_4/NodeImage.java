package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TextureRegion;

public class NodeImage extends NodeContainer {

    private final NodeImageContent imageContent;
    public TextureRegion img = null;

    public float imgWidth = 0;
    public float imgHeight = 0;
    public float zoom = 1f;
    public Color tint = Color.WHITE.clone();

    public NodeImage(final String src) {
        boolean loaded = Assets.isLoaded(src);
        Texture texture = loaded ? Assets.get(src) : new Texture(src);
        img = new TextureRegion(texture);
        this.imageContent = new NodeImageContent(this);
        init();
    }

    public NodeImage(TextureRegion region) {
        this.img = region;
        this.imageContent = new NodeImageContent(this);
        init();
    }

    private void init() {
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

    private static class NodeImageContent extends Node {

        final NodeImage container;

        public NodeImageContent(final NodeImage container) {
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
