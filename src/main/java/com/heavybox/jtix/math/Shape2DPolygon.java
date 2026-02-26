package com.heavybox.jtix.math;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;

// TODO: test everything here.
public class Shape2DPolygon implements Shape2D {

    public  final float[] points;
    public  final int[]   indices;
    private final float   area;
    private final float   perimeter;
    private final Vector2 centroid;

    // TODO: test with both normal and degenerate vertices
    public Shape2DPolygon(float ...points) {
        if (points.length < 6) throw new MathException("A Polygon must contain at least 3 points. Therefore, the input array points: [x0,y0, x1,y1, ...] must contain at least 6 values");
        if (points.length % 2 != 0) throw new MathException("points is a flat array of values representing a polygon. A point has a float x and float y values. Therefore points must contain an even number of points.");

        ArrayFloat outVertices = new ArrayFloat(true, points.length);
        ArrayInt outIndices = new ArrayInt(true, 3 * (2 * points.length - 2));
        MathUtils.polygonTriangulate(points, outVertices, outIndices);

        this.points = outVertices.pack();
        this.indices = outIndices.pack();

        float sumArea = 0;
        float sumPerimeter = 0;
        int n = indices.length;

        // calculate area
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            float xi = points[indices[i] * 2];
            float yi = points[indices[i] * 2 + 1];
            float xj = points[indices[j] * 2];
            float yj = points[indices[j] * 2 + 1];
            sumArea += xi * yj - xj * yi;
        }
        this.area = Math.abs(sumArea) * 0.5f;

        // calculate perimeter
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n; // next vertex, wrap around
            float xi = points[indices[i] * 2];
            float yi = points[indices[i] * 2 + 1];
            float xj = points[indices[j] * 2];
            float yj = points[indices[j] * 2 + 1];
            float dx = xj - xi;
            float dy = yj - yi;
            sumPerimeter += (float) Math.sqrt(dx * dx + dy * dy);
        }
        this.perimeter = sumPerimeter;

        // calculate centroid
        float cx = 0, cy = 0;
        float triArea = 0;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            float xi = points[indices[i] * 2];
            float yi = points[indices[i] * 2 + 1];
            float xj = points[indices[j] * 2];
            float yj = points[indices[j] * 2 + 1];
            float a = xi * yj - xj * yi; // partial area
            triArea += a;
            cx += (xi + xj) * a;
            cy += (yi + yj) * a;
        }
        triArea *= 0.5f;
        cx /= (6 * triArea);
        cy /= (6 * triArea);
        centroid = new Vector2(cx, cy);
    }

    @Override
    public boolean containsPoint(float x, float y) {
        return MathUtils.polygonContainsPoint(points, x, y);
    }

    @Override
    public boolean containsPoint(float x, float y, Transform2D t) {
        float dx = x - t.x;
        float dy = y - t.y;

        // 2. inverse rotate
        float rad = (float) Math.toRadians(-t.deg);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float lx = dx * cos - dy * sin;
        float ly = dx * sin + dy * cos;

        // 3. inverse scale
        lx /= t.sclX;
        ly /= t.sclY;

        // 4. test polygon in local space
        return MathUtils.polygonContainsPoint(points, lx, ly);
    }

    @Override
    public float area() {
        return area;
    }

    @Override
    public float perimeter() {
        return perimeter;
    }

    @Override
    public void centroid(Vector2 out) {
        out.set(centroid.x, centroid.y);
    }

}
