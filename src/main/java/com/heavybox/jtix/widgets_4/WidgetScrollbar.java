package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.Vector2;

public class WidgetScrollbar extends Widget {

    /* internal state */
    public float width = 10;
    public float height = 300; // calculated
    public float value = 0.5f;
    public float thumbHeight = 50;

    /* style */
    public boolean styleDrawBar = true;
    public boolean styleDrawThumb = true;
    public Color styleBarColor = Color.valueOf("343538");
    public Color styleThumbColor = Color.valueOf("5c5d5e");

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (styleDrawBar) drawBar(renderer2D, x, y, deg, sclX, sclY);
        if (styleDrawThumb) drawThumb(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void drawBar(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(styleBarColor);
        renderer2D.drawRectangleFilled(width, height, x, y, deg, sclX, sclY);
    }

    protected void drawThumb(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        // calculate offset
        float offset_x = 0;
        //float offset_y = -height * (value + 0.5f) - thumbHeight * 0.5f;
        float offset_y = height * 0.5f - thumbHeight * 0.5f - (height - thumbHeight) * value;
        Vector2 offset_transformed = new Vector2(offset_x, offset_y);
        offset_transformed.scl(sclX, sclY);
        offset_transformed.rotateDeg(deg);
        renderer2D.setColor(1,0,0,0.2f);
        renderer2D.drawRectangleFilled(width, thumbHeight, x + offset_transformed.x, y + offset_transformed.y, deg, sclX, sclY);
    }

    // TODO
    protected void drawButtonForward(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    // TODO
    protected void drawButtonBackwards(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    @Override
    protected float getWidth() {
        return width;
    }

    @Override
    protected float getHeight() {
        return height;
    }

}
