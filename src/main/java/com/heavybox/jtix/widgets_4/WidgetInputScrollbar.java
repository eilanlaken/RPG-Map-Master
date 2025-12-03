package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import org.jetbrains.annotations.NotNull;

// TODO: make the thumb draggable
// TODO: support both vertical and horizontal scrollbars
public class WidgetInputScrollbar extends Widget implements WidgetInput<Float> {

    public @NotNull Type type = Type.VERTICAL;

    public float thickness = 10;
    public float length = 300; // calculated
    public float value = 0.0f;
    public float thumbLength = 50;

    /* style */
    public boolean styleDrawBar = true;
    public boolean styleDrawThumb = true;
    public Color styleBarColor = Color.valueOf("343538");
    public Color styleThumbColor = Color.valueOf("5c5d5e");

    private final WidgetShapeRectangle thumb = new WidgetShapeRectangle(thickness, thumbLength, styleThumbColor);

    public WidgetInputScrollbar() {
        addChild(thumb);
    }

    @Override
    protected void onMouseScrollDefault(Event.EventMouseScroll e) {
        value -= e.scrollValue * 0.1f;
        value = MathUtils.clampFloat(value, 0, 1);

        updateThumbPosition();
    }

    public final void scroll(float amount) {
        value -= amount;
        value = MathUtils.clampFloat(value, 0, 1);
        updateThumbPosition();
    }

    @Override
    protected void onMouseDownDefault(Event.EventMouseDown e) {
        float local = type == Type.VERTICAL ? e.mouseLocalY : e.mouseLocalX;

        float min = -0.5f * length + 0.5f * thumbLength;
        float max = 0.5f * length - 0.5f * thumbLength;
        if (type == Type.HORIZONTAL) { // swap min <-> max
            float tmp = max;
            max = min;
            min = tmp;
        }

        float thumbTarget = MathUtils.clampFloat(local, min, max);
        float targetValue = (max - thumbTarget) / (max - min);
        targetValue = MathUtils.clampFloat(targetValue, 0, 1);

        value = MathUtils.lerp(0.2f, value, targetValue);
        value = MathUtils.clampFloat(value, 0, 1);

        updateThumbPosition();
    }

    @Override
    protected final void fixedUpdate(float delta) {
        if (type == Type.VERTICAL) {
            thumb.height = thumbLength;
            thumb.width = thickness;
        } else {
            thumb.height = thickness;
            thumb.width = thumbLength;
        }
        value = MathUtils.clampFloat(value, 0, 1);

        updateThumbPosition();
        fixedUpdateScrollbar(delta);
    }

    private void updateThumbPosition() {
        float min = -0.5f * length + 0.5f * thumbLength;
        float max = 0.5f * length - 0.5f * thumbLength;
        if (type == Type.HORIZONTAL) { // swap min <-> max
            float tmp = max;
            max = min;
            min = tmp;
        }
        if (type == Type.VERTICAL) {
            thumb.transform.y = max + (min - max) * value;  // value 0 → maxY, value 1 → minY
        } else {
            thumb.transform.x = max + (min - max) * value;  // value 0 → maxY, value 1 → minY
        }
    }

    protected void fixedUpdateScrollbar(float delta) {};

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (styleDrawBar) drawBar(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void drawBar(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(styleBarColor);
        if (type == Type.VERTICAL) {
            renderer2D.drawRectangleFilled(thickness, length, x, y, deg, sclX, sclY);
        } else {
            renderer2D.drawRectangleFilled(length, thickness, x, y, deg, sclX, sclY);
        }
    }

    @Override
    protected float getWidth() {
        return type == Type.VERTICAL ? thickness : length;
    }

    @Override
    protected float getHeight() {
        return type == Type.VERTICAL ? length : thickness;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public void setValue(Float value) {
        this.value = value == null ? 0 : value;
    }

    public enum Type {
        VERTICAL,
        HORIZONTAL,
        ;
    }
}
