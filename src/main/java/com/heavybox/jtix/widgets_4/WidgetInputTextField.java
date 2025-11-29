package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;

public class WidgetInputTextField extends Widget implements WidgetInput<String> {

    /*** state ***/
    private String value = "";
    public float width = 200;
    public float height = 20;
    public int caretPosition = 0;
    float elapsedTime = 0;
    private boolean caretVisible = true;


    public float caretBlinkSpeed = 0.5f;
    public Color backgroundColor = Color.RED.clone();
    public Color borderColor = Color.YELLOW.clone();
    public float borderSize = 2;
    public Color caretColor = Color.BLACK.clone();

    private WidgetText text = new WidgetText("ggg");

    public WidgetInputTextField() {
        addChild(text);
    }

    @Override
    protected void onMouseLeftClickDefault(Event.EventMouseLeftClick e) {
        if (!focused) {
            focused = true;
            caretPosition = value.length();
        } else { // if already focused, set caret position.

        }
    }

    @Override
    protected void onMouseLeftClickOutsideDefault(Event.EventMouseLeftClickOutside e) {
        focused = false;
    }

    @Override
    public boolean maskChildren() {
        return true;
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(backgroundColor);
        renderer2D.drawRectangleFilled(width, height, x, y, deg, sclX, sclY);
        renderer2D.setColor(borderColor);
        renderer2D.drawRectangleBorder(width, height, borderSize, x, y, deg, sclX, sclY);
    }

    @Override
    protected void drawMask(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.drawRectangleFilled(width, height, x, y, deg, sclX, sclY);
    }

    @Override
    protected void fixedUpdate(float delta) {
        elapsedTime += delta;
        if (elapsedTime >= caretBlinkSpeed) {
            elapsedTime -= caretBlinkSpeed;
            caretVisible = !caretVisible;
        }
        text.anchor = Anchor.CENTER_LEFT;
        text.anchorX = borderSize;
        text.anchorY = 0;
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
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
