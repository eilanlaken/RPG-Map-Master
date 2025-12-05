package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import org.jetbrains.annotations.NotNull;

public class WidgetContainer extends Widget {

    /* state */ // TODO
    private float scrollOffsetX    = 0;
    private float scrollOffsetY    = 0;
    private float backgroundWidth  = 0;
    private float backgroundHeight = 0;

    /* box container layout */
    public Layout    layout                       = Layout.STACK;
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
    public Color     boxBackgroundColor           = Widgets.themeContainerBoxBackgroundColor;
    public int       boxPaddingTop                = Widgets.themeContainerBoxPaddingTop;
    public int       boxPaddingBottom             = Widgets.themeContainerBoxPaddingBottom;
    public int       boxPaddingLeft               = Widgets.themeContainerBoxPaddingLeft;
    public int       boxPaddingRight              = Widgets.themeContainerBoxPaddingRight;
    public int       boxChildSpacingVertical      = Widgets.themeContainerBoxChildSpacingVertical;
    public int       boxChildSpacingHorizontal    = Widgets.themeContainerBoxChildSpacingHorizontal;
    public int       boxCornerRadiusTopLeft       = Widgets.themeContainerBoxCornerRadiusTopLeft;
    public int       boxCornerRadiusTopRight      = Widgets.themeContainerBoxCornerRadiusTopRight;
    public int       boxCornerRadiusBottomRight   = Widgets.themeContainerBoxCornerRadiusBottomRight;
    public int       boxCornerRadiusBottomLeft    = Widgets.themeContainerBoxCornerRadiusBottomLeft;
    public int       boxCornerSegmentsTopLeft     = Widgets.themeContainerBoxCornerSegmentsTopLeft;
    public int       boxCornerSegmentsTopRight    = Widgets.themeContainerBoxCornerSegmentsTopRight;
    public int       boxCornerSegmentsBottomRight = Widgets.themeContainerBoxCornerSegmentsBottomRight;
    public int       boxCornerSegmentsBottomLeft  = Widgets.themeContainerBoxCornerSegmentsBottomLeft;
    public int       boxBorderSize                = Widgets.themeContainerBoxBorderSize;
    public Color     boxBorderColor               = Widgets.themeContainerBoxBorderColor;

    /* scrollbar */
    //private WidgetInputScrollbar_old scrollbar = new WidgetInputScrollbar_old();
    private final WidgetInputScrollbar scrollbar = new WidgetInputScrollbar();

    public WidgetContainer() {
        addChild(scrollbar);
        scrollbar.anchor = Anchor.TOP_RIGHT;
        scrollbar.onMouseScroll = e -> true; // disable the default function
    }

    @Override
    protected void onResizeDefault(Event.EventResize e) {
        if (layout == Layout.VERTICAL) {
            scrollbar.length = backgroundHeight;
            scrollbar.anchor = Anchor.TOP_RIGHT;
            scrollbar.anchorY = boxBorderSize;
            scrollbar.anchorX = 0;
            scrollbar.type = WidgetInputScrollbar.Type.VERTICAL;
        } else if (layout == Layout.HORIZONTAL) {
            scrollbar.length = backgroundWidth;
            scrollbar.anchor = Anchor.BOTTOM_LEFT;
            scrollbar.anchorX = boxBorderSize;
            scrollbar.anchorY = 0;
            scrollbar.type = WidgetInputScrollbar.Type.HORIZONTAL;
        }
    }

    // this will make sure the scrollbar is always on top.
    @Override
    protected void onChildAddedDefault(Event.EventChildAdded e) {
        int scrollbarChildIndex = children.indexOf(scrollbar, true);
        children.set(scrollbarChildIndex, e.widget, true);
        children.set(children.size - 1, scrollbar, true);
    }

    /*** default event handlers */
    @Override
    protected void onMouseScrollDefault(Event.EventMouseScroll e) {
        if (!scrollbar.active) return;
        scrollbar.scroll(e.scrollValue * 0.1f);
    }

