package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.widgets.Node;
import com.heavybox.jtix.widgets_3.WidgetsException;
import org.jetbrains.annotations.NotNull;

public abstract class Widget {

    public boolean active = true;
    protected Widget parent = null;
    protected final Array<Widget> children = new Array<>(true, 1);
    protected final Array<Widget> activeChildren = new Array<>(true, 1);
    protected final Region region = new Region();
    public Transform localTransform = new Transform();

    // set by the parent or anchor.
    float offsetX = 0; // set by container
    float offsetY = 0; // set by container
    Transform globalTransform = new Transform(); // calculated

    // anchor
    public Anchor anchor;
    public float anchorX = 0;
    public float anchorY = 0;

    public final void addChild(Widget widget) {
        if (widget == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (widget == this) throw new WidgetsException("Trying to parent a " + Widget.class.getSimpleName() + " to itself.");
        if (widget.parent != null) widget.parent.removeChild(widget);

        children.add(widget);
        widget.parent = this;
    }

    public final void removeChild(Widget widget) {
        if (widget == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (!children.contains(widget, true)) throw new WidgetsException(Widget.class.getSimpleName() + " does not contain the element " + widget + " as a child so it cannot be removed.");

        children.removeValue(widget,true);
        widget.parent = null;
    }

    public void render(Renderer2D renderer2D) {
        draw(renderer2D, globalTransform.x, globalTransform.y, globalTransform.deg, globalTransform.sclX, globalTransform.sclY);
        for (Widget child : activeChildren) {
            child.render(renderer2D);
        }
    }

    protected abstract void  draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();

    protected abstract void fixedUpdate(float delta);

    public final void update(float delta) {
        activeChildren.clear();
        for (Widget child : children) {
            if (child.active) activeChildren.add(child);
        }
        setActiveChildrenOffsets(activeChildren);

        if (parent == null) {
            setOffsetsAnchor();
        }
        calculateGlobalTransform();
        setInputRegion(region);
        region.transform(globalTransform);

        for (Widget widget : activeChildren) {
            widget.update(delta);
        }

        fixedUpdate(delta);
    }

    // containers can override this, for example.
    protected void setActiveChildrenOffsets(final @NotNull Array<Widget> activeChildren) {
        for (Widget widget : activeChildren) {
            widget.offsetX = 0;
            widget.offsetY = 0;
        }
    }

    protected void setInputRegion(final @NotNull Region region) {
        region.setToRectangle(getWidth(), getHeight());
    }

    private void calculateGlobalTransform() {
        float refX = parent == null ? 0 : parent.globalTransform.x;
        float refY = parent == null ? 0 : parent.globalTransform.y;
        float refDeg = parent == null ? 0 : parent.globalTransform.deg;
        float refSclX = parent == null ? 1 : parent.globalTransform.sclX;
        float refSclY = parent == null ? 1 : parent.globalTransform.sclY;
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = this.localTransform.x * cos - this.localTransform.y * sin;
        float y = this.localTransform.x * sin + this.localTransform.y * cos;
        globalTransform.x = refX + x * refSclX + offsetX * cos - offsetY * sin; // add the rotated offset vector x component
        globalTransform.y = refY + y * refSclY + offsetX * sin + offsetY * cos; // add the rotated offset vector y component
        globalTransform.deg  = localTransform.deg + refDeg;
        globalTransform.sclX = localTransform.sclX * refSclX;
        globalTransform.sclY = localTransform.sclY * refSclY;
    }

    private void setOffsetsAnchor() {
        if (anchor == null) return;
        float width = getWidth();
        float height = getHeight();
        float min_x = -width * 0.5f;
        float max_x = width * 0.5f;
        float min_y = -height * 0.5f;
        float max_y = height * 0.5f;

        float screen_min_x;
        float screen_max_x;
        float screen_min_y;
        float screen_max_y;
        switch (anchor) {
            case CENTER_RIGHT:
                screen_max_x = Graphics.getWindowWidth() * 0.5f - max_x;
                offsetX = screen_max_x - anchorX;
                offsetY = 0;
                break;
            case CENTER_LEFT:
                screen_min_x = min_x + Graphics.getWindowWidth() * 0.5f;
                offsetX = anchorX - screen_min_x;
                offsetY = 0;
                break;
            case UPPER_CENTER:
                screen_max_y = Graphics.getWindowHeight() * 0.5f - max_y;
                offsetX = 0;
                offsetY = screen_max_y - anchorY;
                break;
            case BOTTOM_CENTER:
                screen_min_y = min_y + Graphics.getWindowHeight() * 0.5f;
                offsetX = 0;
                offsetY = anchorY - screen_min_y;
                break;
            case UPPER_LEFT:
                screen_min_x = min_x + Graphics.getWindowWidth() * 0.5f;
                screen_max_y = Graphics.getWindowHeight() * 0.5f - max_y;
                offsetX = anchorX - screen_min_x;
                offsetY = screen_max_y - anchorY;
                break;
            case UPPER_RIGHT:
                screen_max_x = Graphics.getWindowWidth() * 0.5f - max_x;
                screen_max_y = Graphics.getWindowHeight() * 0.5f - max_y;
                offsetX = screen_max_x - anchorX;
                offsetY = screen_max_y - anchorY;
                break;
            case BOTTOM_RIGHT:
                screen_max_x = Graphics.getWindowWidth() * 0.5f - max_x;
                screen_min_y = min_y + Graphics.getWindowHeight() * 0.5f;
                offsetX = screen_max_x - anchorX;
                offsetY = anchorY - screen_min_y;
                break;
            case BOTTOM_LEFT:
                screen_min_x = min_x + Graphics.getWindowWidth() * 0.5f;
                screen_min_y = min_y + Graphics.getWindowHeight() * 0.5f;
                offsetX = anchorX - screen_min_x;
                offsetY = anchorY - screen_min_y;
                break;
            case CENTER_CENTER:
                // TODO
                break;
        }
    }

    // anchors ensure spacing between a widget and its parent's edges.
    // when the parent is null, it's the window edges.
    // this is important to make the ui responsive.
    public enum Anchor {
        UPPER_LEFT ,  UPPER_CENTER,  UPPER_RIGHT,
        CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
        ;
    }

}
