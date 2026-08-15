package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

// TODO: actually finish
public class NodeSlider extends Node {

    /* state and data */
    public boolean integers = false;
    public float   minimum  = 0;
    public float   maximum  = 1;
    public float   value    = 0.5f; // sliding will change this fraction.

    /* TODO: theme. See how to make it flexible */
    public TextureRegion imageBackground = UserInterface.getTheme().sliderImageBackground;
    public TextureRegion imageFill       = UserInterface.getTheme().sliderImageFill;
    public TextureRegion imageThumb      = UserInterface.getTheme().sliderImageThumb;
    public float         length          = UserInterface.getTheme().sliderLength;
    public float         thickness       = UserInterface.getTheme().sliderThickness;
    public float         thumbSize       = UserInterface.getTheme().sliderThumbSize;
    public Color         colorBackground = UserInterface.getTheme().sliderColorBackground.clone();
    public Color         colorThumb      = UserInterface.getTheme().sliderColorThumb.clone();
    public Color         colorFill       = UserInterface.getTheme().sliderColorFill.clone();

    public NodeSlider() {
        onMouseDragStartDefault(e -> {
            float value = 0.5f + e.mouseLocalX / length;
            setValue(value);
        });

        onMouseDragDefault(e -> {
            setValue(value + (e.mouseLocalX - e.mouseLocalXPrev) / length);
        });
    }

    @Override
    protected final void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        drawBackground(renderer2D, x, y, deg, sclX, sclY);
        drawFill(renderer2D, x, y, deg, sclX, sclY);

        float offset_x = length * (value - 0.5f);
        float offset_y = 0;
        Vector2 offset_transformed = new Vector2(offset_x, offset_y);
        offset_transformed.scl(sclX, sclY);
        offset_transformed.rotateDeg(deg);
        drawThumb(renderer2D, x + offset_transformed.x, y + offset_transformed.y, deg, sclX, sclY);
    }

    protected void drawBackground(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (imageBackground == null) {
            renderer2D.setColor(colorBackground);
            renderer2D.drawLineFilled(-length * 0.5f, 0, length * 0.5f, 0, thickness, x, y, deg, sclX, sclY);
            return;
        }

        renderer2D.drawTextureRegion(imageBackground, x, y, deg, sclX, sclY);
    }

    protected void drawFill(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (imageFill == null) {
            renderer2D.setColor(colorFill);
            renderer2D.drawLineFilled(-length * 0.5f, 0, -length * 0.5f + length * value, 0, thickness, x, y, deg, sclX, sclY);
            return;
        }

        renderer2D.drawTextureRegion(
                imageFill,
                0f, 0f,
                value, 1f,
                x, y, deg,
                sclX, sclY
        );
    }

    protected void drawThumb(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (imageThumb == null) {
            renderer2D.setColor(colorThumb);
            renderer2D.drawCircleFilled(thumbSize * 0.5f, 20, x, y, deg, sclX, sclY);
            return;
        }

        renderer2D.drawTextureRegion(imageThumb, x, y, deg, sclX, sclY);
    }

    @Override
    protected float getWidth() {
        return imageBackground != null ? imageBackground.packedWidth : length;
        //return length + thumbSize * 0.75f;
    }

    @Override
    protected float getHeight() {
        float backgroundHeight = imageBackground != null ? imageBackground.packedHeight : thickness;
        float fillHeight = imageFill != null ? imageFill.packedHeight : thickness;
        float thumbHeight = imageThumb != null ? imageThumb.packedHeight : thumbSize;
        return Math.max(backgroundHeight, Math.max(fillHeight, thumbHeight));
    }

    public float getValue() {
        return minimum + value * (maximum - minimum);
    }
    public void setValue(float value) {
        this.value = MathUtils.clampFloat(value, 0, 1);
    }

}
