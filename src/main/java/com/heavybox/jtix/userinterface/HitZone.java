package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Transform2D;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;

public final class HitZone {

    /* The envelope points of the region */
    private final ArrayFloat points = new ArrayFloat(true, 8);

    boolean isValid() { return points.size >= 6 && points.size % 2 == 0; }

    public void setToRectangle(float width, float height) {
        points.clear();
        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;
        points.add(-widthHalf, -heightHalf);
        points.add( widthHalf, -heightHalf);
        points.add( widthHalf,  heightHalf);
        points.add(-widthHalf,  heightHalf);
    }

    public void setToRectangle(float width, float height, float cornerRadius, int refinement) {
        if (cornerRadius == 0) {
            setToRectangle(width, height);
            return;
        }
        points.clear();
        refinement = Math.max(2, refinement);
        float widthHalf  = width  * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / (refinement - 1);

        Vector2 corner = new Vector2();
        // add upper left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(-cornerRadius, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadius, heightHalf - cornerRadius);
            points.add(corner.x, corner.y);
        }

        // add upper right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, cornerRadius);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadius, heightHalf - cornerRadius);
            points.add(corner.x, corner.y);
        }

        // add lower right corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(cornerRadius, 0);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(widthHalf - cornerRadius, -heightHalf + cornerRadius);
            points.add(corner.x, corner.y);
        }

        // add lower left corner vertices
        for (int i = 0; i < refinement; i++) {
            corner.set(0, -cornerRadius);
            corner.rotateDeg(-da * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadius, -heightHalf + cornerRadius);
            points.add(corner.x, corner.y);
        }
    }

    public void setToRectangle(float width, float height,
                               float cornerRadiusTopLeft, int refinementTopLeft,
                               float cornerRadiusTopRight, int refinementTopRight,
                               float cornerRadiusBottomRight, int refinementBottomRight,
                               float cornerRadiusBottomLeft, int refinementBottomLeft) {
        if (MathUtils.isZero(cornerRadiusTopLeft) && MathUtils.isZero(cornerRadiusTopRight)
                && MathUtils.isZero(cornerRadiusBottomLeft) && MathUtils.isZero(cornerRadiusBottomRight)) {
            setToRectangle(width, height);
            return;
        }
        points.clear();
        refinementTopLeft = Math.max(2, refinementTopLeft);
        refinementTopRight = Math.max(2, refinementTopRight);
        refinementBottomRight = Math.max(2, refinementBottomRight);
        refinementBottomLeft = Math.max(2, refinementBottomLeft);
        float widthHalf  = width  * 0.5f;
        float heightHalf = height * 0.5f;
        float daTL = 90.0f / (refinementTopLeft - 1);
        float daTR = 90.0f / (refinementTopRight - 1);
        float daBR = 90.0f / (refinementBottomRight - 1);
        float daBL = 90.0f / (refinementBottomLeft - 1);

        Vector2 corner = new Vector2();
        // add upper left corner vertices
        for (int i = 0; i < refinementTopLeft; i++) {
            corner.set(-cornerRadiusTopLeft, 0);
            corner.rotateDeg(-daTL * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadiusTopLeft,heightHalf - cornerRadiusTopLeft);
            points.add(corner.x, corner.y);
        }

        // add upper right corner vertices
        for (int i = 0; i < refinementTopRight; i++) {
            corner.set(0, cornerRadiusTopRight);
            corner.rotateDeg(-daTR * i); // rotate clockwise
            corner.add(widthHalf - cornerRadiusTopRight, heightHalf - cornerRadiusTopRight);
            points.add(corner.x, corner.y);
        }

        // add lower right corner vertices
        for (int i = 0; i < refinementBottomRight; i++) {
            corner.set(cornerRadiusBottomRight, 0);
            corner.rotateDeg(-daBR * i); // rotate clockwise
            corner.add(widthHalf - cornerRadiusBottomRight, -heightHalf + cornerRadiusBottomRight);
            points.add(corner.x, corner.y);
        }

        // add lower left corner vertices
        for (int i = 0; i < refinementBottomLeft; i++) {
            corner.set(0, -cornerRadiusBottomLeft);
            corner.rotateDeg(-daBL * i); // rotate clockwise
            corner.add(-widthHalf + cornerRadiusBottomLeft, -heightHalf + cornerRadiusBottomLeft);
            points.add(corner.x, corner.y);
        }
    }

    public void setToCircle(float r, int refinement) {
        points.clear();
        refinement = Math.max(refinement, 3);
        float da = 360f / refinement;
        for (int i = 0; i < refinement; i++) {
            points.add(r * MathUtils.cosDeg(da * i));
            points.add(r * MathUtils.sinDeg(da * i));
        }
    }

    public void setToPolygon(final float[] polygonPoints) {
        points.clear();
        points.addAll(polygonPoints);
    }

    // test if point (x,y) is contained inside the transformed polygon
    // by transforming the point by the inverse transform and then testing
    // the new point against the canonical shape.
    boolean containsPoint(final float x, final float y, @NotNull Transform2D transform2D) {
        // translate to local origin
        float dx = x - transform2D.x;
        float dy = y - transform2D.y;
        // inverse rotation
        float rad = -MathUtils.degreesToRadians * transform2D.deg;
        float cos = MathUtils.cosRad(rad);
        float sin = MathUtils.sinRad(rad);
        float x_inv = dx * cos - dy * sin;
        float y_inv = dx * sin + dy * cos;
        // inverse scale
        x_inv /= transform2D.sclX;
        y_inv /= transform2D.sclY;
        return MathUtils.polygonContainsPoint(points, x_inv, y_inv);
    }

    void render(@NotNull Renderer2D renderer2D, @NotNull Transform2D transform2D) {
        renderer2D.setColor(Color.GREEN);
        renderer2D.drawPolygonThin(points, false, transform2D.x,transform2D.y,transform2D.deg,transform2D.sclX,transform2D.sclY);
    }

}
