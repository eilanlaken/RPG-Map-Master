package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import org.jetbrains.annotations.NotNull;

// TODO: add angle parameter <- improve renderer 2d to include drawCircleThin + drawCircleBorder with 'angle' parameter
public class WidgetNodeShapeCircle extends WidgetNode implements WidgetNodeShape {

    // TODO: add angle
    public float arcDeg = 360;
    public Type type = Type.FILLED;
    public float radius;
    public int refinement;
    public Color color;
    public float borderSize;

    public WidgetNodeShapeCircle(float radius, int refinement, final Color color) {
        this.radius = radius;
        this.refinement = refinement;
        this.color = color.clone();
        this.borderSize = radius * 0.05f;
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        if (type == Type.FILLED) {
            renderer2D.drawCircleFilled(radius, refinement, arcDeg, x, y, deg, sclX, sclY);
            return;
        }
        if (type == Type.OUTLINE) {
            renderer2D.drawCircleThin(radius, refinement, arcDeg, x, y, deg, sclX, sclY);
            return;
        }
        if (type == Type.BORDER) {
            renderer2D.drawCircleBorder(radius, borderSize, arcDeg, refinement, x, y, deg, sclX, sclY);
            return;
        }

    }

    @Override
    protected void configureInputRegion(@NotNull Region region) {
        region.setToCircle(radius, refinement);
    }

    @Override
    protected float getWidth() {
        return 2 * radius;
    }

    @Override
    protected float getHeight() {
        return 2 * radius;
    }

}
