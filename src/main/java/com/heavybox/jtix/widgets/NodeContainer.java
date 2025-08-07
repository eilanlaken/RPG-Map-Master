package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;

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
public class NodeContainer extends Node {

    private final Array<Node> children = new Array<>(false, 5);

    public Sizing    boxWidthSizing               = Sizing.DYNAMIC;
    public float     boxWidthMin                  = 0;
    public float     boxWidthMax                  = Float.POSITIVE_INFINITY;
    public float     boxWidth                     = 1;
    public Sizing    boxHeightSizing              = Sizing.DYNAMIC;
    public float     boxHeightMin                 = 0;
    public float     boxHeightMax                 = Float.POSITIVE_INFINITY;
    public float     boxHeight                    = 1;
    public Overflow  contentOverflowX             = Overflow.HIDDEN;
    public Overflow  contentOverflowY             = Overflow.HIDDEN;
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

    // inner state
    private float calculatedWidth;
    private float calculatedHeight;
    private float backgroundWidth;
    private float backgroundHeight;
    private boolean overflowX;
    private boolean overflowY;
    private float scrollX;
    private float scrollY;

    public NodeContainer() {

    }

    public void addChild(Node child) {
        if (child == null) throw new WidgetsException(Node.class.getSimpleName() + " element cannot be null.");
        if (child == this) throw new WidgetsException("Trying to parent a " + Node.class.getSimpleName() + " to itself.");
        children.add(child);
        child.container = this;
    }

    protected void removeChild(Node node) {
        node.container = null;
        children.removeValue(node, true);
    }

    @Override
    protected final void fixedUpdate(float delta) {
        setChildrenOffset(children);
        calculatedWidth = calculateWidth();
        calculatedHeight = calculateHeight();
        backgroundWidth = Math.max(0, calculatedWidth - boxBorderSize * 2);
        backgroundHeight = Math.max(0, calculatedHeight - boxBorderSize * 2);
        // update overflowX amd overflowY
        for (Node child : children) {
            if (!child.active) continue;
            child.update(delta);
        }
        updateContainer();
    }

    protected void updateContainer() {

    }

    @Override
    protected void frameUpdate() {
        for (Node child : children) {
            if (!child.active) continue;
            child.handleInput();
        }
    }

    protected void setChildrenOffset(final Array<Node> children) {
        for (Node child : children) {
            if (!child.active) continue;
            child.offsetX = boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;
            child.offsetY = boxPaddingBottom - (boxPaddingBottom + boxPaddingTop) * 0.5f;
        }
    }

    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (boxBackgroundEnabled) {
            renderer2D.setColor(boxBackgroudColor);
            renderer2D.drawRectangleFilled(backgroundWidth, backgroundHeight,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                    screenX, screenY, screenDeg, screenSclX, screenSclY);
        }
        if (boxBorderSize > 0) {
            renderer2D.setColor(boxBorderColor);
            renderer2D.drawRectangleBorder(backgroundWidth, backgroundHeight, boxBorderSize,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                    screenX, screenY, screenDeg, screenSclX, screenSclY);
        }

        // write mask
        renderer2D.beginStencil();
        renderer2D.setStencilModeIncrement();
//        renderer2D.setStencilModeSetOnes();
        final float windowMaxExtent = Math.max(Graphics.getWindowWidth(), Graphics.getWindowHeight());
        final float fullScreenMask = 2 * windowMaxExtent;
        float maskWidth  = contentOverflowX == Overflow.VISIBLE ? fullScreenMask : backgroundWidth;
        float maskHeight = contentOverflowY == Overflow.VISIBLE ? fullScreenMask : backgroundHeight;
        renderer2D.drawRectangleFilled(maskWidth, maskHeight,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                screenX, screenY, screenDeg, screenSclX, screenSclY);
        renderer2D.endStencil(); // end mask

        // apply mask
        int maskingIndex = getMaskingIndex();
        for (Node child : children) {
            if (!child.active) continue;
            renderer2D.enableMasking(); // enable masking
            renderer2D.setMaskingFunctionEquals(maskingIndex); // TODO: instead of 1, put the correct value for masking.
            child.draw(renderer2D);
            renderer2D.disableMasking(); // disable masking
        }

        // erase mask
        renderer2D.beginStencil();
        renderer2D.setStencilModeDecrement();
        renderer2D.drawRectangleFilled(maskWidth, maskHeight,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                screenX, screenY, screenDeg, screenSclX, screenSclY);
        renderer2D.endStencil();

        // draw scrollbars
    }

    protected float getContentWidth(final Array<Node> children) {
        float max_x = 0;
        for (Node child : children) {
            if (!child.active) continue;
            max_x = Math.max(child.calculateWidth(), max_x);
        }
        return max_x;
    }

    protected float getContentHeight(final Array<Node> children) {
        float max_y = Float.NEGATIVE_INFINITY;
        for (Node child : children) {
            if (!child.active) continue;
            max_y = Math.max(child.calculateHeight(), max_y);
        }
        return max_y;
    }

    @Override
    public float calculateWidth() {
        float width = switch (boxWidthSizing) {
            case STATIC   -> boxWidth;
            case VIEWPORT -> boxWidth * Graphics.getWindowWidth();
            case DYNAMIC  -> getContentWidth(children) + boxPaddingLeft + boxPaddingRight + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(width, boxWidthMin, boxWidthMax);
    }

    @Override
    public float calculateHeight() {
        float height = switch (boxHeightSizing) {
            case STATIC   -> boxHeight;
            case VIEWPORT -> boxHeight * Graphics.getWindowHeight();
            case DYNAMIC  -> getContentHeight(children) + boxPaddingTop + boxPaddingBottom + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(height, boxHeightMin, boxHeightMax);
    }

    @Override
    protected void setPolygon(final Polygon polygon) {
        polygon.setToRectangle(
                calculatedWidth, calculatedHeight,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft
        );
    }

    /* controls how it renders the contents of the widget that overflow the box */
    public enum Overflow {
        VISIBLE,    // does nothing, renders while ignoring the bounds
        HIDDEN,      // uses glScissors to clip the content, so only the pixels that land inside the box render. The rest get trimmed.
        SCROLLBAR, // trims the content and adds scrollbars
    }

    public enum Sizing {
        STATIC,   // explicitly set by width and height
        DYNAMIC,  // conforms to fit content
        VIEWPORT, // relative to the viewport (width or height)
    }

}
