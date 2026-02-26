package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import org.jetbrains.annotations.NotNull;

public class WidgetNodeContainerVertical extends WidgetNode implements WidgetNodeContainer {

    /* state */ // TODO
    private float scrollOffsetY    = 0;
    private float backgroundWidth  = 0;
    private float backgroundHeight = 0;

    /* box container layout */
    public Sizing    layoutWidthSizing            = Sizing.DYNAMIC;
    public float     layoutWidth                  = 1;
    public float     layoutWidthMin               = 0;
    public float     layoutWidthMax               = Float.POSITIVE_INFINITY;
    public Sizing    layoutHeightSizing           = Sizing.DYNAMIC;
    public float     layoutHeight                 = 1;
    public float     layoutHeightMin              = 0;
    public float     layoutHeightMax              = Float.POSITIVE_INFINITY;
    public boolean   layoutAddScrollbar           = true;
    public Overflow  layoutOverflowX              = Overflow.HIDDEN;
    public Overflow  layoutOverflowY              = Overflow.HIDDEN;

    /* box container style */
    public boolean   boxBackgroundVisible         = Widgets.themeContainerBoxBackgroundVisible;
    public Color     boxBackgroundColor           = Widgets.themeContainerBoxBackgroundColor.clone();
    public int       boxPaddingTop                = Widgets.themeContainerBoxPaddingTop;
    public int       boxPaddingBottom             = Widgets.themeContainerBoxPaddingBottom;
    public int       boxPaddingLeft               = Widgets.themeContainerBoxPaddingLeft;
    public int       boxPaddingRight              = Widgets.themeContainerBoxPaddingRight;
    public int       boxChildSpacingVertical      = Widgets.themeContainerBoxChildSpacingVertical;
    public int       boxChildSpacing              = Widgets.themeContainerBoxChildSpacingHorizontal;
    public int       boxCornerRadiusTopLeft       = Widgets.themeContainerBoxCornerRadiusTopLeft;
    public int       boxCornerRadiusTopRight      = Widgets.themeContainerBoxCornerRadiusTopRight;
    public int       boxCornerRadiusBottomRight   = Widgets.themeContainerBoxCornerRadiusBottomRight;
    public int       boxCornerRadiusBottomLeft    = Widgets.themeContainerBoxCornerRadiusBottomLeft;
    public int       boxCornerSegmentsTopLeft     = Widgets.themeContainerBoxCornerSegmentsTopLeft;
    public int       boxCornerSegmentsTopRight    = Widgets.themeContainerBoxCornerSegmentsTopRight;
    public int       boxCornerSegmentsBottomRight = Widgets.themeContainerBoxCornerSegmentsBottomRight;
    public int       boxCornerSegmentsBottomLeft  = Widgets.themeContainerBoxCornerSegmentsBottomLeft;
    public int       boxBorderSize                = Widgets.themeContainerBoxBorderSize;
    public Color     boxBorderColor               = Widgets.themeContainerBoxBorderColor.clone();

    /* scrollbar */
    //private WidgetInputScrollbar_old scrollbar = new WidgetInputScrollbar_old();
    private final WidgetNodeInputScrollbar scrollbar = new WidgetNodeInputScrollbar();

    public WidgetNodeContainerVertical() {
        addChild(scrollbar);
        scrollbar.anchor = Anchor.PARENT_TOP_RIGHT;
    }

    @Override
    protected boolean onResizeDefault(Event.EventResize e) {
        scrollbar.length = backgroundHeight;
        scrollbar.anchor = Anchor.PARENT_TOP_RIGHT;
        scrollbar.anchorY = boxBorderSize;
        scrollbar.anchorX = 0;
        scrollbar.type = WidgetNodeInputScrollbar.Type.VERTICAL;
        return true;
    }

    // this will make sure the scrollbar is always on top.
    @Override
    protected boolean onChildAddedDefault(Event.EventChildAdded e) {
        int scrollbarChildIndex = children.indexOf(scrollbar, true);
        children.set(scrollbarChildIndex, e.node, true);
        children.set(children.size - 1, scrollbar, true);
        return true;
    }

    /*** default event handlers */
    @Override
    protected boolean onMouseScrollDefault(Event.EventMouseScroll e) {
        if (scrollbar.active) scrollbar.scroll(e.scrollValue * 0.1f);
        return true;
    }

    /*** global container logic ***/
    // in order to add logic, just override the fixedUpdateContainer() method instead.
    @Override
    protected final void fixedUpdate(float delta) {
        float contentHeight = getContentHeight(childrenLayout);
        float verticalOverflow = height - contentHeight - boxPaddingTop - boxPaddingBottom;

        scrollbar.active = layoutAddScrollbar;
        if (verticalOverflow >= 0) scrollbar.active = false;

        if (!scrollbar.active) {
            scrollOffsetY = 0; // reset scroll value if scrolling is disabled.
        } else {
            scrollbar.length = backgroundHeight;
            scrollbar.anchor = Anchor.PARENT_TOP_RIGHT;
            scrollbar.anchorY = boxBorderSize;
            scrollbar.anchorX = 0;
            scrollbar.type = WidgetNodeInputScrollbar.Type.VERTICAL;
            scrollOffsetY = -scrollbar.getValue() * verticalOverflow; // TODO
        }

        backgroundWidth = Math.max(0, getWidth() - boxBorderSize * 2);
        backgroundHeight = Math.max(0, getHeight() - boxBorderSize * 2);

        fixedUpdateContainer(delta);
    }

    /*** children layout ***/

