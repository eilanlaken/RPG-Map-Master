package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import org.jetbrains.annotations.NotNull;

// TODO: maybe refactor into 3 simpler classes:
// TODO: NodeContainer
// TODO: NodeContainerVertical
// TODO: NodeContainerHorizontal
public class WidgetNodeContainerStack extends WidgetNode implements WidgetNodeContainer {

    /* state */
    private float backgroundWidth  = 0;
    private float backgroundHeight = 0;

    /* box container layout */
    public Sizing widthSizing                  = Sizing.DYNAMIC;
    public float     widthValue = 1;
    public float     widthValueMin = 0;
    public float     widthValueMax = Float.POSITIVE_INFINITY;
    public Sizing heightSizing = Sizing.DYNAMIC;
    public float     heightValue = 1;
    public float     heightValueMin = 0;
    public float     heightValueMax = Float.POSITIVE_INFINITY;
    public Overflow overflowX = Overflow.HIDDEN;
    public Overflow overflowY = Overflow.HIDDEN;

    /* box container style */
    public boolean   boxBackgroundVisible         = Widgets.themeContainerBoxBackgroundVisible;
    public Color     boxBackgroundColor           = Widgets.themeContainerBoxBackgroundColor;
    public int       boxPaddingTop                = Widgets.themeContainerBoxPaddingTop;
    public int       boxPaddingBottom             = Widgets.themeContainerBoxPaddingBottom;
    public int       boxPaddingLeft               = Widgets.themeContainerBoxPaddingLeft;
    public int       boxPaddingRight              = Widgets.themeContainerBoxPaddingRight;
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

    /*** global container logic ***/
    // in order to add logic, just override the fixedUpdateContainer() method instead.
    @Override
    protected final void fixedUpdate(float delta) {
        backgroundWidth = Math.max(0, getWidth() - boxBorderSize * 2);
        backgroundHeight = Math.max(0, getHeight() - boxBorderSize * 2);
        fixedUpdateContainer(delta);
    }

    /*** children layout ***/

    @Override
    protected void setChildrenOffsets(@NotNull Array<WidgetNode> widgets) {
        for (WidgetNode child : widgets) {
            child.offsetX = boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;
            child.offsetY = boxPaddingBottom - (boxPaddingBottom + boxPaddingTop) * 0.5f;
        }
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

    @Override
    protected void drawMask(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        float backgroundWidth = Math.max(0, getWidth() - boxBorderSize * 2); // TODO: not here
        float backgroundHeight = Math.max(0, getHeight() - boxBorderSize * 2); // TODO: not here.

        final float windowMaxExtent = Math.max(Graphics.getWindowWidth(), Graphics.getWindowHeight());
        final float fullScreenMask = 2 * windowMaxExtent;
        float maskWidth  = overflowX == WidgetNodeContainer.Overflow.VISIBLE ? fullScreenMask : backgroundWidth;
        float maskHeight = overflowY == WidgetNodeContainer.Overflow.VISIBLE ? fullScreenMask : backgroundHeight;
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
        if (overflowX != WidgetNodeContainer.Overflow.VISIBLE && overflowY != WidgetNodeContainer.Overflow.VISIBLE) {
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
        if (overflowX == WidgetNodeContainer.Overflow.VISIBLE && overflowY == WidgetNodeContainer.Overflow.VISIBLE) {
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
        if (overflowX == WidgetNodeContainer.Overflow.VISIBLE) {
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
        if (overflowY == WidgetNodeContainer.Overflow.VISIBLE) {
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
        return maxWidth;
    }

    @Override
    public final float getContentHeight(Array<WidgetNode> widgets) {
        float maxHeight = 0;
        for (WidgetNode child : widgets) {
            maxHeight = Math.max(child.getHeight(), maxHeight);
        }
        return maxHeight;
    }

    @Override
    protected float getWidth() {
        float width = switch (widthSizing) {
            case STATIC   -> widthValue;
            case VIEWPORT -> widthValue * Graphics.getWindowWidth();
            case DYNAMIC  -> getContentWidth(childrenLayout) + boxPaddingLeft + boxPaddingRight + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(width, widthValueMin, widthValueMax);
    }

    @Override
    protected float getHeight() {
        float height = switch (heightSizing) {
            case STATIC   -> heightValue;
            case VIEWPORT -> heightValue * Graphics.getWindowHeight();
            case DYNAMIC  -> getContentHeight(childrenLayout) + boxPaddingTop + boxPaddingBottom + boxBorderSize + boxBorderSize;
        };
        return MathUtils.clampFloat(height, heightValueMin, heightValueMax);
    }

    /*** MASKING ***/
    @Override
    public final boolean maskChildren() {
        return overflowX == WidgetNodeContainer.Overflow.HIDDEN || overflowY == WidgetNodeContainer.Overflow.HIDDEN;
    }

}