    /*** global container logic ***/
    // in order to add logic, just override the fixedUpdateContainer() method instead.
    @Override
    protected final void fixedUpdate(float delta) {
        // TODO: consider: when to add scrollbar, and which direction.
        float contentWidth = getContentsWidth(childrenLayout);
        float contentHeight = getContentsHeight(childrenLayout);
        float verticalOverflow = height - contentHeight - boxPaddingTop - boxPaddingBottom;
        float horizontalOverflow = width - contentWidth - boxPaddingLeft - boxPaddingRight;

        scrollbar.active = layoutAddScrollbar;
        if (layout == Layout.VERTICAL && verticalOverflow > 0) scrollbar.active = false;
        else if (layout == Layout.HORIZONTAL && horizontalOverflow > 0) scrollbar.active = false;

        if (!scrollbar.active) {
            scrollOffsetY = 0; // reset scroll value if scrolling is disabled.
            scrollOffsetX = 0; // reset scroll value if scrolling is disabled.
        } else if (layout == Layout.VERTICAL) {
            scrollbar.length = backgroundHeight;
            scrollbar.anchor = Anchor.TOP_RIGHT;
            scrollbar.anchorY = boxBorderSize;
            scrollbar.anchorX = 0;
            scrollbar.type = WidgetInputScrollbar.Type.VERTICAL;

            scrollOffsetX = 0;
            scrollOffsetY = -scrollbar.getValue() * verticalOverflow; // TODO
        } else if (layout == Layout.HORIZONTAL) {
            scrollbar.length = backgroundWidth;
            scrollbar.anchor = Anchor.BOTTOM_LEFT;
            scrollbar.anchorX = boxBorderSize;
            scrollbar.anchorY = 0;
            scrollbar.type = WidgetInputScrollbar.Type.HORIZONTAL;

            scrollOffsetY = 0;
            scrollOffsetX = -scrollbar.getValue() * horizontalOverflow; // TODO
        }

        backgroundWidth = Math.max(0, getWidth() - boxBorderSize * 2);
        backgroundHeight = Math.max(0, getHeight() - boxBorderSize * 2);

        fixedUpdateContainer(delta);
    }

    protected void fixedUpdateContainer(float delta) {}

    /*** children layout ***/

    @Override
    protected void setChildrenOffsets(@NotNull Array<Widget> widgets) {
        if (layout == null) super.setChildrenOffsets(widgets);
        switch (layout) {
            case STACK      -> setChildrenOffsetsStack(widgets);
            case VERTICAL   -> setChildrenOffsetsVertical(widgets);
            case HORIZONTAL -> setChildrenOffsetsHorizontal(widgets);
            case CUSTOM     -> setChildrenOffsetsCustom(widgets);
        }
    }

    // TODO: consider global scale
    protected final void setChildrenOffsetsStack(Array<Widget> widgets) {
        for (Widget child : widgets) {
            child.offsetX = boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;
            child.offsetY = boxPaddingBottom - (boxPaddingBottom + boxPaddingTop) * 0.5f + scrollOffsetY;
        }
    }

    // TODO: consider global scale
    protected final void setChildrenOffsetsHorizontal(Array<Widget> widgets) {
        float sclX = 1; // global transform
        float position_x = -(getWidth() * 0.5f - boxBorderSize - boxPaddingLeft + scrollOffsetX) * sclX;
        for (Widget child : widgets) {
            float child_width = child.getWidth() * sclX;
            child.offsetX = position_x + child_width * 0.5f;
            child.offsetY = boxPaddingBottom - (boxPaddingBottom + boxPaddingTop) * 0.5f;
            position_x += child_width + boxChildSpacingHorizontal * sclX;
        }
    }

    // TODO: consider global scale
    protected final void setChildrenOffsetsVertical(Array<Widget> widgets) {
        float sclY = 1; // global transform
        float position_y = (getHeight() * 0.5f - boxBorderSize - boxPaddingTop) * sclY + scrollOffsetY;
        for (Widget child : widgets) {
            float child_height = child.getHeight() * sclY;
            child.offsetX = boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;
            child.offsetY = position_y - child_height * 0.5f;
            position_y -= child_height + boxChildSpacingVertical * sclY;
        }
    }

