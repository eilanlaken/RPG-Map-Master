package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.math.MathUtils;

public class NodeCheckbox extends Node {

    private boolean checked = false;

    /* rendering - themes and dims */
    public TextureRegion imageChecked             = UserInterface.getTheme().checkboxImageChecked;
    public TextureRegion imageUnchecked           = UserInterface.getTheme().checkboxImageUnchecked;
    public Color         colorBorderChecked       = UserInterface.getTheme().checkboxColorBorderChecked.clone();
    public Color         colorBorderUnchecked     = UserInterface.getTheme().checkboxColorBorderUnchecked.clone();
    public Color         colorCheckmarkBackground = UserInterface.getTheme().checkboxColorCheckmarkBackground.clone();
    public Color         colorCheckmark           = UserInterface.getTheme().checkboxColorCheckmark.clone();
    public float         size                     = UserInterface.getTheme().checkboxSize;
    public float         sizeBorder               = UserInterface.getTheme().checkboxSizeBorder;

    public NodeCheckbox() {
        onMouseClickDefault(e -> {
            if (e.buttonLeft) flip();
        });
    }

    @Override
    protected final void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        drawBackground(renderer2D, x, y, deg, sclX, sclY);
        if (checked) drawCheckmark(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void drawBackground(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (!checked) {
            if (imageUnchecked != null) renderer2D.drawTextureRegion(imageUnchecked, x,y,deg,sclX,sclY);
            else {
                renderer2D.setColor(colorBorderUnchecked);
                renderer2D.drawRectangleBorder((size - sizeBorder), (size - sizeBorder), sizeBorder, x,y,deg,sclX,sclY);
            }

            return;
        }

        // checked
        if (imageChecked != null) renderer2D.drawTextureRegion(imageChecked, x,y,deg,sclX,sclY);
        else {
            renderer2D.setColor(colorBorderChecked);
            renderer2D.drawRectangleBorder((size - sizeBorder), (size - sizeBorder), sizeBorder, x,y,deg,sclX,sclY);
        }
    }

    protected void drawCheckmark(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (imageChecked != null) {
            renderer2D.drawTextureRegion(imageChecked, x,y,deg,sclX,sclY);
            return;
        }

        renderer2D.setColor(colorCheckmarkBackground);
        renderer2D.drawRectangleFilled((size - sizeBorder), (size - sizeBorder), x,y,deg,sclX,sclY);
        renderer2D.setColor(colorCheckmark);
        renderer2D.drawLineFilled(-9,1, -1,-7,3,x,y,deg,sclX,sclY);
        renderer2D.drawLineFilled(-1.7106f, -4.1754f, 8.2894f,5.9246f,4,x,y,deg,sclX,sclY);
    }

    @Override
    protected float getWidth() {
        if (checked) {
            return imageChecked != null ? imageChecked.originalWidth : size;
        }

        return imageUnchecked != null ? imageUnchecked.originalWidth : size;
    }

    @Override
    protected float getHeight() {
        if (checked) {
            return imageChecked != null ? imageChecked.originalHeight : size;
        }

        return imageUnchecked != null ? imageUnchecked.originalHeight : size;
    }

    public void setValue(boolean value) {
        this.checked = value;
    }

    public boolean getValue() {
        return checked;
    }

    public void flip() {
        this.checked = !this.checked;
    }

}
