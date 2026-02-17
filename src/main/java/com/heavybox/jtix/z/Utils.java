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

    public static Enum<?> enumNext(Enum<?> value) {
        var values = value.getDeclaringClass().getEnumConstants();
        return values[(value.ordinal() + 1) % values.length];
    }

}
