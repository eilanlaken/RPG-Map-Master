package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.InputEventHandler;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Transform2D;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.widgets.WidgetsException;
import org.jetbrains.annotations.NotNull;

public abstract class Widget implements InputEventHandler {


    /*** metrics: transform and dimensions ***/
    private        float      width           = 0; // TODO: use for caching and event handling
    private        float      height          = 0; // TODO: use for caching and event handling
    private        float      prevWidth       = 0; // TODO: use for caching and event handling
    private        float      prevHeight      = 0; // TODO: use for caching and event handling
    public  final Transform2D transform       = new Transform2D(); // used for absolute positioning from root and animations
    public        float       offsetX         = 0; // set by the parent or anchor.
    public        float       offsetY         = 0; // set by the parent or anchor.
    private final Transform2D transformScreen = new Transform2D(); // calculated every frame either by self or parent
    public        Anchor      anchor          = null;
    public        float       anchorX         = 0;
    public        float       anchorY         = 0;
    public        boolean     draggableX      = false;
    public        boolean     draggableY      = false;
    private       int         maskLevel       = 1;
    private       boolean     updated         = false; // TODO: see when to reset the flag. FIXME NEXT

    /*** ui hierarchy ***/
    private       Widget        parent          = null;
    public        boolean       active          = true;
    final         Array<Widget> children        = new Array<>();
    private final Array<Widget> childrenLayout  = new Array<>();

    /*** input handling ***/
    public        int                inputLayerIndex           = 1;
    private       int                inputLayer                = 1;
    public        boolean            preventDefault            = false;
    private final InputRegion        inputRegion               = new InputRegion();
    private final InputRegion        inputRegionMask           = new InputRegion();
    private final Array<InputRegion> inputRegionsAncestors     = new Array<>(false, 3);
    private       InputEventListener inputEventListener        = null; // TODO: change into private and add register listener method
    private       InputEventListener inputEventListenerDefault = null;


    public Widget() {
        // register as input listener etc.?

    }

    protected abstract void  draw    (Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY);
    protected          void  drawMask(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) { draw(renderer2D, x, y, deg, sclX, sclY); }
    protected abstract float getWidth();
    protected abstract float getHeight();

    protected void fixedUpdate(float delta) {} // TODO: call with accumulative error
    protected void onChildAdded(Widget child) {}
    protected void onChildRemoved(Widget child) {}
    protected void onResize(final float prevWidth, final float prevHeight, final float newWidth, final float newHeight) {}

    protected void configureInputRegion(final @NotNull InputRegion region) {
        region.setToRectangle(getWidth(), getHeight());
    }

    protected void configureInputMaskedRegion(final @NotNull InputRegion maskedRegion) {
        configureInputRegion(maskedRegion);
    }

    final Widget getParent() {
        return parent;
    }

    public final boolean isRoot() {
        return parent == null;
    }

    /*** Add and remove child methods ***/
    // TODO: test
    public final void addChild(Widget child) {
        if (child == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (child == this) throw new WidgetsException("Trying to parent a " + Widget.class.getSimpleName() + " to itself.");
        if (Widgets.isXAncestorOfY(child,this)) throw new WidgetsException("Cannot add an ancestor widget as a child, as this would create a cyclic hierarchy.");
        if (children.contains(child,true)) throw new WidgetsException("Widget " + child.getClass().getSimpleName() + " is already a child of widget.");

        if (child.parent != null) child.parent.removeChild(child);
        children.add(child);
        child.parent = this;
        child.recalculateInputLayer();
        child.recalculateMaskIndex();

        onChildAdded(child);
    }

    // TODO: test
    public final void removeChild(Widget child) {
        if (child == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (!children.contains(child, true)) throw new WidgetsException(Widget.class.getSimpleName() + " does not contain the element " + child + " as a child so it cannot be removed.");

        children.removeValue(child,true);
        child.parent = null;
        child.recalculateInputLayer();
        child.recalculateMaskIndex();

        onChildRemoved(child);
    }

    public void anchorSet(Anchor anchor, float anchorX, float anchorY) {
        this.anchor = anchor;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
    }

    public void anchorRemove() {
        anchor = null;
    }

    // containers can override this, for example.
    // takes an array of child widgets and sets their layout
    protected void setChildrenOffsets(final Array<Widget> childrenLayout) {
        for (Widget widget : childrenLayout) {
            widget.offsetX = 0;
            widget.offsetY = 0;
        }
    }

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

    public boolean maskChildren() { return false; }

    final void update(float delta) {
        if (!active) return;

        configureInputRegion(inputRegion);
        configureInputMaskedRegion(inputRegionMask);

        updated = false;
        childrenLayout.clear();
        for (Widget child : children) {
            if (child.anchor == null) childrenLayout.add(child);
        }
        setChildrenOffsets(childrenLayout);
        setOffsetsAnchor();
        prevWidth = width;
        prevHeight = height;
        width = getWidth();
        height = getHeight();

        float deltaWidth  = width - prevWidth;
        float deltaHeight = height - prevHeight;
        boolean resized = !MathUtils.isZero(deltaWidth) || !MathUtils.isZero(deltaHeight);
        if (resized) onResize(prevWidth, prevHeight, width, height);

        calculateGlobalTransform();
        fixedUpdate(delta); // TODO: do the lag stuff
        updated = true;
        // update children
        for (Widget child : children) {
            child.update(delta);
        }
    }

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
            drawMask(renderer2D, transformScreen.x, transformScreen.y, transformScreen.deg, transformScreen.sclX, transformScreen.sclY);
            renderer2D.endStencil();
        }
    }

