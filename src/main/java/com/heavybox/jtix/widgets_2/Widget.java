package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Transform2D;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.widgets.WidgetsException;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;


// TODO: add messaging mechanism
// Widget.sendMessage() to ID, Widget, condition
// Widgets.sendMessage() to ID, Widget, condition
public abstract class Widget {

    public final int ID = Widgets.getID();

    /*** ui hierarchy ***/
    public        boolean       active        = true;
    protected     Widget        parent        = null;
    final         Array<Widget> children      = new Array<>();

    /*** metrics: transform and dimensions ***/
    public        Anchor      anchor          = null;
    public  final Transform2D transform       = new Transform2D(); // used for absolute positioning from root and animations
    private final Transform2D transformOffset = new Transform2D(); // set by the parent layout object.
    private final Transform2D transformScreen = new Transform2D(); // calculated every frame either by self or parent
    public        Layout      layout          = null;

    /*** input handling and state management ***/ // TODO: add a flag that allows events to penetrate to parent. Maybe re-add preventDefault flag.
    public        int                zIndex = ID;
    private final InputShape         inputShape                = new InputShape();
    private final EventListener eventListener = new EventListener(); // TODO: add register listener method
    private final EventListener eventListenerDefault = new EventListener();
    private       boolean            inputMouseInsideSubtree   = false;
    private       Widget             inputMouseDownTarget      = null;
    private       Widget             inputMouseUpTarget        = null;

