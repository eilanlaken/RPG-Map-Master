package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Transform2D;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;

// TODO: first handle input and input layer multiplexing + handlers.
// TODO: THEN, rework WidgetNode and Widget.
public abstract class WidgetNode {

    /*** Widget manager reference ***/
    // TODO: must prevent nodes from belonging to two different Widgets.
    // TODO: must consider the case of adding and removing children.
    Widget widget = null;

    /*** ui hierarchy ***/
    private         WidgetNode        parent         = null;
    public          boolean           hidden         = false;
    public          boolean           active         = true;
    protected final Array<WidgetNode> children       = new Array<>(true, 1);
    protected final Array<WidgetNode> childrenLayout = new Array<>(true, 1);
    protected final Array<WidgetNode> childrenActive = new Array<>(true, 1);

    /*** metrics: transform and dimensions ***/
    public        float       width           = 0; // TODO: use for caching and event handling
    public        float       height          = 0; // TODO: use for caching and event handling
    public        float       prevWidth       = 0; // TODO: use for caching and event handling
    public        float       prevHeight      = 0; // TODO: use for caching and event handling
    public  final Transform2D transform       = new Transform2D(); // used for absolute positioning from root and animations
    private final Transform2D transformScreen = new Transform2D(); // calculated every frame either by self or parent
    protected     float       offsetX         = 0; // set by the parent or anchor.
    protected     float       offsetY         = 0; // set by the parent or anchor.
    public        Anchor      anchor          = null; // anchors one of the margins of the widget to the window
    public        float       anchorX         = 0; // the anchor x distance to be maintained at all times
    public        float       anchorY         = 0; // the anchor y distance to be maintained at all times
    public        boolean     draggableX      = false;
    public        boolean     draggableY      = false;

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
    protected boolean onMouseUpDefault                (Event.EventMouseUp e)                 {return true;}
    protected boolean onMouseDownDefault              (Event.EventMouseDown e)               {return true;}
    protected boolean onMouseEnterDefault             (Event.EventMouseEnter e)              {return true;}
    protected boolean onMouseLeaveDefault             (Event.EventMouseLeave e)              {return true;}
    protected boolean onMouseLeftClickDefault         (Event.EventMouseLeftClick e)          {return true;}
    protected boolean onMouseRightClickDefault        (Event.EventMouseRightClick e)         {return true;}
    protected boolean onMouseMiddleClickDefault       (Event.EventMouseMiddleClick e)        {return true;}
    protected boolean onMouseLeftClickOutsideDefault  (Event.EventMouseLeftClickOutside e)   {return false;}
    protected boolean onMouseRightClickOutsideDefault (Event.EventMouseRightClickOutside e)  {return false;}
    protected boolean onMouseMiddleClickOutsideDefault(Event.EventMouseMiddleClickOutside e) {return false;}
    protected boolean onMouseScrollDefault            (Event.EventMouseScroll e)             {return true;}
    protected boolean onMouseDragDefault              (Event.EventMouseDrag e)               {return true;}
    protected boolean onMouseDragStartDefault         (Event.EventMouseDragStart e)          {return false;}
    protected boolean onMouseDragEndDefault           (Event.EventMouseDragEnd e)            {return false;}
    protected boolean onResizeDefault                 (Event.EventResize e)                  {return false;}
    protected boolean onChildAddedDefault             (Event.EventChildAdded e)              {return false;}
    protected boolean onChildRemovedDefault           (Event.EventChildRemoved e)            {return false;}
    protected boolean onCodepointsTypedDefault        (Event.EventCodepointsTyped e)         {return false;}
    protected boolean onKeysJustPressedDefault        (Event.EventKeysJustPressed e)         {return false;}
    protected boolean onKeysPressedDefault            (Event.EventKeysPressed e)             {return false;}

    /*** Add and remove child methods ***/
    public final void addChild(WidgetNode node) {
        if (node == null) throw new WidgetsException(WidgetNode.class.getSimpleName() + " element cannot be null.");
        if (node == this) throw new WidgetsException("Trying to parent a " + WidgetNode.class.getSimpleName() + " to itself.");
        if (node.parent != null) node.parent.removeChild(node);
        if (children.contains(node, true)) return;

        children.add(node);
        node.parent = this;
        node.setWidget(this.widget);
        Event.EventChildAdded e = new Event.EventChildAdded(transformScreen);
        e.node = node;
        if (onChildAdded != null) onChildAdded.handle(e);
        onChildAddedDefault(e);
    }

