package com.heavybox.jtix.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Vector3Test {

    @Test
    void testAngleBetweenDeg() {
        Vector3 a = new Vector3(1, 0, 0);
        Vector3 b = new Vector3(1, 0, 0);
        Assertions.assertEquals(0, Vector3.angleBetweenDeg(a,b), MathUtils.FLOAT_ROUNDING_ERROR);

        b.set(0,1,0);
        Assertions.assertEquals(90.0f, Vector3.angleBetweenDeg(a,b), MathUtils.FLOAT_ROUNDING_ERROR);
        b.set(1,1,0);
        Assertions.assertEquals(45.0f, Vector3.angleBetweenDeg(a,b), MathUtils.FLOAT_ROUNDING_ERROR);
        b.set(0,-1,0);
        Assertions.assertEquals(90.0f, Vector3.angleBetweenDeg(a,b), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void rotTest() {
        Vector3 v1 = new Vector3(1,0,0);
        Quaternion q1 = new Quaternion().setFromAxisDeg(0,0,1,90);

        v1.rot(q1);
        Assertions.assertEquals(Vector3.Y_UNIT.x, v1.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(Vector3.Y_UNIT.y, v1.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(Vector3.Y_UNIT.z, v1.z, MathUtils.FLOAT_ROUNDING_ERROR);
    }

}
