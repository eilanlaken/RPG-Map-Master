package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

// TODO: actually finish
public class NodeSlider_2 extends Node {

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

    NodeGraphics background;
    NodeGraphics fill;
    NodeGraphics thumb;

    public NodeSlider_2() {
        background = new NodeGraphics(imageBackground) {
            @Override
            protected boolean maskChildren() {
                return true;
            }
        };
        fill = new NodeGraphics(imageFill);
        background.connectChild(fill);
        thumb = new NodeGraphics(imageThumb);

        connectChild(background);
        connectChild(thumb);

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
//        drawBackground(renderer2D, x, y, deg, sclX, sclY);
//        drawFill(renderer2D, x, y, deg, sclX, sclY);
//
//        float offset_x = length * (value - 0.5f);
//        float offset_y = 0;
//        Vector2 offset_transformed = new Vector2(offset_x, offset_y);
//        offset_transformed.scl(sclX, sclY);
//        offset_transformed.rotateDeg(deg);
//        drawThumb(renderer2D, x + offset_transformed.x, y + offset_transformed.y, deg, sclX, sclY);
    }

    protected void drawBackground(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(colorBackground);
        renderer2D.drawLineFilled(-length * 0.5f, 0, length * 0.5f, 0, thickness, x, y, deg, sclX, sclY);
    }

    protected void drawFill(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(colorFill);
        renderer2D.drawLineFilled(-length * 0.5f, 0, -length * 0.5f + length * value, 0, thickness, x, y, deg, sclX, sclY);
    }

    protected void drawThumb(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(colorThumb);
        renderer2D.drawCircleFilled(thumbSize * 0.5f,20, x, y, deg, sclX, sclY);
    }

    @Override
    protected float getWidth() {
        return length + thumbSize * 0.75f;
    }

    @Override
    protected float getHeight() {
        return Math.max(thickness, thumbSize);
    }

    public float getValue() {
        return minimum + value * (maximum - minimum);
    }

    public void setValue(float value) {
        this.value = MathUtils.clampFloat(value, 0, 1);
    }

}
