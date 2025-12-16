package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import org.jetbrains.annotations.NotNull;

// TODO
public class WidgetNodeContainerGrid extends WidgetNode implements WidgetNodeContainer {

    /* state and grid attributes */
    private float gridCellWidth = 0;
    private float gridCellHeight = 0;
    private float backgroundWidth  = 0;
    private float backgroundHeight = 0;

    /* box container layout */
    public Layout    layout                       = Layout.FILL_COLUMNS;
    public int       layoutColumnCapacity         = 1;
    public int       layoutRowCapacity            = 1;

    public Sizing    layoutWidthSizing            = Sizing.DYNAMIC;
    public float     layoutWidth                  = 1;
    public float     layoutWidthMin               = 0;
    public float     layoutWidthMax               = Float.POSITIVE_INFINITY;
    public Sizing    layoutHeightSizing           = Sizing.DYNAMIC;
    public float     layoutHeight                 = 1;
    public float     layoutHeightMin              = 0;
    public float     layoutHeightMax              = Float.POSITIVE_INFINITY;
    public Overflow  layoutOverflowX              = Overflow.HIDDEN;
    public Overflow  layoutOverflowY              = Overflow.HIDDEN;

    /* box container style */
    public boolean   boxBackgroundVisible         = Widgets.themeContainerBoxBackgroundVisible;
    public Color     boxBackgroundColor           = Widgets.themeContainerBoxBackgroundColor.clone();
    public int       boxPaddingTop                = Widgets.themeContainerBoxPaddingTop;
    public int       boxPaddingBottom             = Widgets.themeContainerBoxPaddingBottom;
    public int       boxPaddingLeft               = Widgets.themeContainerBoxPaddingLeft;
    public int       boxPaddingRight              = Widgets.themeContainerBoxPaddingRight;
    public int       boxChildSpacingVertical      = Widgets.themeContainerGridBoxChildSpacingVertical;
    public int       boxChildSpacingHorizontal    = Widgets.themeContainerGridBoxChildSpacingHorizontal;
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

    @Override
    public final boolean maskChildren() {
        return layoutOverflowX == Overflow.HIDDEN || layoutOverflowY == Overflow.HIDDEN;
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
    protected void setChildrenOffsets(Array<WidgetNode> children) {
        if (children.isEmpty()) return;

        // TODO: fix this
        if (layout == Layout.FILL_ROWS) {
            int rows = (int) Math.ceil((float) children.size / layoutRowCapacity);
            if (rows == 1) { // for the first row, it behaves like a horizontal container
                //float position_x = -gridCellWidth * children.size * 0.5f + boxBorderSize + boxPaddingLeft - boxPaddingRight - boxChildSpacingHorizontal * (children.size - 1) * 0.5f;
                float position_x = -(getWidth() * 0.5f - boxBorderSize - boxPaddingLeft);
                for (WidgetNode child : children) {
                    child.offsetX = position_x + gridCellWidth * 0.5f;
                    child.offsetY = boxPaddingBottom - (boxPaddingBottom + boxPaddingTop) * 0.5f;
                    position_x += gridCellWidth + boxChildSpacingHorizontal;
                }
            } else {
                float position_x = -(getWidth() * 0.5f - boxBorderSize - boxPaddingLeft);
                float position_y = getHeight() * 0.5f - boxBorderSize - boxPaddingTop;
                for (int i = 0; i < children.size; i++) {
                    WidgetNode child = children.get(i);
                    child.offsetX = position_x;
                    child.offsetY = position_y - gridCellHeight * 0.5f;
                    position_x += gridCellWidth + boxChildSpacingHorizontal;
                    if ((i + 1) % layoutRowCapacity == 0) {
                        position_x = -(getWidth() * 0.5f - boxBorderSize - boxPaddingLeft);
                        position_y -= gridCellHeight + boxChildSpacingVertical;
                    }
                }
            }
            return;
        }

        if (layout == Layout.FILL_COLUMNS) {

            return;
        }
    }

    @Override
    protected final void fixedUpdate(float delta) {
        gridCellWidth = getGridCellWidth();
        gridCellHeight = getGridCellHeight();
        backgroundWidth = Math.max(0, getWidth() - boxBorderSize * 2);
        backgroundHeight = Math.max(0, getHeight() - boxBorderSize * 2);
        fixedUpdateContainer(delta);
        fixedUpdateContainer(delta);
    }

    private float getGridCellWidth() {
        float max = 0;
        for (WidgetNode child : childrenLayout) {
            max = Math.max(max, child.getWidth());
        }
        return max;
    }

    private float getGridCellHeight() {
        float max = 0;
        for (WidgetNode child : childrenLayout) {
            max = Math.max(max, child.getHeight());
        }
        return max;
    }

    @Override
    public float getContentHeight(Array<WidgetNode> widgets) {
        if (widgets == null || widgets.isEmpty()) return 0;

        if (layout == Layout.FILL_ROWS) {
            int rows = (int) Math.ceil((float) widgets.size / layoutRowCapacity);
            return gridCellHeight * rows + boxChildSpacingVertical * (rows - 1);
        }

        // else: layout == FILL_COLUMNS

        return 0;
    }

    @Override
    public float getContentWidth(Array<WidgetNode> widgets) {
        if (widgets == null || widgets.isEmpty()) return 0;

        if (layout == Layout.FILL_ROWS) {
            if (widgets.size <= layoutRowCapacity) { // sort of a horizontal container
                return gridCellWidth * widgets.size + boxChildSpacingHorizontal * (widgets.size - 1);
            }

            else return gridCellWidth * layoutRowCapacity + boxChildSpacingHorizontal * (layoutRowCapacity - 1);
        }

        // else: layout == FILL_COLUMNS


        return 0;
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

    public enum Layout {
        FILL_COLUMNS,
        FILL_ROWS,
        ;
    }

}
