package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public class Polygon {

    final ArrayFloat points = new ArrayFloat(true, 8);

    void applyTransform(float x, float y, float deg, float sclX, float sclY) {
        Vector2 point = new Vector2();
        for (int i = 0; i < points.size - 1; i += 2) {
            float point_x = points.get(i);
            float point_y = points.get(i + 1);
            point.x = point_x;
            point.y = point_y;
            point.scl(sclX, sclY).rotateDeg(deg).add(x, y);
            points.set(i, point.x);
            points.set(i + 1, point.y);
        }
    }

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

    // TODO: setToCircle (with angle)
    // TODO: setToCircleBorder
    // TODO: setToCircleBorder (with angle)
    // TODO: setToPolygon

    public final boolean containsPoint(float x, float y) {
        return MathUtils.polygonContainsPoint(points, x, y);
    }

}
