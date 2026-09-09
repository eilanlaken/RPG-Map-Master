package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: add curves and functions
// TODO: add borders and thin lines
public class NodeGraphics extends Node {

    public Shader        shader = null;
    public float         color  = Color.WHITE_FLOAT;
    public TextureRegion image  = null;

    private final ArrayFloat polygon = new ArrayFloat(true, 8);
    private final ArrayInt   indices = new ArrayInt(true, 6);

    private float width;
    private float height;

    /* rectangle shape constructor */
    public NodeGraphics(float width, float height) {
        setShapeToRectangle(width, height);
    }

    /* circle shape constructor */
    public NodeGraphics(float r) {
        setShapeToCircle(r, 20);
    }

    public NodeGraphics(@NotNull TextureRegion region) {
        this.image = region;
        setShapeToRectangle(this.image.originalWidth, this.image.originalHeight);
    }

    public NodeGraphics(@NotNull Texture img) {
        this.image = img.region;
        setShapeToRectangle(img.width, img.height);
    }

    public void setShapeToLine(float length, float thickness) {
        setShapeToRectangle(length, thickness);
    }

    public void setShapeToRectangle(float width, float height) {
        // set to rect
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
        // set width and height
        this.width = width;
        this.height = height;
    }

    public void setShapeToRectangleRoundCorners(float width, float height, float cornerRadius, int refinement) {
        setShapeToRectangleRoundCorners(width, height,
                cornerRadius, refinement,
                cornerRadius, refinement,
                cornerRadius, refinement,
                cornerRadius, refinement);
    }

    public void setShapeToRectangleRoundCorners(float width, float height,
                                                float cornerRadiusTopLeft, int refinementTopLeft,
                                                float cornerRadiusTopRight, int refinementTopRight,
                                                float cornerRadiusBottomRight, int refinementBottomRight,
                                                float cornerRadiusBottomLeft, int refinementBottomLeft) {
        if (cornerRadiusTopLeft == 0 && cornerRadiusTopRight == 0 && cornerRadiusBottomRight == 0 && cornerRadiusBottomLeft == 0) {
            setShapeToRectangle(width, height);
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

        this.width = width;
        this.height = height;
    }

    public void setShapeToCircle(float r, int refinement) {
        // set to full circle
        polygon.clear();
        refinement = Math.max(refinement, 3);
        float da = 360f / refinement;
        for (int i = 0; i < refinement; i++) {
            polygon.add(r * MathUtils.cosDeg(da * i));
            polygon.add(r * MathUtils.sinDeg(da * i));
        }
        // triangulate in place
        indices.clear();
        for (int i = 0; i < refinement - 2; i++) {
            indices.add(0);
            indices.add(i + 1);
            indices.add(i + 2);
        }
        // set width and height to the bounding box (w,h)
        width = 2 * Math.abs(r);
        height = 2 * Math.abs(r);
    }

    public void setShapeToCircleArc(float r, int refinement, float angleDeg) {
        // set to circle arc
        polygon.clear();
        refinement = Math.max(refinement, 3);
        float da = angleDeg / refinement;
        polygon.add(0,0);
        for (int i = 0; i < refinement; i++) {
            polygon.add(r * MathUtils.cosDeg(da * i));
            polygon.add(r * MathUtils.sinDeg(da * i));
        }
        // triangulate in place
        indices.clear();
        for (int i = 0; i < refinement - 2; i++) {
            indices.add(0);
            indices.add(i + 1);
            indices.add(i + 2);
        }
        // set width and height to the bounding box (w,h)
        width = 2 * Math.abs(r);
        height = 2 * Math.abs(r);
    }

    public void setShapeToPolygon(final float[] points) {
        if (points.length < 6) throw new UserInterfaceException("Points is a flat array of vertices: [x0,y0,  x1,y1,  x2,y2, ...]. A polygon" + " must contain at least 3 points, and therefore 6 values. Got: " + points.length);
        if (points.length % 2 != 0) throw new UserInterfaceException("Points is a flat array of vertices: [x0,y0,  x1,y1,  x2,y2, ...]. A polygon" + " must contain an even number of values. Got: " + points.length);

        polygon.clear();
        indices.clear();
        polygon.addAll(points);
        polygon.pack();
        MathUtils.polygonTriangulate(polygon, indices);
        indices.pack();

        // set size
        float minX = polygon.get(0);
        float maxX = minX;
        float minY = polygon.get(1);
        float maxY = minY;
        for (int i = 2; i < polygon.size; i += 2) {
            float x = polygon.get(i);
            float y = polygon.get(i + 1);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
        width = maxX - minX;
        height = maxY - minY;
    }

    public void setImage(@Nullable final TextureRegion region) {
        this.image = region;
    }

    public void setImage(@Nullable final Texture texture) {
        if (texture == null) {
            this.image = null;
            return;
        }

        this.image = texture.region;
    }

    @Override
    protected final void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setShader(shader);
        renderer2D.setColor(color);
        renderer2D.drawPolygonFilled(image, polygon, indices, x, y, deg, sclX, sclY);
    }

    @Override
    public final float getWidth() {
        return width;
    }

    @Override
    public final float getHeight() {
        return height;
    }

    @Override
    protected void setHitZone(@NotNull HitZone hitZone) {
        hitZone.setToPolygon(polygon);
    }

}
