package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;

public class WidgetInputTextField extends Widget implements WidgetInput<String> {

    /*** state ***/
    private String value = "";
    public float width = 200;
    public float height = 20;
    public int caretIndex = 0;
    float elapsedTime = 0;
    private boolean caretVisible = true;

    public Font textFont = Widgets.themeTextFont;
    public float textSize = height;
    public float caretBlinkSpeed = 0.5f;
    public Color backgroundColor = Color.RED.clone();
    public Color borderColor = Color.YELLOW.clone();
    public float borderSize = 2;
    public Color caretColor = Color.BLACK.clone();

    private WidgetText text = new WidgetText("");
    private WidgetShapeRectangle caret = new WidgetShapeRectangle(2, 18);

    public WidgetInputTextField() {
        text.anchor = Anchor.CENTER_LEFT;
        text.anchorX = borderSize;
        text.anchorY = 0;
        addChild(text);

        text.addChild(caret);
        caret.anchor = Anchor.CENTER_LEFT;
    }

    @Override
    protected void onMouseLeftClickDefault(Event.EventMouseLeftClick e) {
        if (!isFocused()) {
            caretIndex = value.length();
        } else { // if already focused, set caret position.

        }
    }

    @Override
    protected void onResizeDefault(Event.EventResize e) {
        // set font size
        // set caret dimensions
    }

    // TODO: add characters to the caret position
    @Override
    protected void onCodepointTypedDefault(Event.EventCodepointTyped e) {
        String str = e.codePoints.toRawString();
        text.text += str;
        caretIndex += str.length();
        // TODO: split string properly at caret position
        caret.anchorX += Renderer2D.calculateStringLineWidth(str, text.font, text.size, text.antialiasing);
    }



    @Override
    public boolean maskChildren() {
        return true;
    }


    @Override
    protected void setChildrenOffsets(final Array<Widget> children) {
        for (Widget child : children) {
            child.offsetX = (text.getWidth() - width) * 0.5f + borderSize;
            child.offsetY = 0;
        }
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
        caret.hidden = !isFocused() || !caretVisible;
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
