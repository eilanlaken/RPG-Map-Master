package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

public class WidgetInputScrollbar extends Widget implements WidgetInput<Float> {

    public float width = 10;
    public float height = 300; // calculated
    public float value = 0.0f;
    public float thumbHeight = 50;

    /* style */
    public boolean styleDrawBar = true;
    public boolean styleDrawThumb = true;
    public Color styleBarColor = Color.valueOf("343538");
    public Color styleThumbColor = Color.valueOf("5c5d5e");

    private final WidgetShapeRectangle thumb = new WidgetShapeRectangle(width, thumbHeight, styleThumbColor);

    public WidgetInputScrollbar() {
        addChild(thumb);
    }

    @Override
    protected void onMouseScrollDefault(Event.EventMouseScroll e) {
        value -= e.scrollValue * 0.1f;
        value = MathUtils.clampFloat(value, 0, 1);

        updateThumbYPosition();
    }

    public final void scroll(float amount) {
        value -= amount;
        value = MathUtils.clampFloat(value, 0, 1);
        updateThumbYPosition();
    }

    @Override
    protected void onMouseDownDefault(Event.EventMouseDown e) {
        float localY = e.mouseLocalY;

        float minY = -0.5f * height + 0.5f * thumbHeight;
        float maxY = 0.5f * height - 0.5f * thumbHeight;
        float thumbTargetY = MathUtils.clampFloat(localY, minY, maxY);
        float targetValue = (maxY - thumbTargetY) / (maxY - minY);
        targetValue = MathUtils.clampFloat(targetValue, 0, 1);

        value = MathUtils.lerp(0.2f, value, targetValue);
        value = MathUtils.clampFloat(value, 0, 1);

        updateThumbYPosition();
    }

    @Override
    protected final void fixedUpdate(float delta) {
        thumb.height = thumbHeight;
        value = MathUtils.clampFloat(value, 0, 1);

        updateThumbYPosition();
        fixedUpdateScrollbar(delta);
    }

    private void updateThumbYPosition() {
        float minY = -0.5f * height + 0.5f * thumbHeight;
        float maxY = 0.5f * height - 0.5f * thumbHeight;

        thumb.transform.y = maxY + (minY - maxY) * value;  // value 0 → maxY, value 1 → minY
    }

    protected void fixedUpdateScrollbar(float delta) {};

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (styleDrawBar) drawBar(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void drawBar(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(styleBarColor);
        renderer2D.drawRectangleFilled(width, height, x, y, deg, sclX, sclY);
    }

    @Override
    protected float getWidth() {
        return width;
    }

    @Override
    protected float getHeight() {
        return height;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public void setValue(Float value) {
        this.value = value == null ? 0 : value;
    }
}
