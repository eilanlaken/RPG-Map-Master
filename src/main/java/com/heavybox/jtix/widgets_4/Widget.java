package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;

public abstract class Widget {

    /*** ui hierarchy ***/
    public          boolean       active         = true;
    protected       Widget        parent         = null;
    protected final Array<Widget> children       = new Array<>(true, 1);
    protected final Array<Widget> childrenLayout = new Array<>(true, 1);
    protected final Array<Widget> childrenActive = new Array<>(true, 1);

    /*** metrics: transform and dimensions ***/
    public          float     width           = 0; // TODO: use for caching and event handling
    public          float     height          = 0; // TODO: use for caching and event handling
    public          float     prevWidth       = 0; // TODO: use for caching and event handling
    public          float     prevHeight      = 0; // TODO: use for caching and event handling
    public    final Transform transform       = new Transform(); // used for absolute positioning from root and animations
    private   final Transform transformScreen = new Transform(); // calculated every frame either by self or parent
    protected       float     offsetX         = 0; // set by the parent or anchor.
    protected       float     offsetY         = 0; // set by the parent or anchor.
    public          Anchor    anchor          = null; // anchors one of the margins of the widget to the window
    public          float     anchorX         = 0; // the anchor x distance to be maintained at all times
    public          float     anchorY         = 0; // the anchor y distance to be maintained at all times

    /*** input - state management ***/
    private final Region        region              = new Region(); // TODO: change to private.
    private final Region        regionMask          = new Region(); // TODO: change tp private.
    private final Array<Region> ancestorsRegions    = new Array<>(false, 1);
    private       boolean       mouseRegisterClicks = false;
    private       boolean       mouseInside         = false;

    /*** input - event handlers ***/
    public Event.EventListenerMouseUp      onMouseUp      = null;
    public Event.EventListenerMouseDown    onMouseDown    = null;
    public Event.EventListenerMouseEnter   onMouseEnter   = null;
    public Event.EventListenerMouseLeave   onMouseLeave   = null;
    public Event.EventListenerMouseClick   onMouseClick   = null;
    public Event.EventListenerMouseScroll  onMouseScroll  = null;
    public Event.EventListenerResize       onResize       = null;
    public Event.EventListenerChildAdded   onChildAdded   = null;
    public Event.EventListenerChildRemoved onChildRemoved = null;

    /*** default methods for event handling ***/
    protected void onMouseUpDefault     (Event.EventMouseUp e)      {}
    protected void onMouseDownDefault   (Event.EventMouseDown e)    {}
    protected void onMouseEnterDefault  (Event.EventMouseEnter e)   {}
    protected void onMouseLeaveDefault  (Event.EventMouseLeave e)   {}
    protected void onMouseClickDefault  (Event.EventMouseClick e)   {}
    protected void onMouseScrollDefault (Event.EventMouseScroll e)  {}
    protected void onResizeDefault      (Event.EventResize e)       {}
    protected void onChildAddedDefault  (Event.EventChildAdded e)   {}
    protected void onChildRemovedDefault(Event.EventChildRemoved e) {}