    @Override
    protected void setChildrenOffsets(@NotNull Array<WidgetNode> widgets) {
        float sclY = 1; // global transform
        float position_y = (getHeight() * 0.5f - boxBorderSize - boxPaddingTop) * sclY + scrollOffsetY;
        for (WidgetNode child : widgets) {
            float child_height = child.getHeight() * sclY;
            child.offsetX = boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;
            child.offsetY = position_y - child_height * 0.5f;
            position_y -= child_height + boxChildSpacingVertical * sclY;
        }
    }

    // TODO: cache results of backgroundWidth and backgroundHeight
    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (boxBackgroundVisible) {
            renderer2D.setColor(boxBackgroundColor);
            renderer2D.drawRectangleFilled(backgroundWidth, backgroundHeight,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                    x, y, deg, sclX, sclY);
        }
        if (boxBorderSize > 0) {
            renderer2D.setColor(boxBorderColor);
            renderer2D.drawRectangleBorder(backgroundWidth, backgroundHeight, boxBorderSize,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                    x, y, deg, sclX, sclY);
        }
    }

    @Override
    protected void drawMask(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        float backgroundWidth = Math.max(0, getWidth() - boxBorderSize * 2); // TODO: not here
        float backgroundHeight = Math.max(0, getHeight() - boxBorderSize * 2); // TODO: not here.

        final float windowMaxExtent = Math.max(Graphics.getWindowWidth(), Graphics.getWindowHeight());
        final float fullScreenMask = 2 * windowMaxExtent;
        float maskWidth  = layoutOverflowX == Overflow.VISIBLE ? fullScreenMask : backgroundWidth;
        float maskHeight = layoutOverflowY == Overflow.VISIBLE ? fullScreenMask : backgroundHeight;
        renderer2D.drawRectangleFilled(maskWidth, maskHeight,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                x, y, deg, sclX, sclY);
    }

    @Override
    protected void configureInputRegion(@NotNull Region region) {
        // TODO: optimize using calculatedWidth and calculatedHeight
        float width = getWidth();
        float height = getHeight();
        region.setToRectangle(
                width, height,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft
        );
    }

    @Override
    protected void configureInputMaskedRegion(@NotNull Region maskedRegion) {
        // case: mask matching container shape
        if (layoutOverflowX != Overflow.VISIBLE && layoutOverflowY != Overflow.VISIBLE) {
            float backgroundWidth = Math.max(0, getWidth() - boxBorderSize * 2); // TODO: not here
            float backgroundHeight = Math.max(0, getHeight() - boxBorderSize * 2); // TODO: not here.
            maskedRegion.setToRectangle(
                    backgroundWidth, backgroundHeight,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft
            );
            return;
        }

        // case: full-screen mask
        if (layoutOverflowX == Overflow.VISIBLE && layoutOverflowY == Overflow.VISIBLE) {
            float windowMaxExtent = Math.max(Graphics.getWindowWidth(), Graphics.getWindowHeight());
            float fullScreenMask = 2 * windowMaxExtent;
            maskedRegion.setToRectangle(fullScreenMask, fullScreenMask,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft);
            return;
        }

        // case: trim only top and bottom
        if (layoutOverflowX == Overflow.VISIBLE) {
            float windowMaxExtent = Math.max(Graphics.getWindowWidth(), Graphics.getWindowHeight());
            float fullScreenMask = 2 * windowMaxExtent;
            float backgroundHeight = Math.max(0, getHeight() - boxBorderSize * 2); // TODO: not here.
            maskedRegion.setToRectangle(fullScreenMask, backgroundHeight,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft);
            return;
        }

        // case: trim only left and right
        if (layoutOverflowY == Overflow.VISIBLE) {
            float windowMaxExtent = Math.max(Graphics.getWindowWidth(), Graphics.getWindowHeight());
            float fullScreenMask = 2 * windowMaxExtent;
            float backgroundWidth = Math.max(0, getWidth() - boxBorderSize * 2); // TODO: not here
            maskedRegion.setToRectangle(backgroundWidth, fullScreenMask,
                    boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                    boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                    boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                    boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft);
            return;
        }
    }

    @Override
    public final float getContentWidth(Array<WidgetNode> widgets) {
        float maxWidth = 0;
        for (WidgetNode child : widgets) {
            maxWidth = Math.max(child.getWidth(), maxWidth);
        }
        return Math.abs(maxWidth);
    }

    @Override
    public float getContentHeight(Array<WidgetNode> widgets) {
        float height = 0;
        for (WidgetNode child : widgets) {
            height += child.getHeight();
        }
        height += Math.max(0f, boxChildSpacingVertical * (widgets.size - 1));
        return height;
    }

    @Override
    protected float getWidth() {
        float width = switch (layoutWidthSizing) {
            case STATIC   -> layoutWidth;
            case VIEWPORT -> layoutWidth * Graphics.getWindowWidth();
            case DYNAMIC  -> getContentWidth(childrenLayout) + boxPaddingLeft + boxPaddingRight + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(width, layoutWidthMin, layoutWidthMax);
    }

    @Override
    protected float getHeight() {
        float height = switch (layoutHeightSizing) {
            case STATIC   -> layoutHeight;
            case VIEWPORT -> layoutHeight * Graphics.getWindowHeight();
            case DYNAMIC  -> getContentHeight(childrenLayout) + boxPaddingTop + boxPaddingBottom + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(height, layoutHeightMin, layoutHeightMax);
    }

    /*** MASKING ***/

    @Override
    public final boolean maskChildren() {
        return layoutOverflowX == Overflow.HIDDEN || layoutOverflowY == Overflow.HIDDEN;
    }

}
