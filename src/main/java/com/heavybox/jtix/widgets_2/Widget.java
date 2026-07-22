package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.InputEventHandler;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Transform2D;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.widgets.WidgetsException;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public abstract class Widget implements InputEventHandler {

    public final int ID = Widgets.getID();

    /*** metrics: transform and dimensions ***/
    private        float      width           = 0; // TODO: use for caching and event handling
    private        float      height          = 0; // TODO: use for caching and event handling
    public  final Transform2D transform       = new Transform2D(); // used for absolute positioning from root and animations
    public        float       offsetX         = 0; // set by the parent or anchor.
    public        float       offsetY         = 0; // set by the parent or anchor.
    private final Transform2D transformScreen = new Transform2D(); // calculated every frame either by self or parent
    public        Anchor      anchor          = null;
    public        float       anchorX         = 0;
    public        float       anchorY         = 0;
    private       int         maskLevel       = 1;
    // TODO: add draggableX, draggableY and dragging state

    /*** ui hierarchy ***/
    public        boolean       active         = true;
    protected     Widget        parent         = null;
    final         Array<Widget> children       = new Array<>();
    private final Array<Widget> childrenLayout = new Array<>();

    /*** input handling and state management ***/ // TODO: add a flag that allows events to penetrate to parent. Maybe re-add preventDefault flag.
    public        int                inputLayer                = ID;
    private final InputRegion        inputRegion               = new InputRegion();
    private final InputEventListener inputEventListener        = new InputEventListener(); // TODO: add register listener method
    private final InputEventListener inputEventListenerDefault = new InputEventListener();
    private       boolean            inputMouseInside          = false; // TODO: delete
    private       boolean            inputMouseInsideSubtree   = false; // TODO: delete
    private       Widget             inputMouseDownTarget      = null;
    private       Widget             inputMouseUpTarget        = null;

    protected abstract void  draw    (Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected abstract float getWidth();
    protected abstract float getHeight();

    /* common event callbacks */
    protected void    fixedUpdate   (float delta) {} // TODO: call with accumulative error
    protected void    onChildAdded  (Widget child) {}
    protected void    onChildRemoved(Widget child) {}
    protected void    onResize      (final float prevWidth, final float prevHeight, final float newWidth, final float newHeight) {}
    public    boolean maskChildren  () { return false; }

    protected void configureInputRegion(final @NotNull InputRegion region) {
        region.setToRectangle(getWidth(), getHeight());
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

        child.recalculateMaskIndex();
        onChildAdded(child);

        children.sort(Comparator.comparingInt(a -> a.inputLayer));
    }

    public final void disconnectChild(Widget child) {
        if (child == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (!children.contains(child, true)) throw new WidgetsException(Widget.class.getSimpleName() + " does not contain the element " + child + " as a child so it cannot be removed.");

        children.removeValue(child,true);
        child.parent = null;
        Widgets.add(child);

        child.recalculateMaskIndex();
        onChildRemoved(child);

        children.sort(Comparator.comparingInt(a -> a.inputLayer));
    }

    // containers can override this, for example.
    // takes an array of child widgets and sets their layout
    /* TODO test */
    protected void setChildrenOffsets(final Array<Widget> childrenLayout) {
        for (Widget widget : childrenLayout) {
            widget.offsetX = 0;
            widget.offsetY = 0;
        }
    }

    /*** anchors ***/
    public void anchorSet(Anchor anchor, float anchorX, float anchorY) {
        this.anchor = anchor;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
    }

    public void anchorRemove() {
        anchor = null;
    }

    /* TODO test */
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

    /*** internal state updates and metrics ***/
    private void calculateGlobalTransform() {
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
        transformScreen.x = refX + x * refSclX + offsetX * cos - offsetY * sin; // add the rotated offset vector x component
        transformScreen.y = refY + y * refSclY + offsetX * sin + offsetY * cos; // add the rotated offset vector y component
        transformScreen.deg  = transform.deg + refDeg;
        transformScreen.sclX = transform.sclX * refSclX;
        transformScreen.sclY = transform.sclY * refSclY;
    }

    // TODO: separate into update internal state and call after potential state change (on callbacks).
    final void update(float delta) {
        if (!active) return;

        configureInputRegion(inputRegion);

        childrenLayout.clear();
        for (Widget child : children) {
            if (!child.active) continue;
            if (child.anchor != null) continue;
            childrenLayout.add(child);
        }
        setChildrenOffsets(childrenLayout);
        setOffsetsAnchor();
        float prevWidth = width;
        float prevHeight = height;
        width = getWidth();
        height = getHeight();

        float deltaWidth  = width - prevWidth;
        float deltaHeight = height - prevHeight;
        boolean resized = !MathUtils.isZero(deltaWidth) || !MathUtils.isZero(deltaHeight);
        if (resized) onResize(prevWidth, prevHeight, width, height);

        calculateGlobalTransform();
        fixedUpdate(delta); // TODO: do the lag stuff
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

        // TODO: before rendering sort by input layer z
        children.sort(Widgets.widgetComparator);
        int maskingIndex = getMaskLevel();
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
            draw(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);
            renderer2D.endStencil();
        }
    }

    private boolean hitTest(float pointerX, float pointerY) {
        if (!isActive()) return false;
        if (!Input.mouse.isCursorInWindow()) return false;
        if (!inputRegion.containsPoint(pointerX, pointerY, transformScreen)) return false;

        Widget p = parent;
        boolean hit = true;
        while (p != null) {
            if (p.maskChildren()) {
                hit &= p.inputRegion.containsPoint(pointerX, pointerY, p.transformScreen);
            }
            p = p.parent;
        }

        return hit;
    }

    private boolean hitTestSubtree(float pointerX, float pointerY) {
        if (!isActive()) return false;
        if (!Input.mouse.isCursorInWindow()) return false;

        boolean hit = hitTest(pointerX, pointerY);
        for (final Widget child : children) {
            hit |= child.hitTestSubtree(pointerX, pointerY);
        }

        return hit;
    }

    private int getMaskLevel() {
        return maskLevel;
    }

    private void recalculateMaskIndex() {
        maskLevel = parent != null && parent.maskChildren() ? parent.maskLevel + 1 : 1;
        for (final Widget child : children) {
            child.recalculateMaskIndex();
        }
    }

    @Override
    public int getInputLayer() {
        return inputLayer;
    }

    @Override
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
    @Override
    public final boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();
        Widget target = findTopmostChildAt(pointerX, pointerY);
        inputMouseDownTarget = target;
        if (target == null) return false; // no target of the component tree was hit - mouse down outside the hierarchy

        while (target != null) { // travels to the top-most component that handles the event.
            if (target.inputEventListener.onMouseDown != null || target.inputEventListenerDefault.onMouseDown != null) break;
            if (target.inputEventListener.onMouseDoubleClick != null || target.inputEventListenerDefault.onMouseDoubleClick != null) break;
            //if (target.inputEventListener.onMouseDragStart != null || target.inputEventListenerDefault.onMouseDragStart != null) break;
            else target = target.getParent();
        }

        if (target == null) return true; // none of the components handle the event.


        Vector2 local = new Vector2(pointerX, pointerY);
        local.transform_TranslateRotateScale(-target.transformScreen.x, -target.transformScreen.y, -target.transformScreen.deg, 1 / target.transformScreen.sclX, 1/ target.transformScreen.sclY);

        // taking care of on mouse down event
        InputEventData.MouseDown mouseDown = new InputEventData.MouseDown(
                target,
                buttons.contains(Mouse.Button.LEFT, true),
                buttons.contains(Mouse.Button.RIGHT, true),
                buttons.contains(Mouse.Button.MIDDLE, true),
                local.x,
                local.y
        );
        if (target.inputEventListener.onMouseDown != null) {
            target.inputEventListener.onMouseDown.handle(mouseDown);
        }
        if (target.inputEventListenerDefault.onMouseDown != null) {
            target.inputEventListenerDefault.onMouseDown.handle(mouseDown);
        }

        // taking care of potential double click
        final Array<Mouse.Button> doubleClicks = Input.mouse.getButtonsDoubleClicked();
        if (!doubleClicks.isEmpty()) {
            InputEventData.MouseDoubleClick doubleClick = new InputEventData.MouseDoubleClick(
                    target,
                    buttons.contains(Mouse.Button.LEFT, true),
                    buttons.contains(Mouse.Button.RIGHT, true),
                    buttons.contains(Mouse.Button.MIDDLE, true),
                    local.x,
                    local.y
            );
            if (target.inputEventListener.onMouseDoubleClick != null) {
                target.inputEventListener.onMouseDoubleClick.handle(doubleClick);
            }
            if (target.inputEventListenerDefault.onMouseDoubleClick != null) {
                target.inputEventListenerDefault.onMouseDoubleClick.handle(doubleClick);
            }
        }

        return true;
    }

    @Override
    public final boolean mouseButtonsUp(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();
        Widget target = findTopmostChildAt(pointerX, pointerY);
        inputMouseUpTarget = target;
        if (target == null) return false; // no target of the component tree was hit.

        while (target != null) { // travels to the top-most component that handles the event.
            if (target.inputEventListener.onMouseUp != null || target.inputEventListenerDefault.onMouseUp != null) break;
            if (target.inputEventListener.onMouseClick != null || target.inputEventListenerDefault.onMouseClick != null) break;
            //if (target.inputEventListener.onMouseDragEnd != null || target.inputEventListenerDefault.onMouseDragEnd != null) break;
            else target = target.getParent();
        }

        if (target == null) return true; // none of the components handle the event.


        Vector2 local = new Vector2(pointerX, pointerY);
        local.transform_TranslateRotateScale(-target.transformScreen.x, -target.transformScreen.y, -target.transformScreen.deg, 1 / target.transformScreen.sclX, 1/ target.transformScreen.sclY);

        // taking care of on mouse up event
        InputEventData.MouseUp mouseUp = new InputEventData.MouseUp(
                target,
                buttons.contains(Mouse.Button.LEFT, true),
                buttons.contains(Mouse.Button.RIGHT, true),
                buttons.contains(Mouse.Button.MIDDLE, true),
                local.x,
                local.y
        );
        if (target.inputEventListener.onMouseUp != null) {
            target.inputEventListener.onMouseUp.handle(mouseUp);
        }
        if (target.inputEventListenerDefault.onMouseUp != null) {
            target.inputEventListenerDefault.onMouseUp.handle(mouseUp);
        }

        if (inputMouseDownTarget == inputMouseUpTarget) {
            InputEventData.MouseClick mouseClick = new InputEventData.MouseClick(
                    target,
                    buttons.contains(Mouse.Button.LEFT, true),
                    buttons.contains(Mouse.Button.RIGHT, true),
                    buttons.contains(Mouse.Button.MIDDLE, true),
                    local.x,
                    local.y
            );
            if (target.inputEventListener.onMouseClick != null) {
                target.inputEventListener.onMouseClick.handle(mouseClick);
            }
            if (target.inputEventListenerDefault.onMouseClick != null) {
                target.inputEventListenerDefault.onMouseClick.handle(mouseClick);
            }
        }

        return true;
    }

    @Override
    public final boolean mouseMoved(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY) {
        float pointerXPrevFrame = Widgets.getPointerXPrevFrame();
        float pointerYPrevFrame = Widgets.getPointerYPrevFrame();
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();

        boolean mouseInsideSubtreePrev = inputMouseInsideSubtree;
        inputMouseInsideSubtree = hitTestSubtree(pointerX, pointerY);
        boolean mouseJustEntered = !mouseInsideSubtreePrev && inputMouseInsideSubtree;
        boolean mouseJustLeft = mouseInsideSubtreePrev && !inputMouseInsideSubtree;

        if (mouseJustEntered && inputEventListener.onMouseEnter != null) {
            Vector2 local = new Vector2(pointerX, pointerY);
            Vector2 localPrevFrame = new Vector2(pointerXPrevFrame, pointerYPrevFrame);
            local.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            localPrevFrame.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            InputEventData.MouseEnter mouseEnter = new InputEventData.MouseEnter(this, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
            inputEventListener.onMouseEnter.handle(mouseEnter);
        }
        if (mouseJustEntered && inputEventListenerDefault.onMouseEnter != null) {
            Vector2 local = new Vector2(pointerX, pointerY);
            Vector2 localPrevFrame = new Vector2(pointerXPrevFrame, pointerYPrevFrame);
            local.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            localPrevFrame.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            InputEventData.MouseEnter mouseEnter = new InputEventData.MouseEnter(this, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
            inputEventListenerDefault.onMouseEnter.handle(mouseEnter);
        }
        if (mouseJustLeft && inputEventListener.onMouseLeave != null) {
            Vector2 local = new Vector2(pointerX, pointerY);
            Vector2 localPrevFrame = new Vector2(pointerXPrevFrame, pointerYPrevFrame);
            local.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            localPrevFrame.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            InputEventData.MouseLeave mouseEnter = new InputEventData.MouseLeave(this, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
            inputEventListener.onMouseLeave.handle(mouseEnter);
        }
        if (mouseJustLeft && inputEventListenerDefault.onMouseLeave != null) {
            Vector2 local = new Vector2(pointerX, pointerY);
            Vector2 localPrevFrame = new Vector2(pointerXPrevFrame, pointerYPrevFrame);
            local.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            localPrevFrame.transform_TranslateRotateScale(-this.transformScreen.x, -this.transformScreen.y, -this.transformScreen.deg, 1 / this.transformScreen.sclX, 1/ this.transformScreen.sclY);
            InputEventData.MouseLeave mouseEnter = new InputEventData.MouseLeave(this, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
            inputEventListenerDefault.onMouseLeave.handle(mouseEnter);
        }

        for (int i = 0; i < children.size; i++) {
            Widget child = children.get(i);
            child.mouseMoved(mouseX, mouseY, deltaMouseX, deltaMouseY);
        }
        return inputMouseInsideSubtree;
    }

    @Override
    public final boolean mouseScrolled(float scrollX, float scrollY) {
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();

        Widget target = findTopmostChildAt(pointerX, pointerY);
        if (target == null) return false; // no target of the component tree was hit.

        // travels to the top-most component that handles the event.
        while (target != null) {
            if (target.inputEventListener.onMouseScroll != null || target.inputEventListenerDefault.onMouseScroll != null) break;
            else target = target.getParent();
        }

        if (target == null) return true; // none of the components handle the event.

        Vector2 local = new Vector2(pointerX, pointerY);
        local.transform_TranslateRotateScale(-target.transformScreen.x, -target.transformScreen.y, -target.transformScreen.deg, 1 / target.transformScreen.sclX, 1/ target.transformScreen.sclY);

        InputEventData.MouseScroll eventData = new InputEventData.MouseScroll(
                target,
                scrollX,
                scrollY,
                local.x,
                local.y
        );
        if (target.inputEventListener.onMouseScroll != null) {
            target.inputEventListener.onMouseScroll.handle(eventData);
        }
        if (target.inputEventListenerDefault.onMouseScroll != null) {
            target.inputEventListenerDefault.onMouseScroll.handle(eventData);
        }

        return true;
    }

    @Override
    public final boolean mouseDragged(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY, @NotNull Array<Mouse.Button> buttons) {
        return InputEventHandler.super.mouseDragged(mouseX, mouseY, deltaMouseX, deltaMouseY, buttons);
    }

    @Override
    public final boolean keyboardKeysJustPressed(@NotNull Array<Keyboard.Key> keys) {
        return InputEventHandler.super.keyboardKeysJustPressed(keys);
    }

    @Override
    public final boolean keyboardKeysJustReleased(@NotNull Array<Keyboard.Key> keys) {
        return InputEventHandler.super.keyboardKeysJustReleased(keys);
    }

    @Override
    public final boolean keyboardCodepointsTyped(@NotNull ArrayChar codepoints) {
        return InputEventHandler.super.keyboardCodepointsTyped(codepoints);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + ID;
    }

    /*** register event listeners ***/
    public final void onMouseDown(InputEventListener.OnMouseDown listener) {
        inputEventListener.onMouseDown = listener;
    }

    public final void onMouseDownDefault(InputEventListener.OnMouseDown listener) {
        inputEventListenerDefault.onMouseDown = listener;
    }

    public final void onMouseUp(InputEventListener.OnMouseUp listener) {
        inputEventListener.onMouseUp = listener;
    }

    public final void onMouseUpDefault(InputEventListener.OnMouseUp listener) {
        inputEventListenerDefault.onMouseUp = listener;
    }

    public final void onMouseClick(InputEventListener.OnMouseClick listener) {
        inputEventListener.onMouseClick = listener;
    }

    public final void onMouseClickDefault(InputEventListener.OnMouseClick listener) {
        inputEventListenerDefault.onMouseClick = listener;
    }

    public final void onMouseDoubleClick(InputEventListener.OnMouseDoubleClick listener) {
        inputEventListener.onMouseDoubleClick = listener;
    }

    public final void onMouseClickDoubleDefault(InputEventListener.OnMouseDoubleClick listener) {
        inputEventListenerDefault.onMouseDoubleClick = listener;
    }

    public final void onMouseEnter(InputEventListener.OnMouseEnter listener) {
        inputEventListener.onMouseEnter = listener;
    }

    public final void onMouseEnterDefault(InputEventListener.OnMouseEnter listener) {
        inputEventListenerDefault.onMouseEnter = listener;
    }

    public final void onMouseLeave(InputEventListener.OnMouseLeave listener) {
        inputEventListener.onMouseLeave = listener;
    }

    public final void onMouseLeaveDefault(InputEventListener.OnMouseLeave listener) {
        inputEventListenerDefault.onMouseLeave = listener;
    }


    public final void onMouseScroll(InputEventListener.OnMouseScroll listener) {
        inputEventListener.onMouseScroll = listener;
    }

    public final void onMouseScrollDefault(InputEventListener.OnMouseScroll listener) {
        inputEventListenerDefault.onMouseScroll = listener;
    }

}