    // meant to be overriden by custom layout containers, like a wheel select.
    protected void setChildrenOffsetsCustom(Array<Widget> widgets) {
        super.setChildrenOffsets(widgets);
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

    protected final float getContentWidthStack(final Array<Widget> widgets) {
        float maxWidth = Float.NEGATIVE_INFINITY;
        for (Widget child : widgets) {
            maxWidth = Math.max(child.getWidth(), maxWidth);
        }
        return Math.abs(maxWidth);
    }

    protected final float getContentWidthHorizontal(final Array<Widget> widgets) {
        float width = 0;
        for (Widget child : widgets) {
            width += child.getWidth();
        }
        width += Math.max(0f, boxChildSpacingHorizontal * (widgets.size - 1));
        return width;
    }

    protected float getContentWidthVertical(final Array<Widget> widgets) {
        return getContentWidthStack(widgets);
    }

    protected final float getContentWidthCustom(final Array<Widget> widgets) {
        float min_x = Float.POSITIVE_INFINITY;
        float max_x = Float.NEGATIVE_INFINITY;

        for (Widget widget : widgets) {
            float left = widget.offsetX - widget.getWidth();
            float right = widget.offsetX + widget.getWidth();
            min_x = Math.min(min_x, left);
            max_x = Math.max(max_x, right);
        }

        return Math.abs(max_x - min_x);
    }

    protected float getContentHeightStack(final Array<Widget> widgets) {
        float maxHeight = Float.NEGATIVE_INFINITY;
        for (Widget child : widgets) {
            maxHeight = Math.max(child.getHeight(), maxHeight);
        }
        return maxHeight;
    }

    protected final float getContentHeightHorizontal(final Array<Widget> widgets) {
        return getContentHeightStack(widgets);
    }

    protected final float getContentHeightVertical(final Array<Widget> widgets) {
        float height = 0;
        for (Widget child : widgets) {
            height += child.getHeight();
        }
        height += Math.max(0f, boxChildSpacingVertical * (widgets.size - 1));
        return height;
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

    protected final float getContentHeightCustom(final Array<Widget> widgets) {
        float min_y = Float.POSITIVE_INFINITY;
        float max_y = Float.NEGATIVE_INFINITY;

        for (Widget widget : widgets) {
            float down = widget.offsetY - widget.getHeight();
            float up = widget.offsetY + widget.getHeight();
            min_y = Math.min(min_y, down);
            max_y = Math.max(max_y, up);
        }

        return Math.abs(max_y - min_y);
    }

    protected final float getContentsWidth(Array<Widget> widgets) {
        return switch (layout) {
            case STACK      -> getContentWidthStack(widgets);
            case HORIZONTAL -> getContentWidthHorizontal(widgets);
            case VERTICAL   -> getContentWidthVertical(widgets);
            case CUSTOM     -> getContentWidthCustom(widgets);
        };
    }

    protected final float getContentsHeight(Array<Widget> widgets) {
        return switch (layout) {
            case STACK      -> getContentHeightStack(widgets);
            case HORIZONTAL -> getContentHeightHorizontal(widgets);
            case VERTICAL   -> getContentHeightVertical(widgets);
            case CUSTOM     -> getContentHeightCustom(widgets);
        };
    }

    @Override
    protected float getWidth() {
        float width = switch (layoutWidthSizing) {
            case STATIC   -> layoutWidth;
            case VIEWPORT -> layoutWidth * Graphics.getWindowWidth();
            case DYNAMIC  -> getContentsWidth(childrenLayout) + boxPaddingLeft + boxPaddingRight + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(width, layoutWidthMin, layoutWidthMax);
    }

    @Override
    protected float getHeight() {
        float height = switch (layoutHeightSizing) {
            case STATIC   -> layoutHeight;
            case VIEWPORT -> layoutHeight * Graphics.getWindowHeight();
            case DYNAMIC  -> getContentsHeight(childrenLayout) + boxPaddingTop + boxPaddingBottom + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(height, layoutHeightMin, layoutHeightMax);
    }

    /*** MASKING ***/

    @Override
    public final boolean maskChildren() {
        return layoutOverflowX == Overflow.HIDDEN || layoutOverflowY == Overflow.HIDDEN;
    }

    /*** SUPPORTING ENUMS ***/

    // controls the children's layout
    public enum Layout {
        STACK, // stack on top of each-other at the center
        VERTICAL, // place elements from top to bottom, while taking box model into account (padding, border, ...)
        HORIZONTAL, // place elements from left to right, while taking box model into account (padding, border, ...)
        CUSTOM
        ;
    }

    // controls the box sizing
    public enum Sizing {
        STATIC  ,  // Hard-coded value in pixels. The size remains constant even if content overflows or fits with extra space.
        DYNAMIC , // The widget box will set its size to completely fit its children.
        VIEWPORT, // the node box size will always size itself according to the viewport. For example, if the window width is 100 and the width is 0.82 -> 82 final width in pixels
        ;
    }

    // TODO: remove this scrollbar value
    // controls how it handles overflow children.
    public enum Overflow {
        VISIBLE  ,   // does nothing, renders while ignoring the bounds
        HIDDEN   ,    // uses glScissors to clip the content, so only the pixels that land inside the box render. The rest get trimmed.
        ;
    }

}
