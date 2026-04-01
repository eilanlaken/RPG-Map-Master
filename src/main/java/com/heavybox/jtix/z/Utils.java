package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.Tuple2;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Shape2DPolygon;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;

public class Utils {

    private static final Vector2 topLeft = new Vector2();
    private static final Vector2 topRight = new Vector2();
    private static final Vector2 bottomRight = new Vector2();
    private static final Vector2 bottomLeft = new Vector2();

    public static void polygon_generateRandom(float aabb_width, float aabb_height, final @NotNull ArrayFloat out) {
        out.clear();
        // get the 4 corners of the rectangle
        topLeft.set(-aabb_width * 0.5f, aabb_height * 0.5f);
        topRight.set(aabb_width * 0.5f, aabb_height * 0.5f);
        bottomRight.set(aabb_width * 0.5f, -aabb_height * 0.5f);
        bottomLeft.set(-aabb_width * 0.5f, -aabb_height * 0.5f);

        // divide each segment at random, starting with the top segment, going clockwise
        // top segment
        int random_up = MathUtils.randomUniformInt(0, 4);
        if (random_up == 0) { // don't subdivide
            out.add(topLeft.x, topLeft.y);
            out.add(topRight.x, topRight.y);
        } else if (random_up == 1) { // keep left corner, right corner is random
            out.add(topLeft.x, topLeft.y);
            out.add(MathUtils.randomUniformFloat(topLeft.x, topRight.x), topRight.y);
        } else if (random_up == 2) { // left corner is random, keep right corner.
            out.add(MathUtils.randomUniformFloat(topLeft.x, topRight.x), topLeft.y);
            out.add(topRight.x, topRight.y);
        } else { // pick at random two points on the top line segment
            float x1 = MathUtils.randomUniformFloat(topLeft.x, topRight.x);
            out.add(x1, topLeft.y);
            float x2 = MathUtils.randomUniformFloat(x1, topRight.x);
            out.add(x2, topRight.y);
        }

        // right segment
        int random_right = MathUtils.randomUniformInt(0, 4);
        if (random_right == 0) { // don't subdivide
            out.add(topRight.x, topRight.y);
            out.add(bottomRight.x, bottomRight.y);
        } else if (random_right == 1) { // keep top right corner, bottom right corner is random
            out.add(topRight.x, topRight.y);
            out.add(topRight.x, MathUtils.randomUniformFloat(topRight.y, bottomRight.y));
        } else if (random_right == 2) { // top right corner is random, keep bottom right corner.
            out.add(topRight.x, MathUtils.randomUniformFloat(topRight.y, bottomRight.y));
            out.add(bottomRight.x, bottomRight.y);
        } else { // pick at random two points on the top line segment
            float y1 = MathUtils.randomUniformFloat(topRight.y, bottomRight.y);
            out.add(topRight.x, y1);
            float y2 = MathUtils.randomUniformFloat(bottomRight.y, y1);
            out.add(topRight.x, y2);
        }

        // bottom segment
        int random_bottom = MathUtils.randomUniformInt(0, 4);
        if (random_bottom == 0) { // don't subdivide
            out.add(bottomRight.x, bottomRight.y);
            out.add(bottomLeft.x, bottomLeft.y);
        } else if (random_bottom == 1) { // keep right corner, left corner is random
            out.add(bottomRight.x, bottomRight.y);
            out.add(MathUtils.randomUniformFloat(bottomLeft.x, bottomRight.x), bottomRight.y);
        } else if (random_bottom == 2) { // left corner is fixed, right corner is random
            out.add(MathUtils.randomUniformFloat(bottomLeft.x, bottomRight.x), bottomLeft.y);
            out.add(bottomLeft.x, bottomLeft.y);
        } else { // pick at random two points on the top line segment
            float x1 = MathUtils.randomUniformFloat(bottomLeft.x, bottomRight.x);
            float x2 = MathUtils.randomUniformFloat(bottomLeft.x, x1);
            out.add(x1, bottomLeft.y);
            out.add(x2, bottomLeft.y);
        }

        // left segment
        int random_left = MathUtils.randomUniformInt(0, 4);
        if (random_left == 0) { // don't subdivide
            out.add(bottomLeft.x, bottomLeft.y);
            out.add(topLeft.x, topLeft.y);
        } else if (random_left == 1) { // bottom left fixed, top left random
            out.add(bottomLeft.x, bottomLeft.y);
            out.add(bottomLeft.x, MathUtils.randomUniformFloat(bottomLeft.y, topLeft.y));
        } else if (random_left == 2) { // bottom left random, top left fixed
            out.add(topLeft.x, MathUtils.randomUniformFloat(bottomLeft.y, topLeft.y));
            out.add(topLeft.x, topLeft.y);
        } else { // pick at random two points on the top line segment
            float y1 = MathUtils.randomUniformFloat(bottomLeft.y, topLeft.y);
            float y2 = MathUtils.randomUniformFloat(y1, topLeft.y);
            out.add(bottomLeft.x, y1);
            out.add(bottomLeft.x, y2);
        }

        MathUtils.polygonRemoveDegenerateVertices(out);
    }

