package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.widgets.Node;
import com.heavybox.jtix.widgets.NodeContainer;
import org.jetbrains.annotations.NotNull;

public class WidgetContainer extends Widget {

    /* state */ // TODO
    private float scrollOffsetX    = 0;
    private float scrollOffsetY    = 0;
    private float calculatedWidth  = 0;
    private float calculatedHeight = 0;
    private float backgroundWidth  = 0;
    private float backgroundHeight = 0;

    /* box container visual properties (sizing, overflow, padding etc.) */
    public Layout    boxLayout                    = Layout.STACK;
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
    public Color     boxBackgroudColor            = Color.valueOf("#227BFF");
    public boolean   boxBackgroundEnabled         = true;
    public int       boxPaddingTop                = 80;
    public int       boxPaddingBottom             = 20;
    public int       boxPaddingLeft               = 0;
    public int       boxPaddingRight              = 0;
    public int       boxChildSpacingVertical      = 5;
    public int       boxChildSpacingHorizontal    = 5;
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

    @Override
    protected void setActiveChildrenOffsets(@NotNull Array<Widget> activeChildren) {
        if (boxLayout == null) super.setActiveChildrenOffsets(activeChildren);
        switch (boxLayout) {
            case STACK      -> setActiveChildrenOffsetsStack(activeChildren);
            case VERTICAL   -> setActiveChildrenOffsetsVertical(activeChildren);
            case HORIZONTAL -> setActiveChildrenOffsetsHorizontal(activeChildren);
            case CUSTOM     -> setActiveChildrenOffsetsCustom(activeChildren);
        }
    }

    protected final void setActiveChildrenOffsetsStack(Array<Widget> activeChildren) {
        for (Widget child : activeChildren) {
            child.offsetX = boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;
            child.offsetY = boxPaddingBottom - (boxPaddingBottom + boxPaddingTop) * 0.5f;
        }
    }

    protected final void setActiveChildrenOffsetsHorizontal(Array<Widget> activeChildren) {
        float sclX = 1; // global transform
        float position_x = -(getWidth() * 0.5f - boxBorderSize - boxPaddingLeft) * sclX;
        for (Widget child : activeChildren) {
            float child_width = child.getWidth() * sclX;
            child.offsetX = position_x + child_width * 0.5f;
            child.offsetY = boxPaddingBottom - (boxPaddingBottom + boxPaddingTop) * 0.5f;
            position_x += child_width + boxChildSpacingHorizontal * sclX;
        }
    }

    protected final void setActiveChildrenOffsetsVertical(Array<Widget> activeChildren) {
        float sclY = 1; // global transform
        float position_y = (getHeight() * 0.5f - boxBorderSize - boxPaddingTop) * sclY;
        for (Widget child : activeChildren) {
            float child_height = child.getHeight() * sclY;
            child.offsetX = boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;
            child.offsetY = position_y - child_height * 0.5f;
            position_y -= child_height + boxChildSpacingVertical * sclY;
        }
    }

    // meant to be overriden by custom layout containers, like a wheel select.
    protected void setActiveChildrenOffsetsCustom(Array<Widget> activeChildren) {
        super.setActiveChildrenOffsets(activeChildren);
    }

    // TODO: cache results of backgroundWidth and backgroundHeight
    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        float backgroundWidth = Math.max(0, getWidth() - boxBorderSize * 2); // TODO: not here
        float backgroundHeight = Math.max(0, getHeight() - boxBorderSize * 2); // TODO: not here.

        if (boxBackgroundEnabled) {
            renderer2D.setColor(boxBackgroudColor);
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
        float maskWidth  = boxContentOverflowX == Overflow.VISIBLE ? fullScreenMask : backgroundWidth;
        float maskHeight = boxContentOverflowY == Overflow.VISIBLE ? fullScreenMask : backgroundHeight;
        renderer2D.drawRectangleFilled(maskWidth, maskHeight,
                boxCornerRadiusTopLeft, boxCornerSegmentsTopLeft,
                boxCornerRadiusTopRight, boxCornerSegmentsTopRight,
                boxCornerRadiusBottomRight, boxCornerSegmentsBottomRight,
                boxCornerRadiusBottomLeft, boxCornerSegmentsBottomLeft,
                x, y, deg, sclX, sclY);
    }

    protected final float getContentWidthStack(final Array<Widget> activeChildren) {
        float maxWidth = Float.NEGATIVE_INFINITY;
        for (Widget child : activeChildren) {
            maxWidth = Math.max(child.getWidth(), maxWidth);
        }
        return Math.abs(maxWidth);
    }

