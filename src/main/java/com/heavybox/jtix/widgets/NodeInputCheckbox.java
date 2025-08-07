package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

public class NodeInputCheckbox extends Node implements NodeInput<Boolean> {

    private final float BASE_SIZE = 26;

    // state
    public boolean checked = true;

    public Color borderColorUnchecked = Color.valueOf("767676");
    public Color borderColorChecked = Color.valueOf("0075FF");
    public Color checkmarkBackgroundColor = Color.valueOf("0075FF");
    public Color checkmarkColor = Color.WHITE.clone();

    public float size = 1;
    public float borderSize = 2;


    @Override
    protected void fixedUpdate(float delta) {

    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        float cornerRadius = 5;
        float radius = MathUtils.clampFloat(cornerRadius, 0, BASE_SIZE * 0.5f);

        if (checked) renderer2D.setColor(borderColorChecked);
        else renderer2D.setColor(borderColorUnchecked);
        renderer2D.drawRectangleBorder(BASE_SIZE, BASE_SIZE, borderSize,
                radius, 6,
                radius, 6,
                radius, 6,
                radius, 6,
                x,y,deg,size * sclX,size * sclY);

        if (checked) {
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
    }

    protected void renderBox(Renderer2D renderer2D, float size, float borderSize, float radius, float x, float y, float deg, float sclX, float sclY) {


    }

    protected void renderCheckmark(Renderer2D renderer2D, float size, float radius, float x, float y, float deg, float sclX, float sclY) {

    }

    @Override
    public float calculateWidth() {
        return size * BASE_SIZE + borderSize * 2;
    }

    @Override
    public float calculateHeight() {
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