    private boolean hitTest(float pointerX, float pointerY) {
        if (!inputRegion.containsPoint(pointerX, pointerY, transformScreen)) return false;
        if (!isActive()) return false;

        inputRegionsAncestors.clear();
        Widget p = parent;
        while (p != null) {
            if (p.maskChildren()) {
                inputRegionsAncestors.add(p.inputRegionMask);
            }
            p = p.parent;
        }

        boolean hit = true;
        for (InputRegion ancestorRegion : inputRegionsAncestors) {
            hit &= ancestorRegion.containsPoint(pointerX, pointerY, transformScreen);
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

    private void recalculateInputLayer() {
        inputLayer = parent == null ? inputLayerIndex : parent.inputLayer + inputLayerIndex;
        for (final Widget child : children) {
            child.recalculateInputLayer();
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

    // only propagate up if the ui element is not root
    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        float pointerX = Widgets.getPointerX();
        float pointerY = Widgets.getPointerY();
        boolean mouseInside = hitTest(pointerX, pointerY);
        if (!mouseInside) {
            // clicked outside event
            return false;
        }

        Vector2 local = new Vector2(pointerX, pointerY);
        local.transform_TranslateRotateScale(-transformScreen.x, -transformScreen.y, -transformScreen.deg, 1 / transformScreen.sclX, 1/ transformScreen.sclY);
        InputEventData.MouseDown e = new InputEventData.MouseDown(this);
        e.mouseLocalX = local.x;
        e.mouseLocalY = local.y;
        e.buttonLeft = buttons.contains(Mouse.Button.LEFT, true);
        e.buttonRight = buttons.contains(Mouse.Button.RIGHT, true);;
        e.buttonMiddle = buttons.contains(Mouse.Button.MIDDLE, true);;
        if (inputEventListener != null && inputEventListener.onMouseDown != null) inputEventListener.onMouseDown.handle(e);
        if (!preventDefault && inputEventListenerDefault != null && inputEventListenerDefault.onMouseDown != null) inputEventListenerDefault.onMouseDown.handle(e);

        return isRoot();
    }

    @Override
    public boolean mouseButtonsUp(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        return InputEventHandler.super.mouseButtonsUp(mouseX, mouseY, buttons);
    }

    @Override
    public boolean mouseMoved(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY) {
        return InputEventHandler.super.mouseMoved(mouseX, mouseY, deltaMouseX, deltaMouseY);
    }

    @Override
    public boolean mouseScrolled(float scrollX, float scrollY) {
        return InputEventHandler.super.mouseScrolled(scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY, @NotNull Array<Mouse.Button> buttons) {
        return InputEventHandler.super.mouseDragged(mouseX, mouseY, deltaMouseX, deltaMouseY, buttons);
    }

    @Override
    public boolean keyboardKeysJustPressed(@NotNull Array<Keyboard.Key> keys) {
        return InputEventHandler.super.keyboardKeysJustPressed(keys);
    }

    @Override
    public boolean keyboardKeysJustReleased(@NotNull Array<Keyboard.Key> keys) {
        return InputEventHandler.super.keyboardKeysJustReleased(keys);
    }

    @Override
    public boolean keyboardCodepointsTyped(@NotNull ArrayChar codepoints) {
        return InputEventHandler.super.keyboardCodepointsTyped(codepoints);
    }

    /*** register event listeners ***/
    public void onMouseDown(InputEventListener.OnMouseDown listener) {
        if (inputEventListener == null) inputEventListener = new InputEventListener();
        inputEventListener.onMouseDown = listener;
    }

    public void onMouseDownDefault(InputEventListener.OnMouseDown listener) {
        if (inputEventListenerDefault == null) inputEventListenerDefault = new InputEventListener();
        inputEventListenerDefault.onMouseDown = listener;
    }

}
