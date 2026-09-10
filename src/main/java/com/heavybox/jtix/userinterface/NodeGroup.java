package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;

public class NodeGroup extends Node {

    /* settings */
    public boolean widthFitContent  = false;
    public float   width            = 400;
    public float   widthMin         = 0;
    public float   widthMax         = Float.POSITIVE_INFINITY;
    public boolean heightFitContent = false;
    public float   height           = 400;
    public float   heightMin        = 0;
    public float   heightMax        = Float.POSITIVE_INFINITY;
    public boolean hideOverflow     = true;
    public boolean useScrollbar     = true;

    /* theme */
    public Texture textureBackground         = UserInterface.getTheme().groupTextureBackground;
    public Color   colorBackground           = UserInterface.getTheme().groupColorBackground.clone();
    public float   paddingTop                = UserInterface.getTheme().groupPaddingTop;
    public float   paddingBottom             = UserInterface.getTheme().groupPaddingBottom;
    public float   paddingLeft               = UserInterface.getTheme().groupPaddingLeft;
    public float   paddingRight              = UserInterface.getTheme().groupPaddingRight;
    public float   childSpacingVertical      = UserInterface.getTheme().groupChildSpacingVertical;
    public float   childSpacingHorizontal    = UserInterface.getTheme().groupChildSpacingHorizontal;
    public float   cornerRadiusTopLeft       = UserInterface.getTheme().groupCornerRadiusTopLeft;
    public float   cornerRadiusTopRight      = UserInterface.getTheme().groupCornerRadiusTopRight;
    public float   cornerRadiusBottomRight   = UserInterface.getTheme().groupCornerRadiusBottomRight;
    public float   cornerRadiusBottomLeft    = UserInterface.getTheme().groupCornerRadiusBottomLeft;
    public int     cornerSegmentsTopLeft     = UserInterface.getTheme().groupCornerSegmentsTopLeft;
    public int     cornerSegmentsTopRight    = UserInterface.getTheme().groupCornerSegmentsTopRight;
    public int     cornerSegmentsBottomRight = UserInterface.getTheme().groupCornerSegmentsBottomRight;
    public int     cornerSegmentsBottomLeft  = UserInterface.getTheme().groupCornerSegmentsBottomLeft;
    public float   sizeBorder                = UserInterface.getTheme().groupSizeBorder;
    public Color   colorBorder               = UserInterface.getTheme().groupColorBorder.clone();

    /* state */
    private final ArrayFloat polygon = new ArrayFloat(true, 8);
    private final ArrayInt   indices = new ArrayInt(true, 6);
    private float scrollOffsetY    = 0;
    private float scrollOffsetX    = 0;
    private float backgroundWidth  = 0;
    private float backgroundHeight = 0;
    private float currentWidth     = 0;
    private float currentHeight    = 0;

    // built in children
    private final NodeScrollbar scrollbarY = new NodeScrollbar(true);
    private final NodeScrollbar scrollbarX = new NodeScrollbar(false);

    // set scrolls etc
    public NodeGroup() {
        scrollbarY.zIndex = Integer.MAX_VALUE;
        scrollbarX.zIndex = Integer.MAX_VALUE;
        scrollbarY.anchor = Anchor.PARENT_TOP_RIGHT;
        scrollbarX.anchor = Anchor.PARENT_BOTTOM_LEFT;
        scrollbarX.active = false;
        scrollbarY.active = false;

        childAdd(scrollbarY);
        childAdd(scrollbarX);

        onMouseScrollDefault(e -> {
            scrollbarY.value -= e.scrollY * 0.05f;
            scrollbarY.value = MathUtils.clampFloat(scrollbarY.value, 0, 1);
        });
    }

    public void setLayoutDefault() {
        this.layout = null;
    }

    public void setLayoutVertical() {
        this.layout = new LayoutLinear();
    }

    public void setLayoutHorizontal() {
        this.layout = new LayoutLinear(false);
    }