    public static float[] polygonConvertToFlat(Array<Vector2> polygon) {
        float[] polygonFlat = new float[polygon.size * 2];
        for (int i = 0; i < polygon.size; i++) {
            polygonFlat[2*i] = polygon.get(i).x;
            polygonFlat[2*i + 1] = polygon.get(i).y;
        }
        return polygonFlat;
    }

    public static void polygonConvertToArrayFloat(@NotNull final ArrayFloat flat, @NotNull final Array<Vector2> polygon) {
        flat.clear();
        for (int i = 0; i < polygon.size; i++) {
            flat.add(polygon.get(i).x);
            flat.add(polygon.get(i).y);
        }
    }

    public static void polygonCalculateBoundingBox(@NotNull final Array<Vector2> polygon, @NotNull Vector2 outBottomLeft, @NotNull Vector2 outTopRight) {
        outBottomLeft.x = Float.POSITIVE_INFINITY;
        outBottomLeft.y = Float.POSITIVE_INFINITY;

        outTopRight.x = Float.NEGATIVE_INFINITY;
        outTopRight.y = Float.NEGATIVE_INFINITY;

        for (Vector2 point : polygon) {
            outBottomLeft.x = Math.min(point.x, outBottomLeft.x);
            outBottomLeft.y = Math.min(point.y, outBottomLeft.y);

            outTopRight.x = Math.max(point.x, outTopRight.x);
            outTopRight.y = Math.max(point.y, outTopRight.y);
        }
    }