    protected abstract void  draw    (Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();

    /* common event callbacks */
    protected void    onFixedUpdate(float delta) {} // TODO: call with accumulative error
    protected void    onChildAdded  (Widget child) {}
    protected void    onChildRemoved(Widget child) {}
    protected boolean maskChildren  () { return false; }

    protected void setInputShape(final @NotNull InputShape shape) {
        shape.setToRectangle(getWidth(), getHeight());
    }

    final Widget getParent() {
        return parent;
    }

    public final boolean isRoot() {
        return parent == null;
    }

    public final Widget getRoot() {
        Widget current = this;
        while (!current.isRoot()) {
            current = current.getParent();
        }
        return current;
    }

    /*** Add and remove child methods ***/
    public final void connectChild(Widget child) {
        if (child == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (child == this) throw new WidgetsException("Trying to parent a " + Widget.class.getSimpleName() + " to itself.");
        if (Widgets.isXAncestorOfY(child,this)) throw new WidgetsException("Cannot add an ancestor widget as a child, as this would create a cyclic hierarchy.");
        if (children.contains(child,true)) throw new WidgetsException("Widget " + child.getClass().getSimpleName() + " is already a child of widget.");

        if (child.parent != null) child.parent.children.removeValue(child, true);
        children.add(child);
        child.parent = this;
        onChildAdded(child);
        children.sort(Comparator.comparingInt(a -> a.zIndex));
    }

    public final void disconnectChild(Widget child) {
        if (child == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (!children.contains(child, true)) throw new WidgetsException(Widget.class.getSimpleName() + " does not contain the element " + child + " as a child so it cannot be removed.");

        children.removeValue(child,true);
        child.parent = null;
        Widgets.add(child);
        transformOffset.idt();
        onChildRemoved(child);
        children.sort(Comparator.comparingInt(a -> a.zIndex));
    }

    private void setChildrenOffsets() {
        if (layout == null) { // default no layout behaviour
            for (Widget child : children) {
                if (!child.active) continue;
                if (child.anchor != null) continue;
                child.transformOffset.idt();
            }
            return;
        }

        Widgets.layoutChildren.clear();
        Widgets.layoutOffsets.clear();
        for (Widget child : children) {
            if (!layout.includes(child)) continue;
            Widgets.layoutChildren.add(child);
            Widgets.layoutOffsets.add(child.transformOffset);
        }
        layout.setChildTransformOffset(Widgets.layoutChildren, Widgets.layoutOffsets);
    }

    private void setOffsetsAnchor() {
        if (anchor == null) return;

        float width = getWidth();
        float height = getHeight();
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        float min_x = -width * 0.5f;
        float max_x = width * 0.5f;
        float min_y = -height * 0.5f;
        float max_y = height * 0.5f;

        float screen_min_x;
        float screen_max_x;
        float screen_min_y;
        float screen_max_y;
        float center_x;
        float center_y;

        float halfParentWidth = parent == null ? Graphics.getWindowWidth() * 0.5f : parent.getWidth() * 0.5f;
        float halfParentHeight = parent == null ? Graphics.getWindowHeight() * 0.5f : parent.getHeight() * 0.5f;

        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();

        float parent_screen_x = parent == null ? 0 : parent.transformScreen.x;
        float parent_screen_y = parent == null ? 0 : parent.transformScreen.y;

        switch (anchor) {
            case PARENT_CENTER_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                transformOffset.x = screen_max_x;
                transformOffset.y = 0;
                break;
            case PARENT_CENTER_LEFT:
                screen_min_x = min_x + halfParentWidth;
                transformOffset.x = -screen_min_x;
                transformOffset.y = 0;
                break;
            case PARENT_TOP_CENTER:
                screen_max_y = halfParentHeight - max_y;
                transformOffset.x = 0;
                transformOffset.y = screen_max_y;
                break;
            case PARENT_BOTTOM_CENTER:
                screen_min_y = min_y + halfParentHeight;
                transformOffset.x = 0;
                transformOffset.y = -screen_min_y;
                break;
            case PARENT_TOP_LEFT:
                screen_min_x = min_x + halfParentWidth;
                screen_max_y = halfParentHeight - max_y;
                transformOffset.x = -screen_min_x;
                transformOffset.y = screen_max_y;
                break;
            case PARENT_TOP_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                screen_max_y = halfParentHeight - max_y;
                transformOffset.x = screen_max_x;
                transformOffset.y = screen_max_y;
                break;
            case PARENT_BOTTOM_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                screen_min_y = min_y + halfParentHeight;
                transformOffset.x = screen_max_x;
                transformOffset.y = -screen_min_y;
                break;
            case PARENT_BOTTOM_LEFT:
                screen_min_x = min_x + halfParentWidth;
                screen_min_y = min_y + halfParentHeight;
                transformOffset.x = -screen_min_x;
                transformOffset.y = -screen_min_y;
                break;
            case PARENT_CENTER_CENTER:
                screen_min_x = min_x + halfParentWidth;
                screen_min_y = min_y + halfParentHeight;
                screen_max_x = halfParentWidth - max_x;
                screen_max_y = halfParentHeight - max_y;
                center_x = (screen_min_x + screen_max_x) * 0.5f;
                center_y = (screen_min_y + screen_max_y) * 0.5f;
                transformOffset.x = -center_x;
                transformOffset.y = -center_y;
                break;
        }
    }

    /*** internal state updates and metrics ***/
    private void setGlobalTransform() {
        float parentX = parent == null ? 0 : parent.transformScreen.x;
        float parentY = parent == null ? 0 : parent.transformScreen.y;
        float parentDeg = parent == null ? 0 : parent.transformScreen.deg;
        float parentSclX = parent == null ? 1 : parent.transformScreen.sclX;
        float parentSclY = parent == null ? 1 : parent.transformScreen.sclY;

        // Offset * Local
        float offsetCos = MathUtils.cosDeg(transformOffset.deg);
        float offsetSin = MathUtils.sinDeg(transformOffset.deg);
        float localX = transform.x * transformOffset.sclX;
        float localY = transform.y * transformOffset.sclY;

        float combinedX = transformOffset.x + localX * offsetCos - localY * offsetSin;
        float combinedY = transformOffset.y + localX * offsetSin + localY * offsetCos;
        float combinedDeg = transformOffset.deg + transform.deg;
        float combinedSclX = transformOffset.sclX * transform.sclX;
        float combinedSclY = transformOffset.sclY * transform.sclY;

        // ParentGlobal * Combined
        float parentCos = MathUtils.cosDeg(parentDeg);
        float parentSin = MathUtils.sinDeg(parentDeg);
        float x = combinedX * parentSclX;
        float y = combinedY * parentSclY;
        transformScreen.x = parentX + x * parentCos - y * parentSin;
        transformScreen.y = parentY + x * parentSin + y * parentCos;
        transformScreen.deg = parentDeg + combinedDeg;
        transformScreen.sclX = parentSclX * combinedSclX;
        transformScreen.sclY = parentSclY * combinedSclY;
    }

    // TODO: separate into update internal state and call after potential state change (on callbacks).
    final void update(float delta) {
        if (!active) return;

        setInputShape(inputShape);
        setChildrenOffsets();
        setOffsetsAnchor();
        setGlobalTransform();
        onFixedUpdate(delta); // TODO: do the lag stuff
        // update children
        for (Widget child : children) {
            child.update(delta);
        }
    }

    // TODO: surround the draw() operation with a try-catch clause.
    final void render(Renderer2D renderer2D) {
        if (!active) return;
        renderer2D.setColor(Color.WHITE);
        draw(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);

        /* if masking is enabled, draw the mask */
        boolean maskChildren = maskChildren();
        if (maskChildren) {
            renderer2D.beginStencil();
            renderer2D.setStencilModeIncrement();
            draw(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);
            renderer2D.endStencil();
        }

        children.sort(Widgets.widgetComparator);
        int maskLevel = getMaskingIndex();
        for (Widget child : children) {
            // apply mask, if masking enabled
            if (maskChildren) {
                renderer2D.enableMasking();
                renderer2D.setMaskingFunctionEquals(maskLevel);
            }
            child.render(renderer2D);
            if (maskChildren) renderer2D.disableMasking();
        }

        /* if masking is enabled, erase the mask */
        if (maskChildren) {
            renderer2D.beginStencil();
            renderer2D.setStencilModeDecrement();
            draw(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);
            renderer2D.endStencil();
        }
    }

    final boolean hitTest(float pointerX, float pointerY) {
        if (!isActive()) return false;
        if (!Input.mouse.isCursorInWindow()) return false;
        if (!inputShape.containsPoint(pointerX, pointerY, transformScreen)) return false;

        Widget p = parent;
        boolean hit = true;
        while (p != null) {
            if (p.maskChildren()) {
                hit &= p.inputShape.containsPoint(pointerX, pointerY, p.transformScreen);
            }
            p = p.parent;
        }

        return hit;
    }

    final boolean hitTestSubtree(float pointerX, float pointerY) {
        if (!isActive()) return false;
        if (!Input.mouse.isCursorInWindow()) return false;

        boolean hit = hitTest(pointerX, pointerY);
        for (final Widget child : children) {
            hit |= child.hitTestSubtree(pointerX, pointerY);
        }

        return hit;
    }

    private int getMaskingIndex() {
        if (parent != null && parent.maskChildren()) return parent.getMaskingIndex() + 1;
        else return 1;
    }

    public boolean isActive() {
        return parent == null ? active : active && parent.isActive();
    }

    private Widget findTopmostChildAt(float pointerX, float pointerY) {
        for (int i = children.size - 1; i >= 0; i--) {
            Widget hit = children.get(i).findTopmostChildAt(pointerX, pointerY);
            if (hit != null) return hit;
        }

        return hitTest(pointerX, pointerY) ? this : null;
    }

    /* this widget is guaranteed to be a root widget */
    // TODO: replace by Widgets handler
    @Deprecated
    public final boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();
        Widget target = findTopmostChildAt(pointerX, pointerY);
        inputMouseDownTarget = target;
        if (target == null) return false; // no target of the component tree was hit - mouse down outside the hierarchy

        while (target != null) { // travels to the top-most component that handles the event.
            if (target.eventListener.onMouseDown != null || target.eventListenerDefault.onMouseDown != null) break;
            if (target.eventListener.onMouseDoubleClick != null || target.eventListenerDefault.onMouseDoubleClick != null) break;
            //if (target.inputEventListener.onMouseDragStart != null || target.inputEventListenerDefault.onMouseDragStart != null) break;
            else target = target.getParent();
        }

        if (target == null) return true; // none of the components handle the event.


        Vector2 local = new Vector2(pointerX, pointerY);
        local.transform_TranslateRotateScale(-target.transformScreen.x, -target.transformScreen.y, -target.transformScreen.deg, 1 / target.transformScreen.sclX, 1/ target.transformScreen.sclY);

        // taking care of on mouse down event
        EventData.MouseDown mouseDown = new EventData.MouseDown(
                target,
                buttons.contains(Mouse.Button.LEFT, true),
                buttons.contains(Mouse.Button.RIGHT, true),
                buttons.contains(Mouse.Button.MIDDLE, true),
                local.x,
                local.y
        );
        if (target.eventListener.onMouseDown != null) {
            target.eventListener.onMouseDown.handle(mouseDown);
        }
        if (target.eventListenerDefault.onMouseDown != null) {
            target.eventListenerDefault.onMouseDown.handle(mouseDown);
        }

        // taking care of potential double click
        final Array<Mouse.Button> doubleClicks = Input.mouse.getButtonsDoubleClicked();
        if (!doubleClicks.isEmpty()) {
            EventData.MouseDoubleClick doubleClick = new EventData.MouseDoubleClick(
                    target,
                    buttons.contains(Mouse.Button.LEFT, true),
                    buttons.contains(Mouse.Button.RIGHT, true),
                    buttons.contains(Mouse.Button.MIDDLE, true),
                    local.x,
                    local.y
            );
            if (target.eventListener.onMouseDoubleClick != null) {
                target.eventListener.onMouseDoubleClick.handle(doubleClick);
            }
            if (target.eventListenerDefault.onMouseDoubleClick != null) {
                target.eventListenerDefault.onMouseDoubleClick.handle(doubleClick);
            }
        }

        return true;
    }

    // TODO: replace by Widgets handler
    @Deprecated
    public final boolean mouseButtonsUp(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();
        Widget target = findTopmostChildAt(pointerX, pointerY);
        inputMouseUpTarget = target;
        if (target == null) return false; // no target of the component tree was hit.

        while (target != null) { // travels to the top-most component that handles the event.
            if (target.eventListener.onMouseUp != null || target.eventListenerDefault.onMouseUp != null) break;
            if (target.eventListener.onMouseClick != null || target.eventListenerDefault.onMouseClick != null) break;
            //if (target.inputEventListener.onMouseDragEnd != null || target.inputEventListenerDefault.onMouseDragEnd != null) break;
            else target = target.getParent();
        }

        if (target == null) return true; // none of the components handle the event.


        Vector2 local = new Vector2(pointerX, pointerY);
        local.transform_TranslateRotateScale(-target.transformScreen.x, -target.transformScreen.y, -target.transformScreen.deg, 1 / target.transformScreen.sclX, 1/ target.transformScreen.sclY);

        // taking care of on mouse up event
        EventData.MouseUp mouseUp = new EventData.MouseUp(
                target,
                buttons.contains(Mouse.Button.LEFT, true),
                buttons.contains(Mouse.Button.RIGHT, true),
                buttons.contains(Mouse.Button.MIDDLE, true),
                local.x,
                local.y
        );
        if (target.eventListener.onMouseUp != null) {
            target.eventListener.onMouseUp.handle(mouseUp);
        }
        if (target.eventListenerDefault.onMouseUp != null) {
            target.eventListenerDefault.onMouseUp.handle(mouseUp);
        }

        if (inputMouseDownTarget == inputMouseUpTarget) {
            EventData.MouseClick mouseClick = new EventData.MouseClick(
                    target,
                    buttons.contains(Mouse.Button.LEFT, true),
                    buttons.contains(Mouse.Button.RIGHT, true),
                    buttons.contains(Mouse.Button.MIDDLE, true),
                    local.x,
                    local.y
            );
            if (target.eventListener.onMouseClick != null) {
                target.eventListener.onMouseClick.handle(mouseClick);
            }
            if (target.eventListenerDefault.onMouseClick != null) {
                target.eventListenerDefault.onMouseClick.handle(mouseClick);
            }
        }

        return true;
    }

    // TODO: replace by Widgets handler
    @Deprecated
    public final boolean mouseMoved(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY) {
        float pointerXPrevFrame = Widgets.getPointerXPrev();
        float pointerYPrevFrame = Widgets.getPointerYPrev();
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();

        boolean mouseInsideSubtreePrev = inputMouseInsideSubtree;
        inputMouseInsideSubtree = hitTestSubtree(pointerX, pointerY);
        boolean mouseJustEntered = !mouseInsideSubtreePrev && inputMouseInsideSubtree;
        boolean mouseJustLeft = mouseInsideSubtreePrev && !inputMouseInsideSubtree;

        if (mouseJustEntered && eventListener.onMouseEnter != null) {
            Vector2 local = new Vector2(pointerX, pointerY);
            Vector2 localPrevFrame = new Vector2(pointerXPrevFrame, pointerYPrevFrame);
            local.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            localPrevFrame.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            EventData.MouseEnter mouseEnter = new EventData.MouseEnter(this, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
            eventListener.onMouseEnter.handle(mouseEnter);
        }
        if (mouseJustEntered && eventListenerDefault.onMouseEnter != null) {
            Vector2 local = new Vector2(pointerX, pointerY);
            Vector2 localPrevFrame = new Vector2(pointerXPrevFrame, pointerYPrevFrame);
            local.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            localPrevFrame.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            EventData.MouseEnter mouseEnter = new EventData.MouseEnter(this, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
            eventListenerDefault.onMouseEnter.handle(mouseEnter);
        }
        if (mouseJustLeft && eventListener.onMouseLeave != null) {
            Vector2 local = new Vector2(pointerX, pointerY);
            Vector2 localPrevFrame = new Vector2(pointerXPrevFrame, pointerYPrevFrame);
            local.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            localPrevFrame.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            EventData.MouseLeave mouseEnter = new EventData.MouseLeave(this, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
            eventListener.onMouseLeave.handle(mouseEnter);
        }
        if (mouseJustLeft && eventListenerDefault.onMouseLeave != null) {
            Vector2 local = new Vector2(pointerX, pointerY);
            Vector2 localPrevFrame = new Vector2(pointerXPrevFrame, pointerYPrevFrame);
            local.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            localPrevFrame.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            EventData.MouseLeave mouseEnter = new EventData.MouseLeave(this, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
            eventListenerDefault.onMouseLeave.handle(mouseEnter);
        }

        for (int i = 0; i < children.size; i++) {
            Widget child = children.get(i);
            child.mouseMoved(mouseX, mouseY, deltaMouseX, deltaMouseY);
        }
        return inputMouseInsideSubtree;
    }

    // TODO: replace by Widgets handler
    @Deprecated
    public final boolean mouseScrolled(float scrollX, float scrollY) {
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();

        Widget target = findTopmostChildAt(pointerX, pointerY);
        if (target == null) return false; // no target of the component tree was hit.

        // travels to the top-most component that handles the event.
        while (target != null) {
            if (target.eventListener.onMouseScroll != null || target.eventListenerDefault.onMouseScroll != null) break;
            else target = target.getParent();
        }

        if (target == null) return true; // none of the components handle the event.

        Vector2 local = new Vector2(pointerX, pointerY);
        local.transform_TranslateRotateScale(-target.transformScreen.x, -target.transformScreen.y, -target.transformScreen.deg, 1 / target.transformScreen.sclX, 1/ target.transformScreen.sclY);

        EventData.MouseScroll eventData = new EventData.MouseScroll(
                target,
                scrollX,
                scrollY,
                local.x,
                local.y
        );
        if (target.eventListener.onMouseScroll != null) {
            target.eventListener.onMouseScroll.handle(eventData);
        }
        if (target.eventListenerDefault.onMouseScroll != null) {
            target.eventListenerDefault.onMouseScroll.handle(eventData);
        }

        return true;
    }

    // TODO: replace by Widgets handler
    @Deprecated
    public final boolean mouseDragged(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }

    // TODO: replace by Widgets handler
    @Deprecated
    public final boolean keyboardKeysJustPressed(@NotNull Array<Keyboard.Key> keys) {
        return false;
    }

    // TODO: replace by Widgets handler
    @Deprecated
    public final boolean keyboardKeysJustReleased(@NotNull Array<Keyboard.Key> keys) {
        return false;
    }

    // TODO: replace by Widgets handler
    @Deprecated
    public final boolean keyboardCodepointsTyped(@NotNull ArrayChar codepoints) {
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + ID;
    }

    /*** register event listeners ***/
    public final void onMouseDown(EventListener.OnMouseDown listener) {
        eventListener.onMouseDown = listener;
    }

    public final void onMouseDownDefault(EventListener.OnMouseDown listener) {
        eventListenerDefault.onMouseDown = listener;
    }

    public final void onMouseUp(EventListener.OnMouseUp listener) {
        eventListener.onMouseUp = listener;
    }

    public final void onMouseUpDefault(EventListener.OnMouseUp listener) {
        eventListenerDefault.onMouseUp = listener;
    }

    public final void onMouseClick(EventListener.OnMouseClick listener) {
        eventListener.onMouseClick = listener;
    }

    public final void onMouseClickDefault(EventListener.OnMouseClick listener) {
        eventListenerDefault.onMouseClick = listener;
    }

    public final void onMouseDoubleClick(EventListener.OnMouseDoubleClick listener) {
        eventListener.onMouseDoubleClick = listener;
    }

    public final void onMouseClickDoubleDefault(EventListener.OnMouseDoubleClick listener) {
        eventListenerDefault.onMouseDoubleClick = listener;
    }

    public final void onMouseEnter(EventListener.OnMouseEnter listener) {
        eventListener.onMouseEnter = listener;
    }

    public final void onMouseEnterDefault(EventListener.OnMouseEnter listener) {
        eventListenerDefault.onMouseEnter = listener;
    }

    public final void onMouseLeave(EventListener.OnMouseLeave listener) {
        eventListener.onMouseLeave = listener;
    }

    public final void onMouseLeaveDefault(EventListener.OnMouseLeave listener) {
        eventListenerDefault.onMouseLeave = listener;
    }


    public final void onMouseScroll(EventListener.OnMouseScroll listener) {
        eventListener.onMouseScroll = listener;
    }

    public final void onMouseScrollDefault(EventListener.OnMouseScroll listener) {
        eventListenerDefault.onMouseScroll = listener;
    }

    public final void onMouseDrag(EventListener.OnMouseDrag listener) {
        eventListener.onMouseDrag = listener;
    }

    public final void onMouseDragDefault(EventListener.OnMouseDrag listener) {
        eventListenerDefault.onMouseDrag = listener;
    }

    public final void onMouseDragStart(EventListener.OnMouseDragStart listener) {
        eventListener.onMouseDragStart = listener;
    }

    public final void onMouseDragStartDefault(EventListener.OnMouseDragStart listener) {
        eventListenerDefault.onMouseDragStart = listener;
    }

    public final void onMouseDragEnd(EventListener.OnMouseDragEnd listener) {
        eventListener.onMouseDragEnd = listener;
    }

    public final void onMouseDragEndDefault(EventListener.OnMouseDragEnd listener) {
        eventListenerDefault.onMouseDragEnd = listener;
    }

    public final void onMouseDragEnter(EventListener.OnMouseDragEnter listener) {
        eventListener.onMouseDragEnter = listener;
    }

    public final void onMouseDragEnterDefault(EventListener.OnMouseDragEnter listener) {
        eventListenerDefault.onMouseDragEnter = listener;
    }

    public final void onMouseDragLeave(EventListener.OnMouseDragLeave listener) {
        eventListener.onMouseDragLeave = listener;
    }

    public final void onMouseDragLeaveDefault(EventListener.OnMouseDragLeave listener) {
        eventListenerDefault.onMouseDragLeave = listener;
    }

    public final void onMouseDragDrop(EventListener.OnMouseDragDrop listener) {
        eventListener.onMouseDragDrop = listener;
    }

    public final void onMouseDragDropDefault(EventListener.OnMouseDragDrop listener) {
        eventListenerDefault.onMouseDragDrop = listener;
    }

}

/*
@Deprecated
    private void setGlobalTransform() {
        final Transform2D parentTransform = (parent != null) ? parent.transformScreen : null;
        float refX = parentTransform == null ? 0 : parentTransform.x;
        float refY = parentTransform == null ? 0 : parentTransform.y;
        float refDeg = parentTransform == null ? 0 : parentTransform.deg;
        float refSclX = parentTransform == null ? 1 : parentTransform.sclX;
        float refSclY = parentTransform == null ? 1 : parentTransform.sclY;
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = this.transform.x * cos - this.transform.y * sin;
        float y = this.transform.x * sin + this.transform.y * cos;
        transformScreen.x = refX + x * refSclX + transformOffset.x * cos - transformOffset.y * sin; // add the rotated offset vector x component
        transformScreen.y = refY + y * refSclY + transformOffset.x * sin + transformOffset.y * cos; // add the rotated offset vector y component
        transformScreen.deg  = transform.deg + refDeg;
        transformScreen.sclX = transform.sclX * refSclX;
        transformScreen.sclY = transform.sclY * refSclY;
    }
 */

/*

private void setOffsetsAnchor() {
        if (anchor == null) return;

        float width = getWidth();
        float height = getHeight();
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        float min_x = -width * 0.5f;
        float max_x = width * 0.5f;
        float min_y = -height * 0.5f;
        float max_y = height * 0.5f;

        float screen_min_x;
        float screen_max_x;
        float screen_min_y;
        float screen_max_y;
        float center_x;
        float center_y;

        float halfParentWidth = parent == null ? Graphics.getWindowWidth() * 0.5f : parent.getWidth() * 0.5f;
        float halfParentHeight = parent == null ? Graphics.getWindowHeight() * 0.5f : parent.getHeight() * 0.5f;

        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();

        float parent_screen_x = parent == null ? 0 : parent.transformScreen.x;
        float parent_screen_y = parent == null ? 0 : parent.transformScreen.y;

        switch (anchor) {
            case PARENT_CENTER_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                transformOffset.x = screen_max_x;
                transformOffset.y = 0;
                break;
            case PARENT_CENTER_LEFT:
                screen_min_x = min_x + halfParentWidth;
                transformOffset.x = -screen_min_x;
                transformOffset.y = 0;
                break;
            case PARENT_TOP_CENTER:
                screen_max_y = halfParentHeight - max_y;
                transformOffset.x = 0;
                transformOffset.y = screen_max_y;
                break;
            case PARENT_BOTTOM_CENTER:
                screen_min_y = min_y + halfParentHeight;
                transformOffset.x = 0;
                transformOffset.y = -screen_min_y;
                break;
            case PARENT_TOP_LEFT:
                screen_min_x = min_x + halfParentWidth;
                screen_max_y = halfParentHeight - max_y;
                transformOffset.x = -screen_min_x;
                transformOffset.y = screen_max_y;
                break;
            case PARENT_TOP_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                screen_max_y = halfParentHeight - max_y;
                transformOffset.x = screen_max_x;
                transformOffset.y = screen_max_y;
                break;
            case PARENT_BOTTOM_RIGHT:
                screen_max_x = halfParentWidth - max_x;
                screen_min_y = min_y + halfParentHeight;
                transformOffset.x = screen_max_x;
                transformOffset.y = -screen_min_y;
                break;
            case PARENT_BOTTOM_LEFT:
                screen_min_x = min_x + halfParentWidth;
                screen_min_y = min_y + halfParentHeight;
                transformOffset.x = -screen_min_x;
                transformOffset.y = -screen_min_y;
                break;
            case PARENT_CENTER_CENTER:
                screen_min_x = min_x + halfParentWidth;
                screen_min_y = min_y + halfParentHeight;
                screen_max_x = halfParentWidth - max_x;
                screen_max_y = halfParentHeight - max_y;
                center_x = (screen_min_x + screen_max_x) * 0.5f;
                center_y = (screen_min_y + screen_max_y) * 0.5f;
                transformOffset.x = -center_x;
                transformOffset.y = -center_y;
                break;
            case CURSOR_TOP_LEFT:
                screen_min_x = halfWidth;
                screen_max_y = -halfHeight;
                transformOffset.x = (pointerX + screen_min_x - parent_screen_x);
                transformOffset.y = (pointerY + screen_max_y - parent_screen_y);
                break;
            case CURSOR_TOP_CENTER:
                screen_max_y = -halfHeight;
                transformOffset.x = (pointerX - parent_screen_x);
                transformOffset.y = (pointerY + screen_max_y - parent_screen_y);
                break;
            case CURSOR_TOP_RIGHT:
                screen_max_x = -halfWidth;
                screen_max_y = -halfHeight;
                transformOffset.x = (pointerX + screen_max_x - parent_screen_x);
                transformOffset.y = (pointerY + screen_max_y - parent_screen_y);
                break;
            case CURSOR_CENTER_LEFT:
                screen_min_x = halfWidth;
                transformOffset.x = (pointerX + screen_min_x - parent_screen_x);
                transformOffset.y = (pointerY + 0 - parent_screen_y);
                break;
            case CURSOR_CENTER_RIGHT:
                screen_min_x = -halfWidth;
                transformOffset.x = (pointerX + screen_min_x - parent_screen_x);
                transformOffset.y = (pointerY + 0 - parent_screen_y);
                break;
            case CURSOR_BOTTOM_LEFT:
                screen_min_x = halfWidth;
                screen_min_y = halfHeight;
                transformOffset.x = (pointerX + screen_min_x - parent_screen_x);
                transformOffset.y = (pointerY + screen_min_y - parent_screen_y);
                break;
            case CURSOR_BOTTOM_CENTER:
                screen_max_y = halfHeight;
                transformOffset.x = (pointerX - parent_screen_x);
                transformOffset.y = (pointerY + screen_max_y - parent_screen_y);
                break;
            case CURSOR_BOTTOM_RIGHT:
                screen_max_x = -halfWidth;
                screen_min_y = halfHeight;
                transformOffset.x = (pointerX + screen_max_x - parent_screen_x);
                transformOffset.y = (pointerY + screen_min_y - parent_screen_y);
                break;
            case CURSOR_CENTER_CENTER:
                screen_min_x = min_x + halfWidth;
                screen_min_y = min_y + halfHeight;
                screen_max_x = halfWidth - max_x;
                screen_max_y = halfHeight - max_y;
                center_x = (screen_min_x + screen_max_x) * 0.5f;
                center_y = (screen_min_y + screen_max_y) * 0.5f;
                transformOffset.x = (pointerX - center_x - parent_screen_x);
                transformOffset.y = (pointerY - center_y - parent_screen_y);
                break;
        }
    }


 */