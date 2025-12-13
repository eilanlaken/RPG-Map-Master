package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.math.MathUtils;

public class WidgetNodeInputCheckbox extends WidgetNode implements WidgetNodeInput<Boolean> {

    private static final float BASE_SIZE = 26;

    /* value */
    public boolean checked = true;

    /* rendering - colors */
    // TODO: get from theme.
    // TODO: add images set to global theme
    public static TextureRegion themeCheckboxUnchecked = Widgets.themeCheckboxUnchecked;
    public static TextureRegion themeCheckboxChecked   = Widgets.themeCheckboxChecked;
    public final Color borderColorUnchecked            = Widgets.themeCheckboxBorderColorUnchecked.clone();
    public final Color borderColorChecked              = Widgets.themeCheckboxBorderColorChecked.clone();
    public final Color checkmarkBackgroundColor        = Widgets.themeCheckboxBackgroundColorCheckmark.clone();
    public final Color checkmarkColor                  = Widgets.themeCheckboxColorCheckmark.clone();

    /* rendering - dimensions */
    // TODO: get from theme.
    public float size         = 1;
    public float borderSize   = 5;
    public float cornerRadius = 5;

    /*** DEFAULT INPUT HANDLERS ***/
    @Override
    protected boolean onMouseLeftClickDefault(Event.EventMouseLeftClick e) {
        setValue(!getValue());
        return true;
    }


    /*** RENDERING ***/
    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        drawBox(renderer2D, x, y, deg, sclX, sclY);
        if (checked) drawCheckmark(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void drawBox(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (themeCheckboxUnchecked != null) {
            renderer2D.drawTextureRegion(themeCheckboxUnchecked, x,y,deg,size * sclX,size * sclY);
            return;
        }

        float radius = MathUtils.clampFloat(cornerRadius, 0, BASE_SIZE * 0.5f);
        renderer2D.setColor(checked ? borderColorChecked : borderColorUnchecked);
        renderer2D.drawRectangleBorder(BASE_SIZE, BASE_SIZE, borderSize,
                radius, 6,
                radius, 6,
                radius, 6,
                radius, 6,
                x,y,deg,size * sclX,size * sclY);

    }

    protected void drawCheckmark(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (themeCheckboxChecked != null) {
            renderer2D.drawTextureRegion(themeCheckboxChecked, x,y,deg,size * sclX,size * sclY);
            return;
        }

        float radius = MathUtils.clampFloat(cornerRadius, 0, BASE_SIZE * 0.5f);
        renderer2D.setColor(checkmarkBackgroundColor);
        renderer2D.drawRectangleFilled(BASE_SIZE, BASE_SIZE,
                radius, 6,
                radius, 6,
                radius, 6,
                radius, 6,
                x,y,deg,size * sclX,size * sclY);

        renderer2D.setColor(checkmarkColor);
        renderer2D.drawLineFilled(-9,1, -1,-7,3,x,y,deg,size * sclX,size * sclY);
        renderer2D.drawLineFilled(-1.7106f, -4.1754f, 8.2894f,5.9246f,4,x,y,deg,size * sclX,size * sclY);
    }

    @Override
    public float getWidth() {
        return size * BASE_SIZE + borderSize * 2;
    }

    @Override
    public float getHeight() {
        return size * BASE_SIZE + borderSize * 2;
    }

    @Override
    public Boolean getValue() {
        return checked;
    }

    @Override
    public void setValue(Boolean value) {
        this.checked = value != null && value;
    }
}
