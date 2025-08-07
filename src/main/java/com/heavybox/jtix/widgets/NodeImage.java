package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.*;

public class NodeImage extends Node {

    private TextureRegion region;
    public float resizeX = 1;
    public float resizeY = 1;
    public Color tint = Color.WHITE.clone();

    public NodeImage(final Texture texture) {
        region = new TextureRegion(texture);
    }

    public NodeImage(TextureRegion region) {
        this.region = region;
    }

    @Override
    protected void fixedUpdate(float delta) {

    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(tint);
        renderer2D.drawTextureRegion(region, x, y, deg, sclX * resizeX, sclY * resizeY);
    }

    @Override
    public float calculateWidth() {
        return region.originalWidth * resizeX;
    }

    @Override
    public float calculateHeight() {
        return region.originalHeight * resizeY;
    }

    public void setImage(Texture image) {
        region = new TextureRegion(image);
    }

    public void setImage(TextureRegion region) {
        this.region = region;
    }

}
