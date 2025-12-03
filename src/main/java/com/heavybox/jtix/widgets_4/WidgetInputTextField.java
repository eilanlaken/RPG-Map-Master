package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Keyboard;

// TODO: handle key strokes: <-. -> , backspace, etc.
// TODO: handle style and global theme
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

    /*  built in children. */
    private final WidgetText           text  = new WidgetText("");
    private final WidgetShapeRectangle caret = new WidgetShapeRectangle(2, 18);

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
        elapsedTime = 0;
        if (!isFocused()) {
            caretIndex = value.length();
        } else { // if already focused, set caret position.

        }
    }

    @Override
    protected void onKeysJustPressedDefault(Event.EventKeysJustPressed e) {
        if (e.keys.contains(Keyboard.Key.ENTER, true)) {
            setFocused(false);
            return;
        }

        if (e.keys.contains(Keyboard.Key.BACKSPACE, true) && !value.isEmpty() && caretIndex > 0) {
            System.out.println(caretIndex);
            System.out.println(value.substring(0, caretIndex - 1));
            System.out.println(value.substring(caretIndex));
            value = value.substring(0, caretIndex - 1) + value.substring(caretIndex);
            caretIndex--;
        }
    }

    @Override
    protected void onKeysPressedDefault(Event.EventKeysPressed e) {
        // delete a character and move caret back (if not empty)
//        if (e.keys.contains(Keyboard.Key.BACKSPACE, true) && !value.isEmpty() && caretIndex > 0) {
//            System.out.println(caretIndex);
//            System.out.println(value.substring(0, caretIndex - 1));
//            System.out.println(value.substring(caretIndex));
//            value = value.substring(0, caretIndex - 1) + value.substring(caretIndex);
//            caretIndex--;
//        }
    }

    @Override
    protected void onResizeDefault(Event.EventResize e) {
        // set font size
        // set caret dimensions
    }

    // TODO: add characters to the caret position
    @Override
    protected void onCodepointsTypedDefault(Event.EventCodepointsTyped e) {
        String str = e.codePoints.toRawString();
        value += str;
        caretIndex += str.length();
        // TODO: split string properly at caret position
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
        caret.hidden = !isFocused() || !caretVisible;
        caret.anchorX = Renderer2D.calculateStringLineWidth(value, 0, caretIndex, text.font, text.size, text.antialiasing);
        text.text = getValue();
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
