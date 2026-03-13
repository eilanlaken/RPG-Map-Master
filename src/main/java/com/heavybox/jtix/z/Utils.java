package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.Tuple2;
import com.heavybox.jtix.math.Shape2DPolygon;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;

public class Utils {

    public static float[] polygonConvertToFlat(Array<Vector2> polygon) {
        float[] polygonFlat = new float[polygon.size * 2];
        for (int i = 0; i < polygon.size; i++) {
            polygonFlat[2*i] = polygon.get(i).x;
            polygonFlat[2*i + 1] = polygon.get(i).y;
        }
        return polygonFlat;
    }

    public static Enum<?> enumNext(Enum<?> value) {
        var values = value.getDeclaringClass().getEnumConstants();
        return values[(value.ordinal() + 1) % values.length];
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

        Array<Vector2> directions = new Array<>(true, n);
        for (int i = 0; i < n; i++) {
            Vector2 tail = edges.get(i).t1;
            Vector2 head = edges.get(i).t2;
            Vector2 direction = new Vector2(head.x - tail.x, head.y - tail.y);
            direction.nor();
            directions.add(direction);
        }

        // calculate weighted sum
        Vector2 sumDir = new Vector2();
        float sumWeights = 0;
        for (int i = 0; i < n; i++) {
            Vector2 direction = directions.get(i);
            float distance = distances.get(i);
            float sigma = 50;
            float invDen = 1.0f / (2 * sigma * sigma);
            float w = (float) Math.exp(-distance * invDen);
            sumDir.add(w * direction.x, w * direction.y);
            sumWeights += w;
        }
        sumDir.scl(1.0f / sumWeights);
        sumDir.nor();
        return sumDir.angleDeg();
    }

}
