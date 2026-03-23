package com.heavybox.jtix.math;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import org.jetbrains.annotations.NotNull;

// TODO: test
// represents a SIMPLE polygon: no intersecting edges, no inverse edges, closed, convex or concave, no holes, connected region
public class Shape2DPolygon implements Shape2D {

    public  ArrayFloat points;
    private ArrayInt   indices;
    private float      area;
    private float      perimeter;
    private Vector2    centroid;
    private boolean    dirty;

    public Shape2DPolygon(@NotNull Array<Vector2> points) {
        if (points.size < 3) throw new MathException("A Polygon must contain at least 3 points. Therefore, the input array points: [x0,y0, x1,y1, ...] must contain at least 6 values. Got - points.size = " + points.size);

        this.points = new ArrayFloat(true, points.size * 2);
        for (Vector2 point : points) {
            this.points.add(point.x, point.y);
        }
        MathUtils.polygonRemoveDegenerateVertices(this.points);
        this.dirty = true;
    }

    public Shape2DPolygon(float ...points) {
        if (points.length < 6) throw new MathException("A Polygon must contain at least 3 points. Therefore, the input array points: [x0,y0, x1,y1, ...] must contain at least 6 values");
        if (points.length % 2 != 0) throw new MathException("points is a flat array of values representing a polygon. A point has a float x and float y values. Therefore points must contain an even number of points.");

        this.points = new ArrayFloat(true, points.length);
        this.points.addAll(points);
        MathUtils.polygonRemoveDegenerateVertices(this.points);
        this.dirty = true;
    }

    public Shape2DPolygon(@NotNull final ArrayFloat points) {
        if (points.size < 6) throw new MathException("A Polygon must contain at least 3 points. Therefore, the input array points: [x0,y0, x1,y1, ...] must contain at least 6 values");
        if (points.size % 2 != 0) throw new MathException("points is a flat array of values representing a polygon. A point has a float x and float y values. Therefore points must contain an even number of points.");

        this.points = new ArrayFloat(true, points.size);
        this.points.addAll(points);
        MathUtils.polygonRemoveDegenerateVertices(this.points);
        this.dirty = true;
    }

    public int getNumberOfVertices() {
        return this.points.size / 2;
    }

    public int getNumberOfEdges() {
        return this.points.size / 2;
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

    private void recalculateMetrics() {
        // triangulate polygon + remove degenerate vertices
        if (indices == null) indices = new ArrayInt(true, 3 * (2 * points.size - 2));
        MathUtils.polygonTriangulate(points, indices);

        // calculate area
        this.area = MathUtils.polygonArea(points);

        // calculate perimeter
        this.perimeter = MathUtils.polygonPerimeter(points);

        // calculate centroid
        if (centroid == null) centroid = new Vector2();
        MathUtils.polygonCenterOfMass(points, centroid);

        dirty = false;
    }

    public ArrayInt indices() {
        if (dirty) recalculateMetrics();
        return indices;
    }

    @Override
    public float area() {
        if (dirty) recalculateMetrics();
        return area;
    }

    @Override
    public float perimeter() {
        if (dirty) recalculateMetrics();
        return perimeter;
    }

    @Override
    public void centerOfMass(Vector2 out) {
        if (dirty) recalculateMetrics();
        out.set(centroid.x, centroid.y);
    }

    public void setPoints(float ...points) {
        this.points.clear();
        this.points.addAll(points);
        dirty = true;
    }

    public void setPoints(final @NotNull Array<Vector2> points) {
        this.points.clear();
        for (Vector2 point : points) {
            this.points.add(point.x, point.y);
        }
        dirty = true;
    }

    public void setPoints(final @NotNull ArrayFloat points) {
        this.points.clear();
        this.points.addAll(points);
        dirty = true;
    }

    public void setToRectangle(float width, float height) {
        points.clear();
        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;
        points.add(-widthHalf, -heightHalf);
        points.add( widthHalf, -heightHalf);
        points.add( widthHalf,  heightHalf);
        points.add(-widthHalf,  heightHalf);
        dirty = true;
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

        dirty = true;
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

        dirty = true;
    }

    public void setToCircle(float r, int refinement) {
        points.clear();
        refinement = Math.max(refinement, 3);
        float da = 360f / refinement;
        for (int i = 0; i < refinement; i++) {
            points.add(r * MathUtils.cosDeg(da * i));
            points.add(r * MathUtils.sinDeg(da * i));
        }

        dirty = true;
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
