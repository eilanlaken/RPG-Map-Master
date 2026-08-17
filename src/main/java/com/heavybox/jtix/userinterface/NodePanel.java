package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.widgets.Widgets;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class NodePanel extends Node {

    /* settings */
    public Sizing widthSizing  = Sizing.STATIC;
    public float  width        = 400;
    public float  widthMin     = 0;
    public float  widthMax     = Float.POSITIVE_INFINITY;
    public Sizing heightSizing = Sizing.STATIC;
    public float  height       = 100;
    public float  heightMin    = 0;
    public float  heightMax    = Float.POSITIVE_INFINITY;

    public Overflow overflow = Overflow.HIDDEN;

    /* theme */
    public Texture textureBackground         = null;
    public Color   colorBackground           = UserInterface.getTheme().panelColorBackground.clone();
    public float   paddingTop                = UserInterface.getTheme().panelPaddingTop;
    public float   paddingBottom             = UserInterface.getTheme().panelPaddingBottom;
    public float   paddingLeft               = UserInterface.getTheme().panelPaddingLeft;
    public float   paddingRight              = UserInterface.getTheme().panelPaddingRight;
    public float   childSpacingVertical      = UserInterface.getTheme().panelChildSpacingVertical;
    public float   childSpacingHorizontal    = UserInterface.getTheme().panelChildSpacingHorizontal;
    public float   cornerRadiusTopLeft       = UserInterface.getTheme().panelCornerRadiusTopLeft;
    public float   cornerRadiusTopRight      = UserInterface.getTheme().panelCornerRadiusTopRight;
    public float   cornerRadiusBottomRight   = UserInterface.getTheme().panelCornerRadiusBottomRight;
    public float   cornerRadiusBottomLeft    = UserInterface.getTheme().panelCornerRadiusBottomLeft;
    public int     cornerSegmentsTopLeft     = UserInterface.getTheme().panelCornerSegmentsTopLeft;
    public int     cornerSegmentsTopRight    = UserInterface.getTheme().panelCornerSegmentsTopRight;
    public int     cornerSegmentsBottomRight = UserInterface.getTheme().panelCornerSegmentsBottomRight;
    public int     cornerSegmentsBottomLeft  = UserInterface.getTheme().panelCornerSegmentsBottomLeft;
    public float   sizeBorder                = UserInterface.getTheme().panelSizeBorder;
    public Color   colorBorder               = UserInterface.getTheme().panelColorBorder.clone();

    /* state */
    private final ArrayFloat polygon = new ArrayFloat(true, 8);
    private final ArrayInt   indices = new ArrayInt(true, 6);
    private float scrollOffsetY    = 0;
    private float currentWidth     = 0;
    private float currentHeight    = 0;
    private float backgroundWidth  = 0;
    private float backgroundHeight = 0;

    // placeholders
    // TODO: remove. load properly as theme.
    Texture texture = new Texture("assets/user-interface-theme/panel-background.png");

    // set scrolls etc
    public NodePanel() {
    }

    @Override
    protected boolean maskChildren() {
        return overflow == null || overflow == Overflow.HIDDEN;
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.drawPolygonFilled(texture.region, polygon, indices, x, y, deg, sclX, sclY);
    }

    @Override
    protected float getWidth() {
        float width = switch (widthSizing) {
            case STATIC   -> this.width;
            case DYNAMIC  -> super.getChildrenSpanWidth() + paddingLeft + paddingRight + sizeBorder + sizeBorder;
        };
        return MathUtils.clampFloat(width, widthMin, widthMax);
    }

    @Override
    protected float getHeight() {
        float height = switch (heightSizing) {
            case STATIC   -> this.height;
            case DYNAMIC  -> super.getChildrenSpanHeight() + paddingTop + paddingBottom + sizeBorder + sizeBorder;
        };
        return MathUtils.clampFloat(height, heightMin, heightMax);
    }

    @Override
    protected final void onFixedUpdate(float delta) {
        currentWidth = getWidth();
        currentHeight = getHeight();

        backgroundWidth = Math.max(0, currentWidth - sizeBorder * 2);
        backgroundHeight = Math.max(0, currentHeight - sizeBorder * 2);
        setShapeToRectangleRoundCorners(backgroundWidth, backgroundHeight,
                cornerRadiusTopLeft, cornerSegmentsTopLeft,
                cornerRadiusTopRight, cornerSegmentsTopRight,
                cornerRadiusBottomRight, cornerSegmentsBottomRight,
                cornerRadiusBottomLeft, cornerSegmentsBottomLeft);
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

    public enum Sizing {
        STATIC, // constant width
        DYNAMIC, // resize to fit children
        ;
    }

    public enum Overflow {
        HIDDEN,
        VISIBLE,
        ;
    }

}
