package com.heavybox.jtix.graphics;

// TODO: change name to Texture2DRegion
public class TextureRegion {

    public final Texture texture; // TODO: this will be Texture2D

    public final float x;
    public final float y;
    public final float offsetX;
    public final float offsetY;
    public final float packedWidth;
    public final float packedHeight;
    public final float originalWidth;
    public final float originalHeight;
    public final float packedWidthHalf;
    public final float packedHeightHalf;
    public final float originalWidthHalf;
    public final float originalHeightHalf;
    public final float u1;
    public final float v1;
    public final float u2;
    public final float v2;

    public TextureRegion(Texture texture) {
        this.texture = texture;
        this.x = 0;
        this.y = 0;
        this.offsetX = 0;
        this.offsetY = 0;
        this.packedWidth = this.texture.width;
        this.packedHeight = this.texture.height;
        this.originalWidth = this.texture.width;
        this.originalHeight = this.texture.height;
        this.packedWidthHalf = packedWidth * 0.5f;
        this.packedHeightHalf = packedHeight * 0.5f;
        this.originalWidthHalf = originalWidth * 0.5f;
        this.originalHeightHalf = originalHeight * 0.5f;
        this.u1 = 0;
        this.v1 = 0;
        this.u2 = 1;
        this.v2 = 1;
    }

    public TextureRegion(final Texture texture,
                  int x, int y, int offsetX, int offsetY,
                  int packedWidth, int packedHeight, int originalWidth, int originalHeight) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.packedWidth = packedWidth;
        this.packedHeight = packedHeight;
        this.originalWidth = originalWidth;
        this.originalHeight = originalHeight;
        this.packedWidthHalf = packedWidth * 0.5f;
        this.packedHeightHalf = packedHeight * 0.5f;
        this.originalWidthHalf = originalWidth * 0.5f;
        this.originalHeightHalf = originalHeight * 0.5f;
        float invTexWidth = 1.0f / (float) this.texture.width;
        float invTexHeight = 1.0f / (float) this.texture.height;
        float u1 = (float) x * invTexWidth;
        float v1 = (float) y * invTexHeight;
        float u2 = (float) (x + packedWidth) * invTexWidth;
        float v2 = (float) (y + packedHeight) * invTexHeight;
        if (this.packedWidth == 1 && this.packedHeight == 1) {
            float adjustX = 0.25f / (float) texture.width;
            u1 += adjustX;
            u2 -= adjustX;
            float adjustY = 0.25f / (float) texture.height;
            v1 += adjustY;
            v2 -= adjustY;
        }
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
    }

}
