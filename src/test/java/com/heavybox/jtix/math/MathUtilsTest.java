package com.heavybox.jtix.math;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MathUtilsTest {

    @Test
    void random() {
    }

    @Test
    void testRandom() {
    }

    @Test
    void testFloor() {
        Assertions.assertEquals(Math.floor(1.05f), MathUtils.floor(1.05f));
        Assertions.assertEquals(Math.floor(0.05f), MathUtils.floor(0.05f));
        Assertions.assertEquals(Math.floor(4.82f), MathUtils.floor(4.82f));
        Assertions.assertEquals(Math.floor(-1.05f), MathUtils.floor(-1.05f));
    }

    @Test
    void getAreaTriangle() {
        Assertions.assertEquals(0.5f, MathUtils.getAreaTriangle(0.0f,0.0f,1.0f,0.0f,0.0f,1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.5f, MathUtils.getAreaTriangle(1.0f,0.0f,0.0f,1.0f,1.0f,1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void nextPowerOf2f() {
        Assertions.assertEquals(1, MathUtils.nextPowerOf2f(0));
        Assertions.assertEquals(1, MathUtils.nextPowerOf2f(1));
        Assertions.assertEquals(2, MathUtils.nextPowerOf2f(2));
        Assertions.assertEquals(4, MathUtils.nextPowerOf2f(3));
        Assertions.assertEquals(4, MathUtils.nextPowerOf2f(4));
        Assertions.assertEquals(8, MathUtils.nextPowerOf2f(5));
        Assertions.assertEquals(16, MathUtils.nextPowerOf2f(9));
        Assertions.assertEquals(32, MathUtils.nextPowerOf2f(22));
        Assertions.assertEquals(1, MathUtils.nextPowerOf2f(-1));
        Assertions.assertEquals(1, MathUtils.nextPowerOf2f(-2.4f));
    }

    @Test
    void nextPowerOf2i() {
        Assertions.assertEquals(1, MathUtils.nextPowerOf2i(1));
        Assertions.assertEquals(2, MathUtils.nextPowerOf2i(2));
        Assertions.assertEquals(4, MathUtils.nextPowerOf2i(3));
        Assertions.assertEquals(4, MathUtils.nextPowerOf2i(4));
        Assertions.assertEquals(8, MathUtils.nextPowerOf2i(5));
        Assertions.assertEquals(16, MathUtils.nextPowerOf2i(9));
        Assertions.assertEquals(32, MathUtils.nextPowerOf2i(22));
        Assertions.assertEquals(1, MathUtils.nextPowerOf2i(-1));
        Assertions.assertEquals(1, MathUtils.nextPowerOf2i(0));
    }

    @Test
    void getAreaTriangle2() {
        Vector2 A = new Vector2();
        Vector2 B = new Vector2();
        Vector2 C = new Vector2();

        A.set(0, 0);
        B.set(4, 0);
        C.set(0, 3);
        Assertions.assertEquals(6.0f, MathUtils.getAreaTriangle(A,B,C), MathUtils.FLOAT_ROUNDING_ERROR);

        A.set(-1,-1);
        B.set(3,-1);
        C.set(-1, 2);
        Assertions.assertEquals(6.0f, MathUtils.getAreaTriangle(A,B,C), MathUtils.FLOAT_ROUNDING_ERROR);

        A.set(0,0);
        B.set(2,2);
        C.set(4,4);
        Assertions.assertEquals(0.0f, MathUtils.getAreaTriangle(A,B,C), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void normalizeAngleDeg() {
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleDeg(0.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleDeg(360.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, MathUtils.normalizeAngleDeg(1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(359.0f, MathUtils.normalizeAngleDeg(-1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(20.0f, MathUtils.normalizeAngleDeg(380.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(20.0f, MathUtils.normalizeAngleDeg(380.0f + 360.0f * 5), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void normalizeAngleRad() {
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleRad(0.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, MathUtils.normalizeAngleRad(MathUtils.PI_TWO), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, MathUtils.normalizeAngleRad(1.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.1f, MathUtils.normalizeAngleRad(MathUtils.PI_TWO + 0.1f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(MathUtils.PI_TWO - 0.1f, MathUtils.normalizeAngleRad(-0.1f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.1f, MathUtils.normalizeAngleRad(MathUtils.PI_TWO * 3 + 0.1f), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    // TODO: add more tests.
    @Test
    void polygonContainsPoint() {
        // Square polygon - clockwise
        float[] p1 = new float[] {
                0.0f, 0.0f, // Bottom left
                0.0f, 1.0f, // Top left
                1.0f, 1.0f, // Top right
                1.0f, 0.0f  // Bottom right
        };

        Assertions.assertTrue(MathUtils.polygonContainsPoint(p1,0.5f,0.5f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p1,0.2f,0.9f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p1,0.9f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p1,1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p1,1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p1,-1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p1,Float.POSITIVE_INFINITY,0.1f));

        // Triangle polygon
        float[] p2 = new float[] {
                0.0f, 0.0f, // Left corner
                1.0f, 0.0f, // Right corner
                0.5f, 1.0f  // Top corner
        };

        Assertions.assertTrue(MathUtils.polygonContainsPoint(p2, 0.5f, 0.5f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p2, 0.5f, 0.0f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p2, 0.0f, 0.0f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p2, 1.0f, 1.0f));

        // Square polygon - counter-clockwise
        float[] p3 = new float[] {
                1.0f, 0.0f,  // Bottom right
                1.0f, 1.0f, // Top right
                0.0f, 1.0f, // Top left
                0.0f, 0.0f, // Bottom left
        };

        Assertions.assertTrue(MathUtils.polygonContainsPoint(p3,0.5f,0.5f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p3,0.2f,0.9f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p3,0.9f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p3,1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p3,1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p3,-1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p3,Float.POSITIVE_INFINITY,0.1f));

        // Square polygon - clockwise with degenerate vertices
        float[] p4 = new float[] {
                0.0f, 0.0f, // Bottom left
                0.0f, 0.0f, // Bottom left
                0.0f, 1.0f, // Top left
                0.5f, 1.0f, // Top left - top right
                1.0f, 1.0f, // Top right
                1.0f, 0.0f,  // Bottom right
                1.0f, 0.0f,  // Bottom right
        };
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p4,0.5f,0.5f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p4,0.2f,0.9f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p4,0.9f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4,1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4,1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4,-1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4,Float.POSITIVE_INFINITY,0.1f));

        ArrayFloat p4_ArrayFloat = new ArrayFloat(p4);
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p4_ArrayFloat,0.5f,0.5f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p4_ArrayFloat,0.2f,0.9f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p4_ArrayFloat,0.9f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4_ArrayFloat,1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4_ArrayFloat,1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4_ArrayFloat,-1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4_ArrayFloat,Float.POSITIVE_INFINITY,0.1f));

        ArrayFloat p4_removedDegenerateVertices = new ArrayFloat();
        MathUtils.polygonRemoveDegenerateVertices(p4, p4_removedDegenerateVertices);
        float[] p4_out = p4_removedDegenerateVertices.pack();
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p4_out,0.5f,0.5f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p4_out,0.2f,0.9f));
        Assertions.assertTrue(MathUtils.polygonContainsPoint(p4_out,0.9f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4_out,1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4_out,1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4_out,-1.2f,0.1f));
        Assertions.assertFalse(MathUtils.polygonContainsPoint(p4_out,Float.POSITIVE_INFINITY,0.1f));
    }

    @Test
    public void clampFloat() {
        float v1 = MathUtils.clampFloat(0.0f, -1.0f, 1.0f);
        Assertions.assertEquals(0.0f, v1, MathUtils.FLOAT_ROUNDING_ERROR);

        float v2 = MathUtils.clampFloat(-3.0f, -1.0f, 1.0f);
        Assertions.assertEquals(-1.0f, v2, MathUtils.FLOAT_ROUNDING_ERROR);

        float v3 = MathUtils.clampFloat(2.0f, -1.0f, 1.0f);
        Assertions.assertEquals(1.0f, v3, MathUtils.FLOAT_ROUNDING_ERROR);

        float v4 = MathUtils.clampFloat(0.0f, 1.0f, -1.0f);
        Assertions.assertEquals(0.0f, v4, MathUtils.FLOAT_ROUNDING_ERROR);

        float v5 = MathUtils.clampFloat(8.0f, -1.0f, 10.0f);
        Assertions.assertEquals(8.0f, v5, MathUtils.FLOAT_ROUNDING_ERROR);

        float v6 = MathUtils.clampFloat(4.0f, 3.0f, -1.0f);
        Assertions.assertEquals(3.0f, v6, MathUtils.FLOAT_ROUNDING_ERROR);

        float v7 = MathUtils.clampFloat(2.0f, 2.0f, 2.0f);
        Assertions.assertEquals(2.0f, v7, MathUtils.FLOAT_ROUNDING_ERROR);

        float v8 = MathUtils.clampFloat(Float.POSITIVE_INFINITY, -1, 1);
        Assertions.assertEquals(1.0f, v8, MathUtils.FLOAT_ROUNDING_ERROR);

        float v9 = MathUtils.clampFloat(Float.NEGATIVE_INFINITY, -1, 1);
        Assertions.assertEquals(-1.0f, v9, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void nextPowerOfTwo() {
    }

    @Test
    void atanUnchecked() {
    }

    @Test
    void atan2() {
    }

    @Test
    void areaTriangle() {
    }

    @Test
    void testAreaTriangle() {
    }

    @Test
    void max() {
    }

    @Test
    void testMax() {
    }

    @Test
    void sin() {
    }

    @Test
    void cos() {
    }

    @Test
    void sinDeg() {
    }

    @Test
    void cosDeg() {
    }

    @Test
    void tan() {
    }

    @Test
    void acos() {
    }

    @Test
    void asin() {
    }

    @Test
    void tanDeg() {
    }

    @Test
    void atan() {
    }

    @Test
    void asinDeg() {
    }

    @Test
    void acosDeg() {
    }

    @Test
    void atanDeg() {
    }

    @Test
    void isZero() {
    }

    @Test
    void testIsZero() {
    }

    @Test
    void isEqual() {
    }

    @Test
    void testIsEqual() {
    }

    @Test
    void log() {
    }

    @Test
    void testRandom1() {
    }

    @Test
    void testRandom2() {
    }

    @Test
    void testClamp4() {
    }

    @Test
    void testClamp5() {
    }

    @Test
    void testClamp6() {
    }

    @Test
    void testClamp7() {
    }

    @Test
    void testClamp8() {
    }

    @Test
    void testNextPowerOfTwo() {
    }

    @Test
    void testAtanUnchecked() {
    }

    @Test
    void testAtan2() {
    }

    @Test
    void testAreaTriangle1() {
    }

    @Test
    void testAreaTriangle2() {
    }

    @Test
    void testMax1() {
    }

    @Test
    void testMax2() {
    }

    @Test
    void min() {
    }

    @Test
    void testMin() {
    }

    @Test
    void intervalsOverlap() {
        Assertions.assertEquals(0.0f, MathUtils.intervalsOverlap(0.0f, 1.0f, 2.0f, 4.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.0f, MathUtils.intervalsOverlap(9.0f, 8.0f, 4.0f, 2.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, MathUtils.intervalsOverlap(0.0f, 4.0f, 1.0f, 3.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(3.0f, MathUtils.intervalsOverlap(1.0f, 5.0f, 2.0f, 6.5f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.2f, MathUtils.intervalsOverlap(-1.2f, 1.2f,0.0f, 1.2f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, MathUtils.intervalsOverlap(2.0f, 4.0f, 2.0f, 4.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, MathUtils.intervalsOverlap(2.0f, 4.0f, 1.0f, 3.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, MathUtils.intervalsOverlap(2.0f, -2.0f, 0.0f, 2.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2.0f, MathUtils.intervalsOverlap(0.0f, -2.0f, 0.0f, -2.0f), MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, MathUtils.intervalsOverlap(-1.0f, 1.0f, 0.0f, -3.0f), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void segmentsIntersection() {
        Vector2 a1 = new Vector2();
        Vector2 a2 = new Vector2();
        Vector2 b1 = new Vector2();
        Vector2 b2 = new Vector2();
        Vector2 out = new Vector2();

        /* X */
        a1.set(0, 0);
        a2.set(1, 1);
        b1.set(0, 1);
        b2.set(1, 0);
        int i1 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(0, i1);
        Assertions.assertEquals(0.5f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0.5f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*  ---|--- */
        a1.set(0, 0);
        a2.set(0, 2);
        b1.set(-4, 1);
        b2.set(5, 1);
        int i2 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(0, i2);
        Assertions.assertEquals(0f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
           |
           |
           .
           |
           |
        */
        a1.set(0, 0);
        a2.set(0, 0);
        b1.set(-4, 0);
        b2.set(5, 0);
        int i3 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(0, i3);
        Assertions.assertEquals(0, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
          ------

          ------
         */
        a1.set(0, 0);
        a2.set(1, 0);
        b1.set(0, 1);
        b2.set(1, 1);
        int i4 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(-1, i4);
        Assertions.assertEquals(Float.NaN, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(Float.NaN, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
        --      --
         */
        a1.set(0, 0);
        a2.set(1, 0);
        b1.set(4, 0);
        b2.set(5, 0);
        int i5 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(-2, i5);
        Assertions.assertEquals(2.5f, out.x);
        Assertions.assertEquals(0, out.y);

        /*
        --~-~-~~~
         */
        a1.set(0, 0);
        a2.set(2, 0);
        b1.set(1, 0);
        b2.set(5, 0);
        int i6 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(-2, i6);
        Assertions.assertEquals(2.0f, out.x);
        Assertions.assertEquals(0.0f, out.y);

        /*
        ----~~~~
         */
        a1.set(0, 0);
        a2.set(2, 0);
        b1.set(2, 0);
        b2.set(4, 0);
        int i7 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(0, i7);
        Assertions.assertEquals(2, out.x);
        Assertions.assertEquals(0, out.y);

        /*
        |
        |
        |

  -----
        */
        a1.set(0, 10);
        a2.set(0, 5);
        b1.set(-8, 0);
        b2.set(-4, 0);
        int i8 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(3, i8);
        Assertions.assertEquals(0f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
            |
            |
            |
            |
            |
         */
        a1.set(0, 0);
        a2.set(0, 5);
        b1.set(0, 5);
        b2.set(0, 10);
        int i9 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(0, i9);
        Assertions.assertEquals(0f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(5f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
            |
            |
            |
            |
            |
         */
        a1.set(0, 0);
        a2.set(0, 5);
        b1.set(0, 10);
        b2.set(0, 5);
        int i10 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(0, i10);
        Assertions.assertEquals(0f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(5f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
                /        \
              /           \
            /              \
        */
        a1.set(0, 0);
        a2.set(5, 5);
        b1.set(15, 5);
        b2.set(20, 0);
        int i11 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(3, i11);
        Assertions.assertEquals(10f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(10f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

         /*
                /
              /
            /

   ---------------------
        */
        a1.set(0, 0);
        a2.set(5, 5);
        b1.set(-15, -1);
        b2.set(15, -1);
        int i12 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(2, i12);
        Assertions.assertEquals(-1.0f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-1.0f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
                /
              /
            /

   ---------------------
        */
        a1.set(-15, -1);
        a2.set(15, -1);
        b1.set(0,0);
        b2.set(5,5);
        int i13 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(1, i13);
        Assertions.assertEquals(-1.0f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(-1.0f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
           .  .
        */
        a1.set(1,1);
        a2.set(1,1);
        b1.set(2,2);
        b2.set(2,2);
        int i14 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(-1, i14);
        Assertions.assertEquals(Float.NaN, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(Float.NaN, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
           .-----
        */
        a1.set(1,1);
        a2.set(1,1);
        b1.set(1,1);
        b2.set(1,2);
        int i15 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(0, i15);
        Assertions.assertEquals(1.0f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
           .
        */
        a1.set(1,1);
        a2.set(1,1);
        b1.set(1,1);
        b2.set(1,1);
        int i16 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(0, i16);
        Assertions.assertEquals(1.0f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(1.0f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);

        /*
           .
        */
        a1.set(0,0);
        a2.set(5,5);
        b1.set(3,3);
        b2.set(6,6);
        int i17 = MathUtils.segmentsIntersection(a1, a2, b1, b2, out);
        Assertions.assertEquals(-2, i17);
        Assertions.assertEquals(3.5f, out.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(3.5f, out.y, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void pointOnSegment() {
        Vector2 p  = new Vector2();
        Vector2 a1 = new Vector2();
        Vector2 a2 = new Vector2();

        p.set(0.4f,0);
        a1.set(0,0);
        a2.set(1,0);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.4f,0);
        a1.set(0,0);
        a2.set(1,0);
        Assertions.assertFalse(MathUtils.pointOnSegment(p, a1, a2));

        p.set(0.5f,0.5f);
        a1.set(0,0);
        a2.set(1,1);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.0f,1.0f);
        a1.set(0,0);
        a2.set(1,1);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(-0.5f,-0.5f);
        a1.set(-0.5f,-0.5f);
        a2.set(1,1);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(0.5f,0.6f);
        a1.set(0,0);
        a2.set(1,1);
        Assertions.assertFalse(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.0f,0.0f);
        a1.set(0,0);
        a2.set(Float.POSITIVE_INFINITY, 0);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.0f,0.0f);
        a1.set(0,0);
        a2.set(-Float.POSITIVE_INFINITY, 0);
        Assertions.assertFalse(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.0f,1.0f);
        a1.set(0,0);
        a2.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        Assertions.assertTrue(MathUtils.pointOnSegment(p, a1, a2));

        p.set(1.0f,-1.0f);
        a1.set(0,0);
        a2.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        Assertions.assertFalse(MathUtils.pointOnSegment(p, a1, a2));
    }

    @Test
    void testIsNumeric() {
        Assertions.assertTrue(MathUtils.isNumeric(4.0f));
        Assertions.assertTrue(MathUtils.isNumeric(Float.MIN_VALUE));
        Assertions.assertTrue(MathUtils.isNumeric(Float.MAX_VALUE));
        Assertions.assertFalse(MathUtils.isNumeric(4.0f / 0f));
        Assertions.assertFalse(MathUtils.isNumeric(Float.NaN));
        Assertions.assertFalse(MathUtils.isNumeric(Float.POSITIVE_INFINITY));
        Assertions.assertFalse(MathUtils.isNumeric(Float.NEGATIVE_INFINITY));
    }

    @Test
    void testSin() {

    }

    @Test
    void testCos() {
    }

    @Test
    void testSinDeg() {
    }

    @Test
    void testCosDeg() {
    }

    @Test
    void testTan() {
    }

    @Test
    void testAcos() {
    }

    @Test
    void testAsin() {
    }

    @Test
    void testTanDeg() {
    }

    @Test
    void testAtan() {
    }

    @Test
    void testAsinDeg() {
    }

    @Test
    void testAcosDeg() {
    }

    @Test
    void testAtanDeg() {
    }

    @Test
    void testIsZero1() {
    }

    @Test
    void testIsZero2() {
    }

    @Test
    void testIsEqual1() {
    }

    @Test
    void testIsEqual2() {
    }

    @Test
    void testLog() {
    }

    @Test
    void areCollinear() {
        Vector2 v1 = new Vector2();
        Vector2 v2 = new Vector2();
        Vector2 v3 = new Vector2();

        v1.set(0,0);
        v2.set(1,0);
        v3.set(2,0);
        Assertions.assertTrue(Vector2.areCollinear(v1,v2,v3));

        v1.set(0,1);
        v2.set(0,2);
        v3.set(0,3);
        Assertions.assertTrue(Vector2.areCollinear(v1,v2,v3));

        v1.set(0,0);
        v2.set(1,1);
        v3.set(2,2);
        Assertions.assertTrue(Vector2.areCollinear(v1,v2,v3));

        v1.set(0,0);
        v2.set(1,1);
        v3.set(-5,-5);
        Assertions.assertTrue(Vector2.areCollinear(v1,v2,v3));

        v1.set(0,0);
        v2.set(1,1);
        v3.set(2,0);
        Assertions.assertFalse(Vector2.areCollinear(v1,v2,v3));

        v1.set(-1,-1);
        v2.set(2,-2);
        v3.set(4,3);
        Assertions.assertFalse(Vector2.areCollinear(v1,v2,v3));

        v1.set(0,0);
        v2.set(0,-6);
        v3.set(2,5);
        Assertions.assertFalse(Vector2.areCollinear(v1,v2,v3));

        v1.set(10,-10);
        v2.set(100,100);
        v3.set(-555,-555);
        Assertions.assertFalse(Vector2.areCollinear(v1,v2,v3));
    }

    @Test
    void pointInTriangle() {
        Vector2 p = new Vector2();
        Vector2 a = new Vector2();
        Vector2 b = new Vector2();
        Vector2 c = new Vector2();

        p.set(0,0);
        a.set(-1,-1);
        b.set(1,-1);
        c.set(1,1);
        Assertions.assertTrue(MathUtils.pointInTriangle(p, a, b, c));
        Assertions.assertTrue(MathUtils.pointInTriangle(p, c, a, b));
        Assertions.assertTrue(MathUtils.pointInTriangle(p, b, a, c));

        p.set(10,0);
        a.set(-1,-1);
        b.set(1,-1);
        c.set(1,1);
        Assertions.assertFalse(MathUtils.pointInTriangle(p, a, b, c));
        Assertions.assertFalse(MathUtils.pointInTriangle(p, c, a, b));
        Assertions.assertFalse(MathUtils.pointInTriangle(p, b, a, c));

        p.set(3,3);
        a.set(-1,-1);
        b.set(10,-1);
        c.set(10,10);
        Assertions.assertTrue(MathUtils.pointInTriangle(p, a, b, c));
        Assertions.assertTrue(MathUtils.pointInTriangle(p, c, a, b));
        Assertions.assertTrue(MathUtils.pointInTriangle(p, b, a, c));
    }

    @Test
    void pointInTriangle2() {
        Vector2 p = new Vector2();
        Vector2 a = new Vector2();
        Vector2 b = new Vector2();
        Vector2 c = new Vector2();

        p.set(0,0);
        a.set(-1,-1);
        b.set(1,-1);
        c.set(1,1);
        Assertions.assertTrue(MathUtils.pointInTriangle(p.x, p.y, a.x, a.y, b.x, b.y, c.x, c.y));
        Assertions.assertTrue(MathUtils.pointInTriangle(p.x, p.y, c.x, c.y, a.x, a.y, b.x, b.y));
        Assertions.assertTrue(MathUtils.pointInTriangle(p.x, p.y, b.x, b.y, a.x, a.y, c.x, c.y));
    }

    @Test
    void windingOrder() {
        Array<Vector2> vertices_flat = new Array<>();

        vertices_flat.clear();
        vertices_flat.add(new Vector2(0, 0));
        vertices_flat.add(new Vector2(1, 0));
        vertices_flat.add(new Vector2(1, 1));
        vertices_flat.add(new Vector2(0, 1));

        Assertions.assertEquals(-1, MathUtils.polygonWindingOrder(vertices_flat));
        vertices_flat.reverse();
        Assertions.assertEquals(1, MathUtils.polygonWindingOrder(vertices_flat));

        float[] vertices_array = {0,0,   1,0,   1,1,   0,1};
        Assertions.assertEquals(-1, MathUtils.polygonWindingOrder(vertices_array));

        ArrayFloat vertices_floats = ArrayFloat.with(0,0,   1,0,   1,1,   0,1);
        Assertions.assertEquals(-1, MathUtils.polygonWindingOrder(vertices_floats));
    }

    @Test
    void testSignedArea() {
        Vector2 p0 = new Vector2();
        Vector2 p1 = new Vector2();
        Vector2 p2 = new Vector2();

        p0.set(0,0);
        p1.set(1,1);
        p2.set(2,2);
        Assertions.assertEquals(0, MathUtils.areaTriangleSigned(p0, p1, p2), MathUtils.FLOAT_ROUNDING_ERROR);

        p0.set(0,0);
        p1.set(1,0);
        p2.set(1,1);
        Assertions.assertEquals(0.5f, MathUtils.areaTriangleSigned(p0, p1, p2), MathUtils.FLOAT_ROUNDING_ERROR);

        p0.set(1,1);
        p1.set(1,0);
        p2.set(0,0);
        Assertions.assertEquals(-0.5f, MathUtils.areaTriangleSigned(p0, p1, p2), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void polygonRemoveDegenerateVertices() {
        Array<Vector2> polygon = new Array<>();

        polygon.clear();
        polygon.add(new Vector2(0,0));
        polygon.add(new Vector2(1,0));
        polygon.add(new Vector2(1,1));
        polygon.add(new Vector2(0.5f,1));
        polygon.add(new Vector2(0,1));
        MathUtils.polygonRemoveDegenerateVertices(polygon);
        Assertions.assertEquals(4, polygon.size);
        Assertions.assertEquals(new Vector2(0,0), polygon.get(0));
        Assertions.assertEquals(new Vector2(1,0), polygon.get(1));
        Assertions.assertEquals(new Vector2(1,1), polygon.get(2));
        Assertions.assertEquals(new Vector2(0,1), polygon.get(3));

        polygon.clear();
        polygon.add(new Vector2(0,0));
        polygon.add(new Vector2(1,0));
        polygon.add(new Vector2(1,1));
        polygon.add(new Vector2(0.5f,1));
        polygon.add(new Vector2(0.5f,1));
        polygon.add(new Vector2(0.5f,1));
        polygon.add(new Vector2(0,1));
        MathUtils.polygonRemoveDegenerateVertices(polygon);
        Assertions.assertEquals(4, polygon.size);
        Assertions.assertEquals(new Vector2(0,0), polygon.get(0));
        Assertions.assertEquals(new Vector2(1,0), polygon.get(1));
        Assertions.assertEquals(new Vector2(1,1), polygon.get(2));
        Assertions.assertEquals(new Vector2(0,1), polygon.get(3));

        polygon.clear();
        polygon.add(new Vector2(0,0));
        polygon.add(new Vector2(1,0));
        polygon.add(new Vector2(2,0));
        polygon.add(new Vector2(2,1));
        polygon.add(new Vector2(2,2));
        polygon.add(new Vector2(2,4));
        polygon.add(new Vector2(1,4));
        polygon.add(new Vector2(0,4));
        MathUtils.polygonRemoveDegenerateVertices(polygon);
        Assertions.assertEquals(4, polygon.size);
        Assertions.assertEquals(new Vector2(0,0), polygon.get(0));
        Assertions.assertEquals(new Vector2(2,0), polygon.get(1));
        Assertions.assertEquals(new Vector2(2,4), polygon.get(2));
        Assertions.assertEquals(new Vector2(0,4), polygon.get(3));

        polygon.clear();
        polygon.add(new Vector2(0,0));
        polygon.add(new Vector2(1,0));
        polygon.add(new Vector2(2,0));
        polygon.add(new Vector2(2,1));
        polygon.add(new Vector2(2,2));
        polygon.add(new Vector2(2,4));
        polygon.add(new Vector2(1,4));
        polygon.add(new Vector2(0,4));
        polygon.add(new Vector2(0,4));
        polygon.add(new Vector2(0,4));
        MathUtils.polygonRemoveDegenerateVertices(polygon);
        Assertions.assertEquals(4, polygon.size);
        Assertions.assertEquals(new Vector2(0,0), polygon.get(0));
        Assertions.assertEquals(new Vector2(2,0), polygon.get(1));
        Assertions.assertEquals(new Vector2(2,4), polygon.get(2));
        Assertions.assertEquals(new Vector2(0,4), polygon.get(3));
    }

    @Test
    void polygonRemoveDegenerateVertices_2() {
        Array<Vector2> polygon = new Array<>();
        Array<Vector2> outVertices = new Array<>();

        polygon.clear();
        polygon.add(new Vector2(0,0));
        polygon.add(new Vector2(1,0));
        polygon.add(new Vector2(1,1));
        polygon.add(new Vector2(0.5f,1));
        polygon.add(new Vector2(0,1));
        MathUtils.polygonRemoveDegenerateVertices(polygon, outVertices);
        Assertions.assertEquals(4, outVertices.size);
        Assertions.assertEquals(new Vector2(0,0), outVertices.get(0));
        Assertions.assertEquals(new Vector2(1,0), outVertices.get(1));
        Assertions.assertEquals(new Vector2(1,1), outVertices.get(2));
        Assertions.assertEquals(new Vector2(0,1), outVertices.get(3));

        polygon.clear();
        polygon.add(new Vector2(0,0));
        polygon.add(new Vector2(1,0));
        polygon.add(new Vector2(1,1));
        polygon.add(new Vector2(0.5f,1));
        polygon.add(new Vector2(0.5f,1));
        polygon.add(new Vector2(0.5f,1));
        polygon.add(new Vector2(0,1));
        MathUtils.polygonRemoveDegenerateVertices(polygon, outVertices);
        Assertions.assertEquals(4, outVertices.size);
        Assertions.assertEquals(new Vector2(0,0), outVertices.get(0));
        Assertions.assertEquals(new Vector2(1,0), outVertices.get(1));
        Assertions.assertEquals(new Vector2(1,1), outVertices.get(2));
        Assertions.assertEquals(new Vector2(0,1), outVertices.get(3));

        polygon.clear();
        polygon.add(new Vector2(0,0));
        polygon.add(new Vector2(1,0));
        polygon.add(new Vector2(2,0));
        polygon.add(new Vector2(2,1));
        polygon.add(new Vector2(2,2));
        polygon.add(new Vector2(2,4));
        polygon.add(new Vector2(1,4));
        polygon.add(new Vector2(0,4));
        MathUtils.polygonRemoveDegenerateVertices(polygon, outVertices);
        Assertions.assertEquals(4, outVertices.size);
        Assertions.assertEquals(new Vector2(0,0), outVertices.get(0));
        Assertions.assertEquals(new Vector2(2,0), outVertices.get(1));
        Assertions.assertEquals(new Vector2(2,4), outVertices.get(2));
        Assertions.assertEquals(new Vector2(0,4), outVertices.get(3));

        polygon.clear();
        polygon.add(new Vector2(0,0));
        polygon.add(new Vector2(1,0));
        polygon.add(new Vector2(2,0));
        polygon.add(new Vector2(2,1));
        polygon.add(new Vector2(2,2));
        polygon.add(new Vector2(2,4));
        polygon.add(new Vector2(1,4));
        polygon.add(new Vector2(0,4));
        polygon.add(new Vector2(0,4));
        polygon.add(new Vector2(0,4));
        MathUtils.polygonRemoveDegenerateVertices(polygon, outVertices);
        Assertions.assertEquals(4, outVertices.size);
        Assertions.assertEquals(new Vector2(0,0), outVertices.get(0));
        Assertions.assertEquals(new Vector2(2,0), outVertices.get(1));
        Assertions.assertEquals(new Vector2(2,4), outVertices.get(2));
        Assertions.assertEquals(new Vector2(0,4), outVertices.get(3));
    }

    @Test
    void polygonRemoveDegenerateVertices_3() {
        ArrayFloat outPolygon = new ArrayFloat();

        float[] polygon_1 = {0,0,   1,0,   1,1,   0.5f,1,   0,1};
        MathUtils.polygonRemoveDegenerateVertices(polygon_1, outPolygon);
        Assertions.assertEquals(8, outPolygon.size);
        Assertions.assertEquals(0, outPolygon.get(0));
        Assertions.assertEquals(0, outPolygon.get(1));
        Assertions.assertEquals(1, outPolygon.get(2));
        Assertions.assertEquals(0, outPolygon.get(3));
        Assertions.assertEquals(1, outPolygon.get(4));
        Assertions.assertEquals(1, outPolygon.get(5));
        Assertions.assertEquals(0, outPolygon.get(6));
        Assertions.assertEquals(1, outPolygon.get(7));

        float[] polygon_2 = {0,0,   1,0,   1,1,   0.5f,1,0.5f,1,0.5f,1,   0,1};
        MathUtils.polygonRemoveDegenerateVertices(polygon_2, outPolygon);
        Assertions.assertEquals(8, outPolygon.size);
        Assertions.assertEquals(0, outPolygon.get(0));
        Assertions.assertEquals(0, outPolygon.get(1));
        Assertions.assertEquals(1, outPolygon.get(2));
        Assertions.assertEquals(0, outPolygon.get(3));
        Assertions.assertEquals(1, outPolygon.get(4));
        Assertions.assertEquals(1, outPolygon.get(5));
        Assertions.assertEquals(0, outPolygon.get(6));
        Assertions.assertEquals(1, outPolygon.get(7));

        float[] polygon_3 = {0,0,   1,0,   2,0,   2,1, 2,2,  2,4,  1,4, 0,4};
        MathUtils.polygonRemoveDegenerateVertices(polygon_3, outPolygon);
        Assertions.assertEquals(8, outPolygon.size);
        Assertions.assertEquals(0, outPolygon.get(0));
        Assertions.assertEquals(0, outPolygon.get(1));
        Assertions.assertEquals(2, outPolygon.get(2));
        Assertions.assertEquals(0, outPolygon.get(3));
        Assertions.assertEquals(2, outPolygon.get(4));
        Assertions.assertEquals(4, outPolygon.get(5));
        Assertions.assertEquals(0, outPolygon.get(6));
        Assertions.assertEquals(4, outPolygon.get(7));

        float[] polygon_4 = {0,0,   1,0,   2,0,   2,1,   2,2,   2,4,   1,4,   0,4,   0,4,   0,4};
        MathUtils.polygonRemoveDegenerateVertices(polygon_4, outPolygon);
        Assertions.assertEquals(8, outPolygon.size);
        Assertions.assertEquals(0, outPolygon.get(0));
        Assertions.assertEquals(0, outPolygon.get(1));
        Assertions.assertEquals(2, outPolygon.get(2));
        Assertions.assertEquals(0, outPolygon.get(3));
        Assertions.assertEquals(2, outPolygon.get(4));
        Assertions.assertEquals(4, outPolygon.get(5));
        Assertions.assertEquals(0, outPolygon.get(6));
        Assertions.assertEquals(4, outPolygon.get(7));
    }

    @Test
    void areCollinear2() {
        Vector2 a = new Vector2();
        Vector2 b = new Vector2();
        Vector2 c = new Vector2();

        a.set(0,0);
        b.set(4,0);
        c.set(4,0);

        a.set(4,0);
        b.set(4,0);
        c.set(4,0);

        Assertions.assertTrue(MathUtils.areCollinear(a,b,c));
    }

    @Test
    void triangulatePolygon_1() {
        Array<Vector2> polygon = new Array<>();
        Array<Vector2> outVertices = new Array<>();
        ArrayInt outIndices = new ArrayInt();

        polygon.clear();
        polygon.add(new Vector2(0,0), new Vector2(1,0), new Vector2(1,1), new Vector2(0,1));
        polygon.reverse();
        MathUtils.polygonTriangulate(polygon, outVertices, outIndices);
    }

    @Test
    void triangulatePolygon_flat() {
        ArrayInt indices = new ArrayInt();
        ArrayFloat vertices = new ArrayFloat();
        float[] poly_1 = new float[] {0,0,  1,0,  1,1,  0,1};
        MathUtils.polygonTriangulate(poly_1, vertices, indices);
    }

    @Test
    void isConvex() {
        float[] vertices_1 = new float[] {0,0,   1,0,   1,1,   0,1};
        Assertions.assertTrue(MathUtils.polygonIsConvex(vertices_1));


        float[] vertices_2 = new float[] {0,0,   1,0,   0.25f,0.25f,   0,1};
        Assertions.assertFalse(MathUtils.polygonIsConvex(vertices_2));

        float[] vertices_3 = new float[] {1,4,   -5,2,  -2,-2, 0,0,  0,1, 2,2};
        Assertions.assertFalse(MathUtils.polygonIsConvex(vertices_3));
    }

}