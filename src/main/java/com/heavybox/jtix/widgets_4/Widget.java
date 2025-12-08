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
    protected       boolean       hidden         = false;
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
    public          boolean   draggableX      = false;
    public          boolean   draggableY      = false;

    /*** input - state management ***/
    private final Region        region                                = new Region(); // TODO: change to private.
    private final Region        regionMask                            = new Region(); // TODO: change tp private.
    private final Array<Region> ancestorsRegions                      = new Array<>(false, 1);
    private       boolean       mouseRegisterLeftButtonActionsInside  = false;
    private       boolean       mouseRegisterRightButtonActionsInside = false;
    private       boolean       mouseRegisterMiddleButtonActionInside = false;
    private       boolean       mouseRegisterLeftClicksOutside        = false;
    private       boolean       mouseRegisterRightClicksOutside       = false;
    private       boolean       mouseRegisterMiddleClicksOutside      = false;
    private       boolean       mouseInside                           = false;
    private       boolean       dragging                              = false;
    private       boolean       draggingPrev                          = false;
    private       boolean       focused                               = false;

    /*** input - event handlers ***/
    public Event.EventListenerMouseUp                 onMouseUp                 = null;
    public Event.EventListenerMouseDown               onMouseDown               = null;
    public Event.EventListenerMouseEnter              onMouseEnter              = null;
    public Event.EventListenerMouseLeave              onMouseLeave              = null;
    public Event.EventListenerMouseLeftClick          onMouseLeftClick          = null;
    public Event.EventListenerMouseRightClick         onMouseRightClick         = null;
    public Event.EventListenerMouseMiddleClick        onMouseMiddleClick        = null;
    public Event.EventListenerMouseLeftClickOutside   onMouseLeftClickOutside   = null;
    public Event.EventListenerMouseRightClickOutside  onMouseRightClickOutside  = null;
    public Event.EventListenerMouseMiddleClickOutside onMouseMiddleClickOutside = null;
    public Event.EventListenerMouseScroll             onMouseScroll             = null;
    public Event.EventListenerMouseDrag               onMouseDrag               = null;
    public Event.EventListenerMouseDragStart          onMouseDragStart          = null;
    public Event.EventListenerMouseDragEnd            onMouseDragEnd            = null;
    public Event.EventListenerResize                  onResize                  = null;
    public Event.EventListenerChildAdded              onChildAdded              = null;
    public Event.EventListenerChildRemoved            onChildRemoved            = null;
    public Event.EventListenerCodepointsTyped         onCodepointsTyped         = null;
    public Event.EventListenerKeysJustPressed         onKeysJustPressed         = null;
    public Event.EventListenerKeysPressed             onKeysPressed             = null;

    /*** default methods for event handling ***/
    protected void onMouseUpDefault                (Event.EventMouseUp e)                 {}
    protected void onMouseDownDefault              (Event.EventMouseDown e)               {}
    protected void onMouseEnterDefault             (Event.EventMouseEnter e)              {}
    protected void onMouseLeaveDefault             (Event.EventMouseLeave e)              {}
    protected void onMouseLeftClickDefault         (Event.EventMouseLeftClick e)          {}
    protected void onMouseRightClickDefault        (Event.EventMouseRightClick e)         {}
    protected void onMouseMiddleClickDefault       (Event.EventMouseMiddleClick e)        {}
    protected void onMouseLeftClickOutsideDefault  (Event.EventMouseLeftClickOutside e)   {}
    protected void onMouseRightClickOutsideDefault (Event.EventMouseRightClickOutside e)  {}
    protected void onMouseMiddleClickOutsideDefault(Event.EventMouseMiddleClickOutside e) {}
    protected void onMouseScrollDefault            (Event.EventMouseScroll e)             {}
    protected void onMouseDragDefault              (Event.EventMouseDrag e)               {}
    protected void onMouseDragStartDefault         (Event.EventMouseDragStart e)          {}
    protected void onMouseDragEndDefault           (Event.EventMouseDragEnd e)            {}
    protected void onResizeDefault                 (Event.EventResize e)                  {}
    protected void onChildAddedDefault             (Event.EventChildAdded e)              {}
    protected void onChildRemovedDefault           (Event.EventChildRemoved e)            {}
    protected void onCodepointsTypedDefault        (Event.EventCodepointsTyped e)         {}
    protected void onKeysJustPressedDefault        (Event.EventKeysJustPressed e)         {}
    protected void onKeysPressedDefault            (Event.EventKeysPressed e)             {}

    /*** Add and remove child methods ***/
    public final void addChild(Widget widget) {
        if (widget == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (widget == this) throw new WidgetsException("Trying to parent a " + Widget.class.getSimpleName() + " to itself.");
        if (widget.parent != null) widget.parent.removeChild(widget);
        if (children.contains(widget, true)) return;

        children.add(widget);
        widget.parent = this;
        Event.EventChildAdded e = new Event.EventChildAdded(transformScreen);
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
        Event.EventChildRemoved e = new Event.EventChildRemoved(transformScreen);
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
        if (hidden) return;
        try {
            draw(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);
        } catch (Exception e) {
            //throw e;
        }

        /* if masking is enabled, draw the mask */
        boolean maskChildren = maskChildren();
        if (maskChildren) {
            renderer2D.beginStencil();
            renderer2D.setStencilModeIncrement();
            try {
                drawMask(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);
            } catch (Exception e) {
                //throw e;
            }
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
            try {
                drawMask(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);
            } catch (Exception e) {
                //throw e;
            }
            renderer2D.endStencil();
        }
    }

    protected abstract void  draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();

    public boolean isFocused() {
        return focused;
    }

    protected void drawMask(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        draw(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void fixedUpdate(float delta) {}

    public final void update(float delta) {
        /* update internal state: set active children, global transform etc */
        updateInternalState(); // TODO: it is probably ok to remove this if you call it once on init() or something, then every time the state is changed.

        /* injected fixed update */
        // probably do the lag stuff in ECS.
        fixedUpdate(delta);
        /* handle input. It is possible that an event changed the widget's internal state by adding children, changing size etc.
        So if an event was fired, update the internal state again.
         */
        boolean eventFired = handleInput();
        if (eventFired) updateInternalState();

        /* update all children */
        for (Widget widget : childrenActive) {
            widget.update(delta);
        }

    }

    private void updateInternalState() {
        childrenActive.clear();
        for (Widget child : children) {
            if (child.active) childrenActive.add(child);
        }
        childrenLayout.clear();
        for (Widget child : childrenActive) {
            if (child.anchor == null) childrenLayout.add(child);
        }
        setChildrenOffsets(childrenLayout);
        setOffsetsAnchor();
        prevWidth = width;
        prevHeight = height;
        width = getWidth();
        height = getHeight();
        calculateGlobalTransform();
    }

    // TODO: handle input should be recursive?
    // TODO: event propagation and bubbling
    protected boolean handleInput() {
        configureInputRegion(region);
        region.transform(transformScreen);
        configureInputMaskedRegion(regionMask);
        regionMask.transform(transformScreen);

        boolean eventFired = false;

        /*  mouse input */
        float pointerXPrev = Widgets.getPointerXPrev();
        float pointerYPrev = Widgets.getPointerYPrev();
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();
        float verticalScroll = Input.mouse.getVerticalScroll();
        boolean mouseInsidePrev = mouseInside;
        mouseInside = containsPoint(pointerX, pointerY);
        boolean mouseJustEntered = (!mouseInsidePrev && mouseInside) || (Input.mouse.cursorJustEnteredWindow() && mouseInside);
        boolean mouseJustLeft = (!mouseInside && mouseInsidePrev) || (Input.mouse.cursorJustLeftWindow() && mouseInsidePrev);
        boolean draggable = draggableX || draggableY;
        boolean leftMouseDrag = mouseInside && Input.mouse.moved() && Input.mouse.isButtonPressed(Mouse.Button.LEFT);
        draggingPrev = dragging;
        if (mouseInside && Input.mouse.isButtonPressed(Mouse.Button.LEFT)) {
            dragging = true;
        }
        if (Input.mouse.isButtonReleased(Mouse.Button.LEFT)) {
            dragging = false;
        }
        if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            mouseRegisterLeftButtonActionsInside = mouseInside;
        }
        if (Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT)) {
            mouseRegisterRightButtonActionsInside = mouseInside;
        }
        if (Input.mouse.isButtonJustPressed(Mouse.Button.MIDDLE)) {
            mouseRegisterMiddleButtonActionInside = mouseInside;
        }
        if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            mouseRegisterLeftClicksOutside = !mouseInside;
        }
        if (Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT)) {
            mouseRegisterRightClicksOutside = !mouseInside;
        }
        if (Input.mouse.isButtonJustPressed(Mouse.Button.MIDDLE)) {
            mouseRegisterMiddleClicksOutside = !mouseInside;
        }
        boolean mouseUpLeft = Input.mouse.isButtonJustReleased(Mouse.Button.LEFT);
        boolean mouseUpRight = Input.mouse.isButtonJustReleased(Mouse.Button.RIGHT);
        boolean mouseUpMiddle = Input.mouse.isButtonJustReleased(Mouse.Button.MIDDLE);
        boolean mouseUp = mouseInside && (mouseUpLeft || mouseUpRight || mouseUpMiddle);
        boolean mouseDownLeft = Input.mouse.isButtonPressed(Mouse.Button.LEFT);
        boolean mouseDownRight = Input.mouse.isButtonPressed(Mouse.Button.RIGHT);
        boolean mouseDownMiddle = Input.mouse.isButtonPressed(Mouse.Button.MIDDLE);
        boolean mouseDown = mouseInside && (mouseDownLeft || mouseDownRight || mouseDownMiddle);

        /* key presses */
        boolean codepointPressed = !Input.keyboard.getCodepointPressed().isEmpty();
        boolean keysJustPressed = !Input.keyboard.getKeysJustDown().isEmpty();
        boolean keysPressed = !Input.keyboard.getKeysDown().isEmpty();

        /* dimensions change */
        float deltaWidth  = width - prevWidth;
        float deltaHeight = height - prevHeight;
        boolean resized = !MathUtils.isZero(deltaWidth) || !MathUtils.isZero(deltaHeight);

        /* mouse down */
        if (mouseDown) {
            eventFired = true;
            Event.EventMouseDown e = new Event.EventMouseDown(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            e.buttonLeft = mouseDownLeft;
            e.buttonRight = mouseDownRight;
            e.buttonMiddle = mouseDownMiddle;
            if (onMouseDown != null) {
                boolean handled = onMouseDown.handle(e);
                if (!handled) onMouseDownDefault(e);
            } else {
                onMouseDownDefault(e);
            }
        }

        /* mouse up */
        if (mouseUp) {
            eventFired = true;
            Event.EventMouseUp e = new Event.EventMouseUp(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            e.buttonLeft = mouseUpLeft;
            e.buttonRight = mouseUpRight;
            e.buttonMiddle = mouseUpMiddle;
            if (onMouseUp != null) {
                boolean handled = onMouseUp.handle(e);
                if (!handled) onMouseUpDefault(e);
            } else {
                onMouseUpDefault(e);
            }
        }

        /* mouse click - left */
        if (mouseRegisterLeftButtonActionsInside && Input.mouse.isButtonClicked(Mouse.Button.LEFT) && mouseInside) {
            eventFired = true;
            focused = true;
            Event.EventMouseLeftClick e = new Event.EventMouseLeftClick(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseLeftClick != null) {
                boolean handled = onMouseLeftClick.handle(e);
                if (!handled) onMouseLeftClickDefault(e);
            } else {
                onMouseLeftClickDefault(e);
            }
        }

        /* mouse click - right */
        if (mouseRegisterRightButtonActionsInside && Input.mouse.isButtonClicked(Mouse.Button.RIGHT) && mouseInside) {
            eventFired = true;
            Event.EventMouseRightClick e = new Event.EventMouseRightClick(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseRightClick != null) {
                boolean handled = onMouseRightClick.handle(e);
                if (!handled) onMouseRightClickDefault(e);
            } else {
                onMouseRightClickDefault(e);
            }
        }

        /* mouse click - middle */
        if (mouseRegisterMiddleButtonActionInside && Input.mouse.isButtonClicked(Mouse.Button.MIDDLE) && mouseInside) {
            eventFired = true;
            Event.EventMouseMiddleClick e = new Event.EventMouseMiddleClick(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseMiddleClick != null) {
                boolean handled = onMouseMiddleClick.handle(e);
                if (!handled) onMouseMiddleClickDefault(e);
            } else {
                onMouseMiddleClickDefault(e);
            }
        }

        /* mouse left click - outside */
        if (mouseRegisterLeftClicksOutside && Input.mouse.isButtonClicked(Mouse.Button.LEFT) && !mouseInside) {
            eventFired = true;
            focused = false;
            Event.EventMouseLeftClickOutside e = new Event.EventMouseLeftClickOutside(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseLeftClickOutside != null) {
                boolean handled = onMouseLeftClickOutside.handle(e);
                if (!handled) onMouseLeftClickOutsideDefault(e);
            } else {
                onMouseLeftClickOutsideDefault(e);
            }
        }

        /* mouse right click - outside */
        if (mouseRegisterRightClicksOutside && Input.mouse.isButtonClicked(Mouse.Button.RIGHT) && !mouseInside) {
            eventFired = true;
            Event.EventMouseRightClickOutside e = new Event.EventMouseRightClickOutside(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseRightClickOutside != null) {
                boolean handled = onMouseRightClickOutside.handle(e);
                if (!handled) onMouseRightClickOutsideDefault(e);
            } else {
                onMouseRightClickOutsideDefault(e);
            }
        }

        /* mouse middle click - outside */
        if (mouseRegisterMiddleClicksOutside && Input.mouse.isButtonClicked(Mouse.Button.MIDDLE) && !mouseInside) {
            eventFired = true;
            Event.EventMouseMiddleClickOutside e = new Event.EventMouseMiddleClickOutside(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseMiddleClickOutside != null) {
                boolean handled = onMouseMiddleClickOutside.handle(e);
                if (!handled) onMouseMiddleClickOutsideDefault(e);
            } else {
                onMouseMiddleClickOutsideDefault(e);
            }
        }

        /* mouse enter */
        if (mouseJustEntered) {
            eventFired = true;
            Event.EventMouseEnter e = new Event.EventMouseEnter(transformScreen);
            Vector2 localPrev = new Vector2(pointerXPrev, pointerYPrev);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            localPrev.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalXPrev = localPrev.x;
            e.mouseLocalYPrev = localPrev.y;
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
            eventFired = true;
            Event.EventMouseLeave e = new Event.EventMouseLeave(transformScreen);
            Vector2 localPrev = new Vector2(pointerXPrev, pointerYPrev);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            localPrev.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalXPrev = localPrev.x;
            e.mouseLocalYPrev = localPrev.y;
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
            eventFired = true;
            Event.EventMouseScroll e = new Event.EventMouseScroll(transformScreen);
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

        /* mouse dragged */
        if (draggable && dragging && mouseRegisterLeftButtonActionsInside) {
            eventFired = true;
            Event.EventMouseDrag e = new Event.EventMouseDrag(transformScreen);
            Vector2 localPrev = new Vector2(pointerXPrev, pointerYPrev);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            localPrev.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalXPrev = localPrev.x;
            e.mouseLocalYPrev = localPrev.y;
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            e.mouseLocalDeltaX = local.x - localPrev.x;
            e.mouseLocalDeltaY = local.y - localPrev.y;
            if (draggableX) transform.x += e.mouseLocalDeltaX;
            if (draggableY) transform.y += e.mouseLocalDeltaY;
            if (onMouseDrag != null) {
                boolean handled = onMouseDrag.handle(e);
                if (!handled) onMouseDragDefault(e);
            } else {
                onMouseDragDefault(e);
            }
        }

        // mouse drag start
        if (draggable && dragging && !draggingPrev) {
            eventFired = true;
            Event.EventMouseDragStart e = new Event.EventMouseDragStart(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            if (onMouseDragStart != null) {
                boolean handled = onMouseDragStart.handle(e);
                if (!handled) onMouseDragStartDefault(e);
            } else {
                onMouseDragStartDefault(e);
            }
        }

        if (draggable && !dragging && draggingPrev) {
            eventFired = true;
            Event.EventMouseDragEnd e = new Event.EventMouseDragEnd(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            if (onMouseDragEnd != null) {
                boolean handled = onMouseDragEnd.handle(e);
                if (!handled) onMouseDragEndDefault(e);
            } else {
                onMouseDragEndDefault(e);
            }
        }

        /* resize */
        if (resized) {
            eventFired = true;
            Event.EventResize e = new Event.EventResize(transformScreen);
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

        /* codepoint presses */
        if (focused && codepointPressed) {
            eventFired = true;
            Event.EventCodepointsTyped e = new Event.EventCodepointsTyped(transformScreen);
            e.codePoints = Input.keyboard.getCodepointPressed();
            if (onCodepointsTyped != null) {
                boolean handled = onCodepointsTyped.handle(e);
                if (!handled) onCodepointsTypedDefault(e);
            } else {
                onCodepointsTypedDefault(e);
            }
        }

        /* keys just pressed */
        if (focused && keysJustPressed) {
            eventFired = true;
            Event.EventKeysJustPressed e = new Event.EventKeysJustPressed(transformScreen);
            e.keys.addAll(Input.keyboard.getKeysJustDown());
            if (onKeysJustPressed != null) {
                boolean handled = onKeysJustPressed.handle(e);
                if (!handled) onKeysJustPressedDefault(e);
            } else {
                onKeysJustPressedDefault(e);
            }
        }

        /* keys pressed */
        // TODO: set cool down time for keys as part of a widget handler.
        if (focused && keysPressed) {
            eventFired = true;
            Event.EventKeysPressed e = new Event.EventKeysPressed(transformScreen);
            e.keys.addAll(Input.keyboard.getKeysDown());
            if (onKeysPressed != null) {
                boolean handled = onKeysPressed.handle(e);
                if (!handled) onKeysPressedDefault(e);
            } else {
                onKeysPressedDefault(e);
            }
        }

        return eventFired; // change
    }

    // containers can override this, for example.
    protected void setChildrenOffsets(final Array<Widget> activeChildren) {
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

    // TODO: what if the parent is: not active? hidden?
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

    public void setFocused(boolean focused) {
        this.focused = focused;
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
        if (anchor == null) return;

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
            case TOP_CENTER:
                screen_max_y = halfParentHeight - max_y;
                offsetX = 0;
                offsetY = screen_max_y - anchorY;
                break;
            case BOTTOM_CENTER:
                screen_min_y = min_y + halfParentHeight;
                offsetX = 0;
                offsetY = anchorY - screen_min_y;
                break;
            case TOP_LEFT:
                screen_min_x = min_x + halfParentWidth;
                screen_max_y = halfParentHeight - max_y;
                offsetX = anchorX - screen_min_x;
                offsetY = screen_max_y - anchorY;
                break;
            case TOP_RIGHT:
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
        TOP_LEFT   , TOP_CENTER   , TOP_RIGHT   ,
        CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
        ;
    }

}
