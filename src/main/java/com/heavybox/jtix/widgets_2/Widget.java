package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.InputEventHandler;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Shape2DPolygon;
import com.heavybox.jtix.math.Transform2D;
import org.jetbrains.annotations.NotNull;

public abstract class Widget implements InputEventHandler {

    public int inputLayerIndex = 1;
    public Widget parent = null;

    public        boolean     active          = true;
    public  final Transform2D transform       = new Transform2D(); // used for absolute positioning from root and animations
    public        float       offsetX         = 0; // set by the parent or anchor.
    public        float       offsetY         = 0; // set by the parent or anchor.
    private final Transform2D transformScreen = new Transform2D(); // calculated every frame either by self or parent

    boolean updated = false; // TODO: see when to reset the flag. FIXME NEXT

    private final Shape2DPolygon inputShape = new Shape2DPolygon();


    private final Array<Widget> children = new Array<>();

    public Widget() {
        // register as input listener etc.
    }

    protected abstract void  draw    (Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected          void  drawMask(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) { draw(renderer2D, x, y, deg, sclX, sclY); }
    protected abstract float getWidth();
    protected abstract float getHeight();

    protected void configureInputShape(@NotNull InputRegion region) {
        region.setToRectangle(getWidth(), getHeight());
    }

    private Transform2D getTransformScreen() {
        if (updated) return transformScreen;

        final Transform2D parentTransform = (parent != null) ? parent.getTransformScreen() : null;
        float refX = parentTransform == null ? 0 : parentTransform.x;
        float refY = parentTransform == null ? 0 : parentTransform.y;
        float refDeg = parentTransform == null ? 0 : parentTransform.deg;
        float refSclX = parentTransform == null ? 1 : parentTransform.sclX;
        float refSclY = parentTransform == null ? 1 : parentTransform.sclY;
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = this.transform.x * cos - this.transform.y * sin;
        float y = this.transform.x * sin + this.transform.y * cos;
        transformScreen.x = refX + x * refSclX + offsetX * cos - offsetY * sin; // add the rotated offset vector x component
        transformScreen.y = refY + y * refSclY + offsetX * sin + offsetY * cos; // add the rotated offset vector y component
        transformScreen.deg  = transform.deg + refDeg;
        transformScreen.sclX = transform.sclX * refSclX;
        transformScreen.sclY = transform.sclY * refSclY;

        updated = true;
        return transformScreen;
    }

    // TODO: test
    private int getMaskingIndex() {
        if (parent != null && parent.maskChildren()) return parent.getMaskingIndex() + 1;
        else return 1;
    }

    public boolean maskChildren() { return false; }

    final void render(Renderer2D renderer2D) {
        if (!active) return;
        renderer2D.setColor(Color.WHITE);
        draw(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);

        /* if masking is enabled, draw the mask */
        boolean maskChildren = maskChildren();
        if (maskChildren) {
            renderer2D.beginStencil();
            renderer2D.setStencilModeIncrement();
            drawMask(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);
            renderer2D.endStencil();
        }

        int maskingIndex = getMaskingIndex();
        for (Widget child : children) {
            // apply mask, if masking enabled
            if (maskChildren) {
                renderer2D.enableMasking();
                renderer2D.setMaskingFunctionEquals(maskingIndex);
            }
            child.render(renderer2D);
            if (maskChildren) renderer2D.disableMasking();
        }

        /* if masking is enabled, erase the mask */
        if (maskChildren) {
            renderer2D.beginStencil();
            renderer2D.setStencilModeDecrement();
            drawMask(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);
            renderer2D.endStencil();
        }
    }

    @Override
    public int getInputLayer() {
        if (parent == null) return inputLayerIndex;
        return parent.getInputLayer() + inputLayerIndex;
    }

    @Override
    public boolean isActive() {
        return true;
    }

}