    public final void removeChild(WidgetNode node) {
        if (node == null) throw new WidgetsException(WidgetNode.class.getSimpleName() + " element cannot be null.");
        if (!children.contains(node, true)) throw new WidgetsException(WidgetNode.class.getSimpleName() + " does not contain the element " + node + " as a child so it cannot be removed.");

        int index = children.removeValue(node,true);
        node.parent = null;
        node.setWidget(null);
        Event.EventChildRemoved e = new Event.EventChildRemoved(transformScreen);
        e.node = node;
        e.index = index;
        if (onChildRemoved != null) onChildRemoved.handle(e);
        onChildRemovedDefault(e);
    }

    final void render(Renderer2D renderer2D) {
        if (hidden) return;
        try {
            draw(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);
        } catch (Exception e) {
            //throw e;
        }
        if (childrenActive.isEmpty()) return;

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
        for (WidgetNode child : childrenActive) {
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

    final void update(float delta) {
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
        for (WidgetNode node : childrenActive) {
            node.update(delta);
        }
    }

    private void updateInternalState() {
        childrenActive.clear();
        for (WidgetNode child : children) {
            if (child.active) childrenActive.add(child);
        }
        childrenLayout.clear();
        for (WidgetNode child : childrenActive) {
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
    final boolean handleInput() {
        /* input region */
        configureInputRegion(region);
        region.transform(transformScreen);
        configureInputMaskedRegion(regionMask);
        regionMask.transform(transformScreen);

        /* flag that will be returned true if event was fired, false otherwise. */
        boolean eventFired = false;
        boolean inputHandled = false;

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
        boolean draggingPrev = dragging;
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
            if (onMouseDown != null) onMouseDown.handle(e);
            onMouseDownDefault(e);
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
            if (onMouseUp != null) onMouseUp.handle(e);
            onMouseUpDefault(e);
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
            if (onMouseLeftClick != null) onMouseLeftClick.handle(e);
            onMouseLeftClickDefault(e);
        }

        /* mouse click - right */
        if (mouseRegisterRightButtonActionsInside && Input.mouse.isButtonClicked(Mouse.Button.RIGHT) && mouseInside) {
            eventFired = true;
            Event.EventMouseRightClick e = new Event.EventMouseRightClick(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseRightClick != null) onMouseRightClick.handle(e);
            onMouseRightClickDefault(e);
        }

        /* mouse click - middle */
        if (mouseRegisterMiddleButtonActionInside && Input.mouse.isButtonClicked(Mouse.Button.MIDDLE) && mouseInside) {
            eventFired = true;
            Event.EventMouseMiddleClick e = new Event.EventMouseMiddleClick(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseMiddleClick != null) onMouseMiddleClick.handle(e);
            onMouseMiddleClickDefault(e);
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
            if (onMouseLeftClickOutside != null) onMouseLeftClickOutside.handle(e);
            onMouseLeftClickOutsideDefault(e);
        }

        /* mouse right click - outside */
        if (mouseRegisterRightClicksOutside && Input.mouse.isButtonClicked(Mouse.Button.RIGHT) && !mouseInside) {
            eventFired = true;
            Event.EventMouseRightClickOutside e = new Event.EventMouseRightClickOutside(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseRightClickOutside != null) onMouseRightClickOutside.handle(e);
            onMouseRightClickOutsideDefault(e);
        }

        /* mouse middle click - outside */
        if (mouseRegisterMiddleClicksOutside && Input.mouse.isButtonClicked(Mouse.Button.MIDDLE) && !mouseInside) {
            eventFired = true;
            Event.EventMouseMiddleClickOutside e = new Event.EventMouseMiddleClickOutside(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            e.mouseLocalX = local.x;
            e.mouseLocalY = local.y;
            if (onMouseMiddleClickOutside != null) onMouseMiddleClickOutside.handle(e);
            onMouseMiddleClickOutsideDefault(e);
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
            if (onMouseEnter != null) onMouseEnter.handle(e);
            onMouseEnterDefault(e);
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
            if (onMouseLeave != null) onMouseLeave.handle(e);
            onMouseLeaveDefault(e);
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
            if (onMouseScroll != null) onMouseScroll.handle(e);
            onMouseScrollDefault(e);
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
            if (onMouseDrag != null) onMouseDrag.handle(e);
            onMouseDragDefault(e);
        }

        // mouse drag start
        if (draggable && dragging && !draggingPrev) {
            eventFired = true;
            Event.EventMouseDragStart e = new Event.EventMouseDragStart(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            if (onMouseDragStart != null) onMouseDragStart.handle(e);
            onMouseDragStartDefault(e);
        }

        if (draggable && !dragging && draggingPrev) {
            eventFired = true;
            Event.EventMouseDragEnd e = new Event.EventMouseDragEnd(transformScreen);
            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
            if (onMouseDragEnd != null) onMouseDragEnd.handle(e);
            onMouseDragEndDefault(e);
        }

        /* resize */
        if (resized) {
            eventFired = true;
            Event.EventResize e = new Event.EventResize(transformScreen);
            e.prevWidth = prevWidth;
            e.prevHeight = prevHeight;
            e.newWidth = width;
            e.newHeight = height;
            if (onResize != null) onResize.handle(e);
            onResizeDefault(e);
        }

        /* codepoint presses */
        if (focused && codepointPressed) {
            eventFired = true;
            Event.EventCodepointsTyped e = new Event.EventCodepointsTyped(transformScreen);
            e.codePoints = Input.keyboard.getCodepointPressed();
            if (onCodepointsTyped != null) onCodepointsTyped.handle(e);
            onCodepointsTypedDefault(e);
        }

        /* keys just pressed */
        if (focused && keysJustPressed) {
            eventFired = true;
            Event.EventKeysJustPressed e = new Event.EventKeysJustPressed(transformScreen);
            e.keys.addAll(Input.keyboard.getKeysJustDown());
            if (onKeysJustPressed != null) onKeysJustPressed.handle(e);
            onKeysJustPressedDefault(e);
        }

        /* keys pressed */
        // TODO: set cool down time for keys as part of a widget handler.
        if (focused && keysPressed) {
            eventFired = true;
            Event.EventKeysPressed e = new Event.EventKeysPressed(transformScreen);
            e.keys.addAll(Input.keyboard.getKeysDown());
            if (onKeysPressed != null) onKeysPressed.handle(e);
            onKeysPressedDefault(e);
        }

        return eventFired; // change
    }

    // containers can override this, for example.
    protected void setChildrenOffsets(final Array<WidgetNode> childrenLayout) {
        for (WidgetNode node : childrenLayout) {
            node.offsetX = 0;
            node.offsetY = 0;
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
        WidgetNode p = parent;
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

    public boolean hasParent() {
        return parent != null;
    }

    public WidgetNode getParent() { return parent; }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    void setWidget(final Widget widget) {
        if (this.widget == widget) return;
        if (widget == null) this.widget = null;
        if (this.widget != null) throw new WidgetsException("WidgetNode " + this.getClass().getSimpleName() + " already belongs to Widget " + this.widget + ".");

        this.widget = widget;
        for (WidgetNode child : children) {
            child.setWidget(widget);
        }
    }

    private void setOffsetsAnchor() {
        if (anchor == null) return;

        float currentWidth = width;
        float currentHeight = height;
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        float min_x = -currentWidth * 0.5f;
        float max_x = currentWidth * 0.5f;
        float min_y = -currentHeight * 0.5f;
        float max_y = currentHeight * 0.5f;

        float screen_min_x;
        float screen_max_x;
        float screen_min_y;
        float screen_max_y;
        float center_x;
        float center_y;

        float halfParentWidth = parent == null ? Graphics.getWindowWidth() * 0.5f : parent.getWidth() * 0.5f;
        float halfParentHeight = parent == null ? Graphics.getWindowHeight() * 0.5f : parent.getHeight() * 0.5f;

        float cursorX = Widgets.getPointerX();
        float cursorY = Widgets.getPointerY();

        float parent_screen_x = parent == null ? 0 : parent.transformScreen.x;
        float parent_screen_y = parent == null ? 0 : parent.transformScreen.y;

        switch (anchor) {
            case PARENT_CENTER_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                offsetX = screen_max_x - anchorX;
                //offsetY = 0;
                break;
            case PARENT_CENTER_LEFT:
                screen_min_x = min_x + halfParentWidth;
                offsetX = anchorX - screen_min_x;
                //offsetY = 0;
                break;
            case PARENT_TOP_CENTER:
                screen_max_y = halfParentHeight - max_y;
                //offsetX = 0;
                offsetY = screen_max_y - anchorY;
                break;
            case PARENT_BOTTOM_CENTER:
                screen_min_y = min_y + halfParentHeight;
                //offsetX = 0;
                offsetY = anchorY - screen_min_y;
                break;
            case PARENT_TOP_LEFT:
                screen_min_x = min_x + halfParentWidth;
                screen_max_y = halfParentHeight - max_y;
                offsetX = anchorX - screen_min_x;
                offsetY = screen_max_y - anchorY;
                break;
            case PARENT_TOP_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                screen_max_y = halfParentHeight - max_y;
                offsetX = screen_max_x - anchorX;
                offsetY = screen_max_y - anchorY;
                break;
            case PARENT_BOTTOM_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                screen_min_y = min_y + halfParentHeight;
                offsetX = screen_max_x - anchorX;
                offsetY = anchorY - screen_min_y;
                break;
            case PARENT_BOTTOM_LEFT:
                screen_min_x = min_x + halfParentWidth;
                screen_min_y = min_y + halfParentHeight;
                offsetX = anchorX - screen_min_x;
                offsetY = anchorY - screen_min_y;
                break;
            case PARENT_CENTER_CENTER:
                screen_min_x = min_x + halfParentWidth;
                screen_min_y = min_y + halfParentHeight;
                screen_max_x = halfParentWidth - max_x;
                screen_max_y = halfParentHeight - max_y;
                center_x = (screen_min_x + screen_max_x) * 0.5f;
                center_y = (screen_min_y + screen_max_y) * 0.5f;
                offsetX = anchorX - center_x;
                offsetY = anchorY - center_y;
                break;
            case CURSOR_TOP_LEFT:
                screen_min_x = halfWidth;
                screen_max_y = -halfHeight;
                offsetX = (cursorX + screen_min_x - parent_screen_x) + anchorX;
                offsetY = (cursorY + screen_max_y - parent_screen_y) + anchorY;
                break;
            case CURSOR_TOP_CENTER:
                screen_max_y = -halfHeight;
                offsetX = (cursorX - parent_screen_x) + anchorX;
                offsetY = (cursorY + screen_max_y - parent_screen_y) + anchorY;
                break;
            case CURSOR_TOP_RIGHT:
                screen_max_x = -halfWidth;
                screen_max_y = -halfHeight;
                offsetX = (cursorX + screen_max_x - parent_screen_x) + anchorX;
                offsetY = (cursorY + screen_max_y - parent_screen_y) + anchorY;
                break;
            case CURSOR_CENTER_LEFT:
                screen_min_x = halfWidth;
                offsetX = (cursorX + screen_min_x - parent_screen_x) + anchorX;
                offsetY = (cursorY + 0 - parent_screen_y) + anchorY;
                break;
            case CURSOR_CENTER_RIGHT:
                screen_min_x = -halfWidth;
                offsetX = (cursorX + screen_min_x - parent_screen_x) + anchorX;
                offsetY = (cursorY + 0 - parent_screen_y) + anchorY;
                break;
            case CURSOR_BOTTOM_LEFT:
                screen_min_x = halfWidth;
                screen_min_y = halfHeight;
                offsetX = (cursorX + screen_min_x - parent_screen_x) + anchorX;
                offsetY = (cursorY + screen_min_y - parent_screen_y) + anchorY;
                break;
            case CURSOR_BOTTOM_CENTER:
                screen_max_y = halfHeight;
                offsetX = (cursorX - parent_screen_x) + anchorX;
                offsetY = (cursorY + screen_max_y - parent_screen_y) + anchorY;
                break;
            case CURSOR_BOTTOM_RIGHT:
                screen_max_x = -halfWidth;
                screen_min_y = halfHeight;
                offsetX = (cursorX + screen_max_x - parent_screen_x) + anchorX;
                offsetY = (cursorY + screen_min_y - parent_screen_y) + anchorY;
                break;
            case CURSOR_CENTER_CENTER:
                screen_min_x = min_x + halfWidth;
                screen_min_y = min_y + halfHeight;
                screen_max_x = halfWidth - max_x;
                screen_max_y = halfHeight - max_y;
                center_x = (screen_min_x + screen_max_x) * 0.5f;
                center_y = (screen_min_y + screen_max_y) * 0.5f;
                offsetX = (cursorX - center_x - parent_screen_x) + anchorX;
                offsetY = (cursorY - center_y - parent_screen_y) + anchorY;
                break;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    // anchors ensure spacing between a widget and its parent's edges.
    // when the parent is null, it's the window edges.
    // this is important to make the ui responsive.
    public enum Anchor {

        PARENT_TOP_LEFT,     PARENT_TOP_CENTER,    PARENT_TOP_RIGHT,
        PARENT_CENTER_LEFT, PARENT_CENTER_CENTER,  PARENT_CENTER_RIGHT,
        PARENT_BOTTOM_LEFT,  PARENT_BOTTOM_CENTER, PARENT_BOTTOM_RIGHT,

        CURSOR_TOP_LEFT,     CURSOR_TOP_CENTER,    CURSOR_TOP_RIGHT,
        CURSOR_CENTER_LEFT, CURSOR_CENTER_CENTER,  CURSOR_CENTER_RIGHT,
        CURSOR_BOTTOM_LEFT,  CURSOR_BOTTOM_CENTER, CURSOR_BOTTOM_RIGHT,
        ;

    }

}
