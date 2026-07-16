package com.heavybox.jtix.math;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import org.jetbrains.annotations.NotNull;

// TODO: test
// represents a SIMPLE convex or concave, closed polygon: no intersecting edges, no inverse edges, no holes.
public class Shape2DPolygon implements Shape2D {

    public  final ArrayFloat points;
    private final float      area;
    private final float      perimeter;
    private final Vector2    centroid = new Vector2();

    private ArrayInt indices; // calculated on demand

    public Shape2DPolygon(@NotNull Array<Vector2> points) {
        if (points.size < 3) throw new MathException("A Polygon must contain at least 3 points. Therefore, the input array points: [x0,y0, x1,y1, ...] must contain at least 6 values. Got - points.size = " + points.size);

        this.points = new ArrayFloat(true, points.size * 2);
        for (Vector2 point : points) {
            this.points.add(point.x, point.y);
        }
        MathUtils.polygonRemoveDegenerateVertices(this.points);

        this.area = MathUtils.polygonArea(this.points);
        this.perimeter = MathUtils.polygonPerimeter(this.points);
        MathUtils.polygonCenterOfMass(this.points, centroid);
    }

    public Shape2DPolygon(float ...points) {
        if (points.length < 6) throw new MathException("A Polygon must contain at least 3 points. Therefore, the input array points: [x0,y0, x1,y1, ...] must contain at least 6 values");
        if (points.length % 2 != 0) throw new MathException("points is a flat array of values representing a polygon. A point has a float x and float y values. Therefore points must contain an even number of points.");

        this.points = new ArrayFloat(true, points.length);
        this.points.addAll(points);
        MathUtils.polygonRemoveDegenerateVertices(this.points);

        this.area = MathUtils.polygonArea(this.points);
        this.perimeter = MathUtils.polygonPerimeter(this.points);
        MathUtils.polygonCenterOfMass(this.points, centroid);
    }

    public Shape2DPolygon(@NotNull final ArrayFloat points) {
        if (points.size < 6) throw new MathException("A Polygon must contain at least 3 points. Therefore, the input array points: [x0,y0, x1,y1, ...] must contain at least 6 values");
        if (points.size % 2 != 0) throw new MathException("points is a flat array of values representing a polygon. A point has a float x and float y values. Therefore points must contain an even number of points.");

        this.points = new ArrayFloat(true, points.size);
        this.points.addAll(points);
        MathUtils.polygonRemoveDegenerateVertices(this.points);

        this.area = MathUtils.polygonArea(this.points);
        this.perimeter = MathUtils.polygonPerimeter(this.points);
        MathUtils.polygonCenterOfMass(this.points, centroid);
    }

    public int getNumberOfVertices() {
        return this.points.size >> 1;
    }

    public int getNumberOfEdges() {
        return this.points.size >> 1;
    }

    @Override
    public boolean containsPoint(float x, float y) {
        return MathUtils.polygonContainsPoint(points, x, y);
    }

    public void getVertex(int i, @NotNull Vector2 out) {
        if (i < 0 || i >= this.points.size / 2) throw new MathException("Index i: " + i + " out of bounds. Polygon " + this + " contains " + this.points.size / 2 + " vertices");

        int index = i * 2;
        out.set(this.points.get(index), this.points.get(index + 1));
    }

    public void getEdge(int i, @NotNull Vector2 tail, @NotNull Vector2 head) {
        int vertexCount = this.points.size / 2;

        if (i < 0 || i >= vertexCount) throw new MathException("Index i: " + i + " out of bounds. Polygon " + this + " contains " + vertexCount + " edges");

        int tailBase = i * 2;
        int headBase = ((i + 1) % vertexCount) * 2;

        tail.set(points.get(tailBase), points.get(tailBase + 1));
        head.set(points.get(headBase), points.get(headBase + 1));
    }

    public ArrayInt indices() {
        if (indices == null) {
            indices = new ArrayInt(true, 3 * (2 * points.size - 2));
            MathUtils.polygonTriangulate(points, indices);
        }
        return indices;
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
    public void centerOfMass(@NotNull Vector2 out) {
        out.set(centroid.x, centroid.y);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Polygon: ").append('[');
        for (int i = 0; i < points.size / 2; i++) {
            sb.append('(').append(points.get(i * 2)).append(",").append(points.get(i * 2 + 1)).append(')').append(' ');
        }
        sb.append(']');
        return sb.toString();
    }

}
