package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.widgets.Node;
import com.heavybox.jtix.widgets.NodeContainer;

/*
Follows CSS' box model, more or less -

draw():
 -----------------------------------------------------------
|                           border                         |
|          --------------------------------------          |
|         |               padding top            |         |
|         |          p0----------------p1        |         |
|         |  padding  |                | padding |         |
| border  |   left    |                |  right  |  border |
|         |           |    content     |         |         |
|         |           |    render()    |         |         |
|         |           |                |         |         |
|         |          p3----------------p2        |         |
|         |             padding bottom           |         |
|          --------------------------------------          |
|                           border                         |
 -----------------------------------------------------------

 */
public abstract class Widget {

    /* hierarchy properties */
    protected Widget        parent   = null;
    protected Array<Widget> children = new Array<>(true, 1);
    public    boolean       active   = true;

    /* positioning and input */
    protected Layout    layout          = Layout.STACK;
    protected float     offsetX         = 0; // set by parent
    protected float     offsetY         = 0; // set by parent
    protected Transform localTransform  = new Transform();
    protected Transform globalTransform = new Transform(); // calculated.
    protected Region    region          = new Region();

    /* box properties */
    public Sizing    boxWidthSizing               = Sizing.DYNAMIC;
    public float     boxWidthMin                  = 0;
    public float     boxWidthMax                  = Float.POSITIVE_INFINITY;
    public float     boxWidth                     = 1;
    public Sizing    boxHeightSizing              = Sizing.DYNAMIC;
    public float     boxHeightMin                 = 0;
    public float     boxHeightMax                 = Float.POSITIVE_INFINITY;
    public float     boxHeight                    = 1;
    public Overflow  boxContentOverflowX          = Overflow.HIDDEN;
    public Overflow  boxContentOverflowY          = Overflow.HIDDEN;
    public Color     boxBackgroudColor            = Color.valueOf("#007BFF");
    public boolean   boxBackgroundEnabled         = true;
    public int       boxPaddingTop                = 80;
    public int       boxPaddingBottom             = 20;
    public int       boxPaddingLeft               = 0;
    public int       boxPaddingRight              = 0;
    public int       boxCornerRadiusTopLeft       = 0;
    public int       boxCornerRadiusTopRight      = 0;
    public int       boxCornerRadiusBottomRight   = 0;
    public int       boxCornerRadiusBottomLeft    = 0;
    public int       boxCornerSegmentsTopLeft     = 10;
    public int       boxCornerSegmentsTopRight    = 10;
    public int       boxCornerSegmentsBottomRight = 10;
    public int       boxCornerSegmentsBottomLeft  = 10;
    public int       boxBorderSize                = 8;
    public Color     boxBorderColor               = Color.RED.clone();

    protected Widget() {

    }

    /*** HIERARCHY OPERATIONS ***/

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

    /*** METRICS OPERATIONS ***/
    protected abstract float getInnerWidth();
    protected abstract float getInnerHeight();

    final void calculateBoxMetrics() {


    }

    public final float calculateTotalWidth() {
        if (layout == null || layout == Layout.STACK) {

        }

        if (layout == Layout.HORIZONTAL) {

        }

        if (layout == Layout.VERTICAL) {

        }

        return 0;
    }

    private void calculateGlobalTransform() {
        //int refZIndex = container == null ? this.zIndex : this.zIndex + container.screenZIndex;
        float refX = parent == null ? 0 : parent.globalTransform.x;
        float refY = parent == null ? 0 : parent.globalTransform.y;
        float refDeg = parent == null ? 0 : parent.globalTransform.deg;
        float refSclX = parent == null ? 1 : parent.globalTransform.sclX;
        float refSclY = parent == null ? 1 : parent.globalTransform.sclY;
        float cos = MathUtils.cosDeg(refDeg);
        float sin = MathUtils.sinDeg(refDeg);
        float x = this.localTransform.x * cos - this.localTransform.y * sin;
        float y = this.localTransform.x * sin + this.localTransform.y * cos;
        //screenZIndex = refZIndex + this.zIndex;
        globalTransform.x = refX + x * refSclX + offsetX * cos - offsetY * sin; // add the rotated offset vector x component
        globalTransform.y = refY + y * refSclY + offsetX * sin + offsetY * cos; // add the rotated offset vector y component
        globalTransform.deg  = this.localTransform.deg + refDeg;
        globalTransform.sclX = this.localTransform.sclX * refSclX;
        globalTransform.sclY = this.localTransform.sclY * refSclY;
    }