    public static void polygonCalculateBoundingBox(@NotNull final ArrayFloat polygon, @NotNull Vector2 outBottomLeft, @NotNull Vector2 outTopRight) {
        outBottomLeft.x = Float.POSITIVE_INFINITY;
        outBottomLeft.y = Float.POSITIVE_INFINITY;
        outTopRight.x = Float.NEGATIVE_INFINITY;
        outTopRight.y = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < polygon.size / 2; i++) {
            outBottomLeft.x = Math.min(polygon.get(2 * i), outBottomLeft.x);
            outBottomLeft.y = Math.min(polygon.get(2 * i + 1), outBottomLeft.y);
            outTopRight.x = Math.max(polygon.get(2 * i), outTopRight.x);
            outTopRight.y = Math.max(polygon.get(2 * i + 1), outTopRight.y);
        }
    }

    public static float distancePointToLine(Vector2 p, Vector2 v1, Vector2 v2) {
        Vector2 v1v2 = new Vector2(v2.x - v1.x, v2.y - v1.y);
        Vector2 v1p = new Vector2(p.x - v1.x, p.y - v1.y);
        Vector2 n = new Vector2(v1v2).rotate90(1);
        return Math.abs(Vector2.dot(v1p, n) / n.len());
    }

    // TODO: test, change to getDirections.
    public static ArrayFloat getDirectionVector(float x, float y, Shape2DPolygon polygon, final @NotNull Vector2 out) {
        Array<Tuple2<Vector2, Vector2>> edges = new Array<>();

        final int n = polygon.getNumberOfEdges();
        for (int i = 0; i < n; i++) {
            Vector2 tail = new Vector2();
            Vector2 head = new Vector2();
            polygon.getEdge(i, tail, head);
            Tuple2<Vector2, Vector2> edge = new Tuple2<>(tail, head);
            edges.add(edge);
        }

        ArrayFloat distances = new ArrayFloat(true, n);
        for (int i = 0; i < n; i++) {
            Vector2 p = new Vector2(x, y);
            float distance = distancePointToLine(p, edges.get(i).t1, edges.get(i).t2);
            distances.add(distance);
        }

        Array<Vector2> directions = new Array<>(true, n);
        for (int i = 0; i < n; i++) {
            Vector2 tail = edges.get(i).t1;
            Vector2 head = edges.get(i).t2;
            Vector2 direction = new Vector2(head.x - tail.x, head.y - tail.y);
            direction.nor();
            directions.add(direction);
        }

        return distances;
    }

    // min distance direction
    public static float getDirectionRough(Vector2 p, Shape2DPolygon polygon) {
        Array<Tuple2<Vector2, Vector2>> edges = new Array<>();

        final int n = polygon.getNumberOfEdges();
        for (int i = 0; i < n; i++) {
            Vector2 tail = new Vector2();
            Vector2 head = new Vector2();
            polygon.getEdge(i, tail, head);
            Tuple2<Vector2, Vector2> edge = new Tuple2<>(tail, head);
            edges.add(edge);
        }

        ArrayFloat distances = new ArrayFloat(true, n);
        for (int i = 0; i < n; i++) {
            float distance = distancePointToLine(p, edges.get(i).t1, edges.get(i).t2);
            distances.add(distance);
        }

        float minDistance = Float.POSITIVE_INFINITY;
        int minIndex = 0;
        for (int i = 0; i < distances.size; i++) {
            float current = distances.get(i);
            if (current < minDistance) {
                minIndex = i;
                minDistance = current;
            }
        }

        Array<Vector2> directions = new Array<>(true, n);
        for (int i = 0; i < n; i++) {
            Vector2 tail = edges.get(i).t1;
            Vector2 head = edges.get(i).t2;
            Vector2 direction = new Vector2(head.x - tail.x, head.y - tail.y);
            direction.nor();
            directions.add(direction);
        }

        return directions.get(minIndex).angleDeg();
    }

    // interpolated direction
    public static float getDirectionSmooth(Vector2 p, Shape2DPolygon polygon) {
        Array<Tuple2<Vector2, Vector2>> edges = new Array<>();

        // gets the edges as tuples(tail, head) of vectors
        final int n = polygon.getNumberOfEdges();
        for (int i = 0; i < n; i++) {
            Vector2 tail = new Vector2();
            Vector2 head = new Vector2();
            polygon.getEdge(i, tail, head);
            Tuple2<Vector2, Vector2> edge = new Tuple2<>(tail, head);
            edges.add(edge);
        }

        // gets the distances to each edge
        ArrayFloat distances = new ArrayFloat(true, n);
        for (int i = 0; i < n; i++) {
            float distance = distancePointToLine(p, edges.get(i).t1, edges.get(i).t2);
            distances.add(distance);
        }

        // gets a direction vector for each edge
        Array<Vector2> directions = new Array<>(true, n);
        for (int i = 0; i < n; i++) {
            Vector2 tail = edges.get(i).t1;
            Vector2 head = edges.get(i).t2;
            Vector2 direction = new Vector2(head.x - tail.x, head.y - tail.y);
            direction.nor();
            directions.add(direction);
        }

        // calculate weighted sum of the directions vector and a weight (the further the point from the edge, the less the weight)
        Vector2 sumDir = new Vector2();
        for (int i = 0; i < n; i++) {
            Vector2 direction = directions.get(i);
            float d = distances.get(i);
            float sigma = 150;
            float invDen = 1.0f / (2 * sigma * sigma);
            //float w = (float) Math.exp(-(d * d) * invDen);
            float w = 1f / (0.05f + d*d); // bias: 0.001, 1, 0.05
            sumDir.add(w * direction.x, w * direction.y);
        }
        sumDir.nor();

        // return the angle of the resulting vector
        return sumDir.angleDeg();
    }

}
