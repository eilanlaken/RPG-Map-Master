package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public abstract class Node {

    protected NodeContainer container = null;
    protected Polygon       polygon   = new Polygon();
    public    boolean       active    = true;

    /* input handling */
    public Runnable onClick     = null;
    public Runnable onMouseOver  = null;
    public Runnable onMouseEnter = null;
    public Runnable onMouseLeave = null;
    private boolean mouseRegisterClicks = false;
    private boolean mouseInside = false;
    private boolean mouseInsidePrev = false;

    /* can be explicitly set by the programmer */
    public int   zIndex = 0;
    public float x      = 0;
    public float y      = 0;
    public float deg    = 0;
    public float sclX   = 1;
    public float sclY   = 1;

    /* calculated by container and Transform */
    // TODO: change to protected
    public int   screenZIndex = 0;
    public float screenX      = 0;
    public float screenY      = 0;
    public float screenDeg    = 0;
    public float screenSclX   = 1;
    public float screenSclY   = 1;

    // calculated by container
    public float offsetX = 0;
    public float offsetY = 0;
    public int maskingIndex = 1; // TODO.

    protected abstract void fixedUpdate(float delta);
    protected abstract void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    public abstract float calculateWidth(); // TODO: cache location and implement reset() logic
    public abstract float calculateHeight(); // TODO: cache location and implement reset() logic

    // TODO: optimize by caching and only change when setting a parent.
    public final int getMaskingIndex() {
        if (container != null) return container.getMaskingIndex() + 1;
        return 1;
    }

    protected final void draw(Renderer2D renderer2D) {
        render(renderer2D, screenX, screenY, screenDeg, screenSclX, screenSclY);
    }

    public final void update(float delta) {
        transform();
        fixedUpdate(delta);
    }

    final void setInputRegion() {
        setPolygon(polygon);
        polygon.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);
    }

    // TODO: many bugs here.
    final void handleInput() {
        setPolygon(polygon);
        polygon.applyTransform(screenX, screenY, screenDeg, screenSclX, screenSclY);

        // TODO see how to lift up to widget.
        float windowHalfWidth = Graphics.getWindowWidth() * 0.5f;
        float windowHalfHeight = Graphics.getWindowHeight() * 0.5f;
        float pointerX = Input.mouse.getX() - windowHalfWidth;
        float pointerY = windowHalfHeight - Input.mouse.getY();

        mouseInsidePrev = mouseInside;
        mouseInside = containsPoint(pointerX, pointerY);
        boolean mouseJustEntered = (!mouseInsidePrev && mouseInside) || (Input.mouse.cursorJustEnteredWindow() && mouseInside);
        boolean mouseJustLeft = (!mouseInside && mouseInsidePrev) || Input.mouse.cursorJustLeftWindow();
        if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            mouseRegisterClicks = mouseInside;
        }

        /* invoke event callbacks */
        // TODO
        if (mouseRegisterClicks && Input.mouse.isButtonClicked(Mouse.Button.LEFT) && mouseInside) {
            if (onClick != null) onClick.run();
        }
        if (mouseJustEntered) {
            if (onMouseEnter != null) onMouseEnter.run();
        }
        if (mouseJustLeft) {
            if (onMouseLeave != null) onMouseLeave.run();
        }

        frameUpdate();
    }

    protected void frameUpdate() {

    }

    final void transform() {
        int refZIndex = container == null ? this.zIndex : this.zIndex + container.screenZIndex;
        float refX = container == null ? 0 : container.screenX;
        float refY = container == null ? 0 : container.screenY;
        float refDeg = container == null ? 0 : container.screenDeg;
        float refSclX = container == null ? 1 : container.screenSclX;
        float refSclY = container == null ? 1 : container.screenSclY;
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = this.x * cos - this.y * sin;
        float y = this.x * sin + this.y * cos;
        screenZIndex = refZIndex + this.zIndex;
        screenX = refX + x * refSclX + offsetX * cos - offsetY * sin; // add the rotated offset vector x component
        screenY = refY + y * refSclY + offsetX * sin + offsetY * cos; // add the rotated offset vector y component
        screenDeg  = this.deg + refDeg;
        screenSclX = this.sclX * refSclX;
        screenSclY = this.sclY * refSclY;
    }


    // kind of a default implementation
    protected void setPolygon(final Polygon polygon) {
        polygon.setToRectangle(calculateWidth(), calculateHeight());
    }

    final boolean containsPoint(float x, float y) {
        if (container == null) return polygon.containsPoint(x, y);

        if (container.contentOverflowX == NodeContainer.Overflow.VISIBLE && container.contentOverflowY == NodeContainer.Overflow.VISIBLE) {
            return polygon.containsPoint(x, y);
        }

        if (container.contentOverflowX == NodeContainer.Overflow.VISIBLE && container.contentOverflowY == NodeContainer.Overflow.HIDDEN) {
            if (MathUtils.isZero(container.sclY)) return false;

            float height = container.calculateHeight() - container.boxBorderSize;
            Vector2 v = new Vector2(x, y);
            v.sub(container.screenX, container.screenY);
            v.rotateDeg(-container.screenDeg);
            v.scl(0, 1 / container.screenSclY);

            return polygon.containsPoint(x, y) && Math.abs(v.y) <= Math.abs(height / 2);
        }

        if (container.contentOverflowX == NodeContainer.Overflow.HIDDEN && container.contentOverflowY == NodeContainer.Overflow.VISIBLE) {
            if (MathUtils.isZero(container.sclX)) return false;

            float width = container.calculateWidth() - container.boxBorderSize;
            Vector2 v = new Vector2(x, y);
            v.sub(container.screenX, container.screenY);
            v.rotateDeg(-container.screenDeg);
            v.scl(0, 1 / container.screenSclY);

            return polygon.containsPoint(x, y) && Math.abs(v.x) <= Math.abs(width / 2);
        }

        return polygon.containsPoint(x, y) && container.containsPoint(x, y);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}