    /*** RENDERING ***/
    protected abstract void renderPrimitive(Renderer2D renderer2D, Transform globalTransform);

    public final void render(Renderer2D renderer2D) {
        render(renderer2D, 0);
    }

    protected final void render(Renderer2D renderer2D, int maskingIndex) {
        // placeholder
        float backgroundWidth = 200;
        float backgroundHeight = 50;

        if (boxBackgroundEnabled) {
            renderer2D.setColor(boxBackgroudColor);
            renderer2D.drawRectangleFilled(backgroundWidth, backgroundHeight,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                    globalTransform.x, globalTransform.y, globalTransform.deg, globalTransform.sclX, globalTransform.sclY);
        }
        if (boxBorderSize > 0) {
            renderer2D.setColor(boxBorderColor);
            renderer2D.drawRectangleBorder(backgroundWidth, backgroundHeight, boxBorderSize,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                    globalTransform.x, globalTransform.y, globalTransform.deg, globalTransform.sclX, globalTransform.sclY);
        }

        // write mask
        renderer2D.beginStencil();
        renderer2D.setStencilModeIncrement();
        final float windowMaxExtent = Math.max(Graphics.getWindowWidth(), Graphics.getWindowHeight());
        final float fullScreenMask = 2 * windowMaxExtent;
        float maskWidth  = boxContentOverflowX == Overflow.VISIBLE ? fullScreenMask : backgroundWidth;
        float maskHeight = boxContentOverflowY == Overflow.VISIBLE ? fullScreenMask : backgroundHeight;
        renderer2D.drawRectangleFilled(maskWidth, maskHeight,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                globalTransform.x, globalTransform.y, globalTransform.deg, globalTransform.sclX, globalTransform.sclY);
        renderer2D.endStencil(); // end mask

        // apply mask
        renderer2D.enableMasking(); // enable masking
        renderer2D.setMaskingFunctionEquals(maskingIndex); // TODO: should be greater equals?
        for (Widget child : children) {
            if (!child.active) continue;
            child.render(renderer2D, maskingIndex + 1);
        }
        renderer2D.disableMasking(); // disable masking
        renderPrimitive(renderer2D, globalTransform); // TODO: masking here is a problem.

        // erase mask
        renderer2D.beginStencil();
        renderer2D.setStencilModeDecrement();
        renderer2D.drawRectangleFilled(maskWidth, maskHeight,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                globalTransform.x, globalTransform.y, globalTransform.deg, globalTransform.sclX, globalTransform.sclY);
        renderer2D.endStencil();

        // draw scrollbars
    }

    /**** ENUMS ****/

    // controls the children's layout
    public enum Layout {
        STACK, // stack on top of each-other at the center
        VERTICAL, // place elements from top to bottom, while taking box model into account (padding, border, ...)
        HORIZONTAL, // place elements from left to right, while taking box model into account (padding, border, ...)
        ;
    }

    // controls the box sizing
    public enum Sizing {
        STATIC,  // Hard-coded value in pixels. The size remains constant even if content overflows or fits with extra space.
        DYNAMIC, // The widget box will set its size to completely fit its children.
        ;
    }

    // controls how it renders the contents of the widget that overflow the box
    public enum Overflow {
        VISIBLE,   // does nothing, renders while ignoring the bounds
        HIDDEN,    // uses glScissors to clip the content, so only the pixels that land inside the box render. The rest get trimmed.
        SCROLLBAR, // trims the content and adds scrollbars
        ;
    }

    // anchors ensure spacing between a widget and its parent's edges.
    // when the parent is null, it's the window edges.
    // this is important to make the ui responsive.
    public enum Anchor {
        TOP_LEFT,    TOP_CENTER,    TOP_RIGHT,
        CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
        ;
    }

}
