package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.math.Vector2;

public class Utils {

    public static float[] polygonConvertToFlat(Array<Vector2> polygon) {
        float[] polygonFlat = new float[polygon.size * 2];
        for (int i = 0; i < polygon.size; i++) {
            polygonFlat[2*i] = polygon.get(i).x;
            polygonFlat[2*i + 1] = polygon.get(i).y;
        }
        return polygonFlat;
    }

    public static void polygonConvertToFlat(Array<Vector2> polygon, ArrayFloat out) {
        out.clear();
        for (Vector2 point : polygon) {
            out.add(point.x);
            out.add(point.y);
        }
    }

}