    /*** Add and remove child methods ***/
    public final void addChild(Widget widget) {
        if (widget == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (widget == this) throw new WidgetsException("Trying to parent a " + Widget.class.getSimpleName() + " to itself.");
        if (widget.parent != null) widget.parent.removeChild(widget);
        if (children.contains(widget, true)) return;

        children.add(widget);
        widget.parent = this;
        Event.EventChildAdded e = new Event.EventChildAdded();
        e.widget = widget;
        if (onChildAdded != null) {
            boolean handled = onChildAdded.handle(e);
            if (!handled) onChildAddedDefault(e);
        } else {
            onChildAddedDefault(e);
        }
    }

    public final void removeChild(Widget widget) {
        if (widget == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (!children.contains(widget, true)) throw new WidgetsException(Widget.class.getSimpleName() + " does not contain the element " + widget + " as a child so it cannot be removed.");

        int index = children.removeValue(widget,true);
        widget.parent = null;
        Event.EventChildRemoved e = new Event.EventChildRemoved();
        e.widget = widget;
        e.index = index;
        if (onChildRemoved != null) {
            boolean handled = onChildRemoved.handle(e);
            if (!handled) onChildRemovedDefault(e);
        } else {
            onChildRemovedDefault(e);
        }
    }

    public final void render(Renderer2D renderer2D) {
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
        for (Widget child : childrenActive) {
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

    protected abstract void  draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();

    protected void drawMask(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        draw(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void fixedUpdate(float delta) {}

    // TODO: the heart of all the ui library is here.
    public void update(float delta) {
        prevWidth = width;
        prevHeight = height;


        calculateGlobalTransform();

        childrenActive.clear();
        for (Widget child : children) {
            if (child.active) childrenActive.add(child);
        }

        childrenLayout.clear();
        for (Widget child : childrenActive) {
            if (child.anchor == null) childrenLayout.add(child);
        }
        setChildrenOffsets(childrenLayout);
        if (anchor != null) { // if the widget has an anchor, then it will position itself relative to the parent's / window borders
            setOffsetsAnchor();
        }

        width = getWidth();
        height = getHeight();

        // TODO: maybe this should go to handleInput()
        configureInputRegion(region);
        region.transform(transformScreen);
        configureInputMaskedRegion(regionMask);
        regionMask.transform(transformScreen);

        for (Widget widget : childrenActive) {
            widget.update(delta);
        }

        handleInput();

        // probably do the lag stuff in ECS.
        fixedUpdate(delta);
    }

    // TODO: handle input should be recursive?
    // TODO: handle click outside
    // TODO: event propagation and bubbling
    protected boolean handleInput() {
        // mouse input
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();
        float verticalScroll = Input.mouse.getVerticalScroll();
        boolean mouseInsidePrev = mouseInside;
        mouseInside = containsPoint(pointerX, pointerY);
        boolean mouseJustEntered = (!mouseInsidePrev && mouseInside) || (Input.mouse.cursorJustEnteredWindow() && mouseInside);
        boolean mouseJustLeft = (!mouseInside && mouseInsidePrev) || (Input.mouse.cursorJustLeftWindow() && mouseInsidePrev);
        if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            mouseRegisterClicks = mouseInside;
        }

        // dimensions change
        float deltaWidth  = width - prevWidth;
        float deltaHeight = height - prevHeight;
        boolean resized = !MathUtils.isZero(deltaWidth) || !MathUtils.isZero(deltaHeight);

        // TODO
        /* mouse up */

        // TODO
        /* mouse down */

        /* mouse click */
        if (mouseRegisterClicks && Input.mouse.isButtonClicked(Mouse.Button.LEFT) && mouseInside) {
            Event.EventMouseClick e = new Event.EventMouseClick();
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseClick != null) {
                boolean handled = onMouseClick.handle(e);
                if (!handled) onMouseClickDefault(e);
            } else {
                onMouseClickDefault(e);
            }
        }

        /* mouse enter */
        if (mouseJustEntered) {
            Event.EventMouseEnter e = new Event.EventMouseEnter();
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseEnter != null) {
                boolean handled = onMouseEnter.handle(e);
                if (!handled) onMouseEnterDefault(e);
            } else {
                onMouseEnterDefault(e);
            }
        }

        /* mouse leave */
        if (mouseJustLeft) {
            Event.EventMouseLeave e = new Event.EventMouseLeave();
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseLeave != null) {
                boolean handled = onMouseLeave.handle(e);
                if (!handled) onMouseLeaveDefault(e);
            } else {
                onMouseLeaveDefault(e);
            }
        }

        /* mouse scroll */
        if (mouseInside && verticalScroll != 0) {
            Event.EventMouseScroll e = new Event.EventMouseScroll();
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            e.scrollValue = verticalScroll;
            if (onMouseScroll != null) {
                boolean handled = onMouseScroll.handle(e);
                if (!handled) onMouseScrollDefault(e);
            } else {
                onMouseScrollDefault(e);
            }
        }

        /* resize */
        if (resized) {
            Event.EventResize e = new Event.EventResize();
            e.prevWidth = prevWidth;
            e.prevHeight = prevHeight;
            e.newWidth = width;
            e.newHeight = height;
            if (onResize != null) {
                boolean handled = onResize.handle(e);
                if (!handled) onResizeDefault(e);
            } else {
                onResizeDefault(e);
            }
        }

        return false;
    }

    // containers can override this, for example.
    protected void setChildrenOffsets(final @NotNull Array<Widget> activeChildren) {
        for (Widget widget : activeChildren) {
            widget.offsetX = 0;
            widget.offsetY = 0;
        }
    }

    protected void configureInputRegion(final @NotNull Region region) {
        region.setToRectangle(getWidth(), getHeight());
    }

    // default implementation: set masking region same as input region.
    protected void configureInputMaskedRegion(final @NotNull Region maskedRegion) {
        configureInputRegion(maskedRegion);
    }

    private boolean containsPoint(float pointerX, float pointerY) {
        if (!region.containsPoint(pointerX, pointerY)) return false;

        ancestorsRegions.clear();
        Widget p = parent;
        while (p != null) {
            if (p.maskChildren()) {
                ancestorsRegions.add(p.regionMask);
            }
            p = p.parent;
        }

        boolean hit = true;
        for (Region ancestorRegion : ancestorsRegions) {
            hit &= ancestorRegion.containsPoint(pointerX, pointerY);
        }
        return hit;
    }

    private void calculateGlobalTransform() {
        float refX = parent == null ? 0 : parent.transformScreen.x;
        float refY = parent == null ? 0 : parent.transformScreen.y;
        float refDeg = parent == null ? 0 : parent.transformScreen.deg;
        float refSclX = parent == null ? 1 : parent.transformScreen.sclX;
        float refSclY = parent == null ? 1 : parent.transformScreen.sclY;
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = this.transform.x * cos - this.transform.y * sin;
        float y = this.transform.x * sin + this.transform.y * cos;
        transformScreen.x = refX + x * refSclX + offsetX * cos - offsetY * sin; // add the rotated offset vector x component
        transformScreen.y = refY + y * refSclY + offsetX * sin + offsetY * cos; // add the rotated offset vector y component
        transformScreen.deg  = transform.deg + refDeg;
        transformScreen.sclX = transform.sclX * refSclX;
        transformScreen.sclY = transform.sclY * refSclY;
    }

    /***  masking - relevant to containers ***/
    public boolean maskChildren() {
        return false;
    }

    final int getMaskingIndex() {
        if (parent != null && parent.maskChildren()) return parent.getMaskingIndex() + 1;
        else return 1;
    }

    private void setOffsetsAnchor() {

        float currentWidth = width;
        float currentHeight = height;
        float min_x = -currentWidth * 0.5f;
        float max_x = currentWidth * 0.5f;
        float min_y = -currentHeight * 0.5f;
        float max_y = currentHeight * 0.5f;

        float screen_min_x;
        float screen_max_x;
        float screen_min_y;
        float screen_max_y;

        float halfParentWidth = parent == null ? Graphics.getWindowWidth() * 0.5f : parent.getWidth() * 0.5f;
        float halfParentHeight = parent == null ? Graphics.getWindowHeight() * 0.5f : parent.getHeight() * 0.5f;

        switch (anchor) {
            case CENTER_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                offsetX = screen_max_x - anchorX;
                offsetY = 0;
                break;
            case CENTER_LEFT:
                screen_min_x = min_x + halfParentWidth;
                offsetX = anchorX - screen_min_x;
                offsetY = 0;
                break;
            case UPPER_CENTER:
                screen_max_y = halfParentHeight - max_y;
                offsetX = 0;
                offsetY = screen_max_y - anchorY;
                break;
            case BOTTOM_CENTER:
                screen_min_y = min_y + halfParentHeight;
                offsetX = 0;
                offsetY = anchorY - screen_min_y;
                break;
            case UPPER_LEFT:
                screen_min_x = min_x + halfParentWidth;
                screen_max_y = halfParentHeight - max_y;
                offsetX = anchorX - screen_min_x;
                offsetY = screen_max_y - anchorY;
                break;
            case UPPER_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                screen_max_y = halfParentHeight - max_y;
                offsetX = screen_max_x - anchorX;
                offsetY = screen_max_y - anchorY;
                break;
            case BOTTOM_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                screen_min_y = min_y + halfParentHeight;
                offsetX = screen_max_x - anchorX;
                offsetY = anchorY - screen_min_y;
                break;
            case BOTTOM_LEFT:
                screen_min_x = min_x + halfParentWidth;
                screen_min_y = min_y + halfParentHeight;
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