    protected final float getContentWidthHorizontal(final Array<Widget> activeChildren) {
        float width = 0;
        for (Widget child : activeChildren) {
            width += child.getWidth();
        }
        width += Math.max(0f, boxChildSpacingHorizontal * (children.size - 1));
        return width;
    }

    protected float getContentWidthVertical(final Array<Widget> activeChildren) {
        return getContentWidthStack(activeChildren);
    }

    protected final float getContentWidthCustom(final Array<Widget> activeChildren) {
        float min_x = Float.POSITIVE_INFINITY;
        float max_x = Float.NEGATIVE_INFINITY;

        for (Widget widget : activeChildren) {
            float left = widget.offsetX - widget.getWidth();
            float right = widget.offsetX + widget.getWidth();
            min_x = Math.min(min_x, left);
            max_x = Math.max(max_x, right);
        }

        return Math.abs(max_x - min_x);
    }

    protected float getContentHeightStack(final Array<Widget> activeChildren) {
        float maxHeight = Float.NEGATIVE_INFINITY;
        for (Widget child : children) {
            maxHeight = Math.max(child.getHeight(), maxHeight);
        }
        return maxHeight;
    }

    protected final float getContentHeightHorizontal(final Array<Widget> activeChildren) {
        return getContentHeightStack(activeChildren);
    }

    protected final float getContentHeightVertical(final Array<Widget> activeChildren) {
        float height = 0;
        for (Widget child : activeChildren) {
            height += child.getHeight();
        }
        height += Math.max(0f, boxChildSpacingVertical * (children.size - 1));
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
        if (boxContentOverflowX != Overflow.VISIBLE && boxContentOverflowY != Overflow.VISIBLE) {
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
        if (boxContentOverflowX == Overflow.VISIBLE && boxContentOverflowY == Overflow.VISIBLE) {
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
        if (boxContentOverflowX == Overflow.VISIBLE) {
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
        if (boxContentOverflowY == Overflow.VISIBLE) {
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

    protected final float getContentHeightCustom(final Array<Widget> activeChildren) {
        float min_y = Float.POSITIVE_INFINITY;
        float max_y = Float.NEGATIVE_INFINITY;

        for (Widget widget : activeChildren) {
            float down = widget.offsetY - widget.getHeight();
            float up = widget.offsetY + widget.getHeight();
            min_y = Math.min(min_y, down);
            max_y = Math.max(max_y, up);
        }

        return Math.abs(max_y - min_y);
    }

    protected final float getContentWidth(Array<Widget> activeChildren) {
        return switch (boxLayout) {
            case STACK      -> getContentWidthStack(activeChildren);
            case HORIZONTAL -> getContentWidthHorizontal(activeChildren);
            case VERTICAL   -> getContentWidthVertical(activeChildren);
            case CUSTOM     -> getContentWidthCustom(activeChildren);
        };
    }

    protected final float getContentHeight(Array<Widget> activeChildren) {
        return switch (boxLayout) {
            case STACK      -> getContentHeightStack(activeChildren);
            case HORIZONTAL -> getContentHeightHorizontal(activeChildren);
            case VERTICAL   -> getContentHeightVertical(activeChildren);
            case CUSTOM     -> getContentHeightCustom(activeChildren);
        };
    }

    @Override
    protected float getWidth() {
        float width = switch (boxWidthSizing) {
            case STATIC   -> boxWidth;
            case VIEWPORT -> boxWidth * Graphics.getWindowWidth();
            case DYNAMIC  -> getContentWidth(children) + boxPaddingLeft + boxPaddingRight + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(width, boxWidthMin, boxWidthMax);
    }

    @Override
    protected float getHeight() {
        float height = switch (boxHeightSizing) {
            case STATIC   -> boxHeight;
            case VIEWPORT -> boxHeight * Graphics.getWindowHeight();
            case DYNAMIC  -> getContentHeight(children) + boxPaddingTop + boxPaddingBottom + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(height, boxHeightMin, boxHeightMax);
    }

    /*** masking ***/
    @Override
    public boolean maskChildren() {
        return boxContentOverflowX != Overflow.VISIBLE || boxContentOverflowY != Overflow.VISIBLE;
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

    // controls how it handles overflow children.
    public enum Overflow {
        VISIBLE  ,   // does nothing, renders while ignoring the bounds
        HIDDEN   ,    // uses glScissors to clip the content, so only the pixels that land inside the box render. The rest get trimmed.
        SCROLLBAR, // trims the content and adds scrollbars
        ;
    }

}
