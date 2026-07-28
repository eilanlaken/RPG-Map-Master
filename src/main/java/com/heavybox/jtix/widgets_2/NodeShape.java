package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import org.jetbrains.annotations.NotNull;

// TODO: finish all shapes and fill types.
public class NodeShape extends Node {

    public Color color = Color.randomOpaque();

    private final ArrayFloat points  = new ArrayFloat(true, 8);
    private final ArrayInt   indices = new ArrayInt(true, 6);

    private float width;
    private float height;

    /* rectangle constructor */
    public NodeShape(float width, float height) {
        setToRectangle(width, height);
    }

    /* circle constructor */
    public NodeShape(float r) {
        setToCircle(r, 20);
    }

    public void setToRectangle(float width, float height) {
        points.clear();
        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;
        points.add(-widthHalf, -heightHalf);
        points.add( widthHalf, -heightHalf);
        points.add( widthHalf,  heightHalf);
        points.add(-widthHalf,  heightHalf);
        updateInternals();
    }

    public void setToCircle(float r, int refinement) {
        points.clear();
        refinement = Math.max(refinement, 3);
        float da = 360f / refinement;
        for (int i = 0; i < refinement; i++) {
            points.add(r * MathUtils.cosDeg(da * i));
            points.add(r * MathUtils.sinDeg(da * i));
        }
        updateInternals();
    }

    public void setToPolygon(final float[] polygonPoints) {
        points.clear();
        points.addAll(polygonPoints);
        updateInternals();
    }

    private void updateInternals() {
        if (points.size < 6 || points.size % 2 != 0) return;
        // triangulate
        points.pack();
        MathUtils.polygonTriangulate(points, indices);
        indices.pack();
        // set size
        float minX = points.get(0);
        float maxX = minX;
        float minY = points.get(1);
        float maxY = minY;
        for (int i = 2; i < points.size; i += 2) {
            float x = points.get(i);
            float y = points.get(i + 1);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
        width = maxX - minX;
        height = maxY - minY;
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.setColor(color);
        renderer2D.drawPolygonFilled(points, indices, x, y, deg, sclX, sclY);
    }

    @Override
    protected float getWidth() {
        return width;
    }

    @Override
    protected float getHeight() {
        return height;
    }

    @Override
    protected void setInputShape(@NotNull InputShape shape) {
        shape.setToPolygon(points.items);
    }

    public enum FillType {
        FILLED,
        BORDER,
        THIN,
    }

}