    public void setLayoutGrid(final boolean fillRows, final int rows, final int cols, boolean uniformGrid) {
        this.layout = new LayoutGrid();
        LayoutGrid layoutGrid = (LayoutGrid) this.layout;
        layoutGrid.fillRows = fillRows;
        layoutGrid.rows = rows;
        layoutGrid.cols = cols;
        layoutGrid.uniformGrid = uniformGrid;
    }

    @Override
    protected boolean maskChildren() {
        return hideOverflow;
    }

    @Override
    protected final void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        drawBackground(renderer2D, x, y, deg, sclX, sclY);
        drawBorder(renderer2D, x, y, deg, sclX, sclY);
    }

    @Override
    protected final void drawMask(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        drawBackground(renderer2D, x, y, deg, sclX, sclY);
    }

    protected void drawBackground(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(colorBackground);
        renderer2D.drawPolygonFilled(textureBackground, polygon, indices, x, y, deg, sclX, sclY);
    }

    protected void drawBorder(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (sizeBorder <= 0) return;

        renderer2D.setColor(colorBorder);
        renderer2D.drawRectangleBorder(backgroundWidth, backgroundHeight, sizeBorder,
                cornerRadiusTopLeft, cornerSegmentsTopLeft,
                cornerRadiusTopRight, cornerSegmentsTopRight,
                cornerRadiusBottomRight, cornerSegmentsBottomRight,
                cornerRadiusBottomLeft, cornerSegmentsBottomLeft,
                x, y, deg, sclX, sclY);
    }

    @Override
    public final float getWidth() {
        return currentWidth;
    }

    @Override
    public final float getHeight() {
        return currentHeight;
    }

    @Override
    protected final void onFixedUpdate(float delta) {
        currentWidth = widthFitContent ? super.getChildrenSpanWidth() + paddingLeft + paddingRight + sizeBorder + sizeBorder : this.width;
        currentWidth = MathUtils.clampFloat(currentWidth, widthMin, widthMax);
        currentHeight = heightFitContent ? super.getChildrenSpanHeight() + paddingTop + paddingBottom + sizeBorder + sizeBorder : this.height;
        currentHeight = MathUtils.clampFloat(currentHeight, heightMin, heightMax);
        backgroundWidth = Math.max(0, currentWidth - sizeBorder * 2);
        backgroundHeight = Math.max(0, currentHeight - sizeBorder * 2);
        setShapeToRectangleRoundCorners(backgroundWidth, backgroundHeight,
                cornerRadiusTopLeft, cornerSegmentsTopLeft,
                cornerRadiusTopRight, cornerSegmentsTopRight,
                cornerRadiusBottomRight, cornerSegmentsBottomRight,
                cornerRadiusBottomLeft, cornerSegmentsBottomLeft);

        if (layout instanceof LayoutLinear) {
            LayoutLinear layoutLinear = (LayoutLinear) layout;

            if (layoutLinear.vertical) {
                layoutLinear.childSpacing = childSpacingVertical;
                layoutLinear.primaryAxisOffset = (sizeBorder + paddingTop) - scrollOffsetY;
                layoutLinear.secondaryAxisOffset = (paddingLeft - paddingRight) * 0.5f;
                float contentHeight = getChildrenSpanHeight();
                float verticalOverflow = currentHeight - contentHeight - paddingTop - paddingBottom;
                scrollbarY.active = useScrollbar && (contentHeight > currentHeight);
                scrollbarY.thumbSize = contentHeight == 0 ? 1 : currentHeight / contentHeight;
                scrollOffsetY = scrollbarY.active ? -scrollbarY.value * verticalOverflow : 0;
                scrollbarY.length = backgroundHeight;
            }

            if (!layoutLinear.vertical) {
                layoutLinear.childSpacing = childSpacingHorizontal;
                layoutLinear.primaryAxisOffset = (sizeBorder + paddingLeft) - scrollOffsetX;
                layoutLinear.secondaryAxisOffset = (paddingBottom - paddingTop) * 0.5f;
                float contentWidth = getChildrenSpanWidth();
                float horizontalOverflow = currentWidth - contentWidth - paddingLeft - paddingRight;
                scrollbarX.active = useScrollbar && (contentWidth > currentWidth);
                scrollbarX.thumbSize = contentWidth == 0 ? 1 : currentWidth / contentWidth;
                scrollOffsetX = scrollbarX.active ? -scrollbarX.value * horizontalOverflow : 0;
                scrollbarX.length = backgroundWidth;
            }

        }

        if (layout instanceof LayoutGrid) {
            LayoutGrid layoutGrid = (LayoutGrid) layout;

            layoutGrid.rowsSpacing = childSpacingVertical;
            layoutGrid.colsSpacing = childSpacingHorizontal;
            layoutGrid.colsOffset = (sizeBorder + paddingLeft) - scrollOffsetX;
            layoutGrid.rowsOffset = -(sizeBorder + paddingTop) + scrollOffsetY;

            float contentWidth = getChildrenSpanWidth();
            float contentHeight = getChildrenSpanHeight();
            float horizontalOverflow = currentWidth - contentWidth - paddingLeft - paddingRight;
            float verticalOverflow = currentHeight - contentHeight - paddingTop - paddingBottom;

            scrollbarY.active = useScrollbar && (contentHeight > currentHeight);
            scrollbarY.thumbSize = contentHeight == 0 ? 1 : currentHeight / contentHeight;
            scrollOffsetY = scrollbarY.active ? -scrollbarY.value * verticalOverflow : 0;
            scrollbarY.length = backgroundHeight;

            scrollbarX.active = useScrollbar && (contentWidth > currentWidth);
            scrollbarX.thumbSize = contentWidth == 0 ? 1 : currentWidth / contentWidth;
            scrollOffsetX = scrollbarX.active ? -scrollbarX.value * horizontalOverflow : 0;
            scrollbarX.length = backgroundWidth - (scrollbarY.active ? scrollbarY.thickness : 0);
        }

        scrollbarY.transform.x = -sizeBorder;
        scrollbarX.transform.y = sizeBorder;

        onFixedUpdatePanel(delta);
    }

    protected void onFixedUpdatePanel(float delta) { }

    // TODO: find a place for this.
    private void setShapeToRectangleRoundCorners(float width, float height,
                                                float cornerRadiusTopLeft, int refinementTopLeft,
                                                float cornerRadiusTopRight, int refinementTopRight,
                                                float cornerRadiusBottomRight, int refinementBottomRight,
                                                float cornerRadiusBottomLeft, int refinementBottomLeft) {
        if (cornerRadiusTopLeft == 0 && cornerRadiusTopRight == 0 && cornerRadiusBottomRight == 0 && cornerRadiusBottomLeft == 0) {
            polygon.clear();
            float widthHalf = width * 0.5f;
            float heightHalf = height * 0.5f;
            polygon.add(-widthHalf, -heightHalf);
            polygon.add( widthHalf, -heightHalf);
            polygon.add( widthHalf,  heightHalf);
            polygon.add(-widthHalf,  heightHalf);
            // triangulate in place
            indices.clear();
            indices.add(0);
            indices.add(1);
            indices.add(3);
            indices.add(3);
            indices.add(1);
            indices.add(2);
            return;
        }

        /* put vertices */
        polygon.clear();
        refinementTopLeft = Math.max(2, refinementTopLeft);
        refinementTopRight = Math.max(2, refinementTopRight);
        refinementBottomRight = Math.max(2, refinementBottomRight);
        refinementBottomLeft = Math.max(2, refinementBottomLeft);
        float maxRadius = Math.min(Math.abs(width), Math.abs(height)) * 0.5f;
        cornerRadiusTopLeft     = Math.min(Math.abs(cornerRadiusTopLeft),     maxRadius);
        cornerRadiusTopRight    = Math.min(Math.abs(cornerRadiusTopRight),    maxRadius);
        cornerRadiusBottomRight = Math.min(Math.abs(cornerRadiusBottomRight), maxRadius);
        cornerRadiusBottomLeft  = Math.min(Math.abs(cornerRadiusBottomLeft),  maxRadius);
        float widthHalf  = width   * 0.5f;
        float heightHalf = height  * 0.5f;
        float daTL = 90.0f / (refinementTopLeft - 1);
        float daTR = 90.0f / (refinementTopRight - 1);
        float daBR = 90.0f / (refinementBottomRight - 1);
        float daBL = 90.0f / (refinementBottomLeft - 1);

        Vector2 corner = new Vector2();
        int totalRefinement = 0;
        // add upper left corner vertices
        if (MathUtils.isZero(cornerRadiusTopLeft)) {
            corner.set(-widthHalf, heightHalf);
            polygon.add(corner.x);
            polygon.add(corner.y);
            totalRefinement++;
        } else {
            for (int i = 0; i < refinementTopLeft; i++) {
                corner.set(-cornerRadiusTopLeft, 0);
                corner.rotateDeg(-daTL * i); // rotate clockwise
                corner.add(-widthHalf + cornerRadiusTopLeft, heightHalf - cornerRadiusTopLeft);
                polygon.add(corner.x);
                polygon.add(corner.y);
                totalRefinement++;
            }
        }
        // add upper right corner vertices
        if (MathUtils.isZero(cornerRadiusTopRight)) {
            corner.set(widthHalf, heightHalf);
            polygon.add(corner.x);
            polygon.add(corner.y);
            totalRefinement++;
        } else {
            for (int i = 0; i < refinementTopRight; i++) {
                corner.set(0, cornerRadiusTopRight);
                corner.rotateDeg(-daTR * i); // rotate clockwise
                corner.add(widthHalf - cornerRadiusTopRight, heightHalf - cornerRadiusTopRight);
                polygon.add(corner.x);
                polygon.add(corner.y);
                totalRefinement++;
            }
        }
        // add lower right corner vertices
        if (MathUtils.isZero(cornerRadiusBottomRight)) {
            corner.set(widthHalf, -heightHalf);
            polygon.add(corner.x);
            polygon.add(corner.y);
            totalRefinement++;
        } else {
            for (int i = 0; i < refinementBottomRight; i++) {
                corner.set(cornerRadiusBottomRight, 0);
                corner.rotateDeg(-daBR * i); // rotate clockwise
                corner.add(widthHalf - cornerRadiusBottomRight, -heightHalf + cornerRadiusBottomRight);
                polygon.add(corner.x);
                polygon.add(corner.y);
                totalRefinement++;
            }
        }
        // add lower left corner vertices
        if (MathUtils.isZero(cornerRadiusBottomLeft)) {
            corner.set(-widthHalf, -heightHalf);
            polygon.add(corner.x);
            polygon.add(corner.y);
            totalRefinement++;
        } else {
            for (int i = 0; i < refinementBottomLeft; i++) {
                corner.set(0, -cornerRadiusBottomLeft);
                corner.rotateDeg(-daBL * i); // rotate clockwise
                corner.add(-widthHalf + cornerRadiusBottomLeft, -heightHalf + cornerRadiusBottomLeft);
                polygon.add(corner.x);
                polygon.add(corner.y);
                totalRefinement++;
            }
        }

        /* in-place triangulation */
        indices.clear();
        for (int i = 0; i < totalRefinement - 2; i++) {
            indices.add(0);
            indices.add(i + 2);
            indices.add(i + 1);
        }
    }

    @Override
    protected void setHitZone(@NotNull HitZone hitZone) {
        float width = getWidth();
        float height = getHeight();
        hitZone.setToRectangle(
                width, height,
                cornerRadiusTopLeft, cornerSegmentsTopLeft,
                cornerRadiusTopRight, cornerSegmentsTopRight,
                cornerRadiusBottomRight, cornerSegmentsBottomRight,
                cornerRadiusBottomLeft, cornerSegmentsBottomLeft
        );
    }

}
