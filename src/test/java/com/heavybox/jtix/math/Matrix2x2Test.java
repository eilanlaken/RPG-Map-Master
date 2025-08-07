package com.heavybox.jtix.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.heavybox.jtix.math.Matrix2x2.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Matrix2x2Test {

    @Test
    void idt() {
        Matrix2x2 idt = new Matrix2x2();
        assertEquals(1, idt.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, idt.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, idt.val[M10], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(1, idt.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 idt2 = new Matrix2x2(new float[] {1,2,3,4});
        idt2.idt();
        assertEquals(1, idt2.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, idt2.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, idt2.val[M10], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(1, idt2.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void mul() {
        Matrix2x2 idt = new Matrix2x2();
        Vector2 a = new Vector2(1,2);
        Vector2 a1 = idt.mul(a);
        assertEquals(1, a1.x, MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(2, a1.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m1 = new Matrix2x2();
        m1.set(new float[] {0,0,0,0});
        Vector2 a2 = m1.mul(a);
        assertEquals(0, a2.x, MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, a2.x, MathUtils.FLOAT_ROUNDING_ERROR);

        Vector2 b = new Vector2(1,1);
        Matrix2x2 m2 = new Matrix2x2(new float[] {1, -1, 2, 4});
        Vector2 m2xb = m2.mul(b);
        assertEquals(3, m2xb.x, MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(3, m2xb.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m3 = new Matrix2x2(new float[] {2,0,0,2});
        Vector2 m3xb = m3.mul(b);
        assertEquals(2, m3xb.x, MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(2, m3xb.y, MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void testMul() {
        Matrix2x2 m1 = new Matrix2x2(new float[] {1,3,-4,-1});
        Matrix2x2 m2 = new Matrix2x2(new float[] {1,3,-4,-1});
        Matrix2x2 m1xm2 = m1.mul(m2);

        assertEquals(-11, m1xm2.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, m1xm2.val[M10],   MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, m1xm2.val[M01],   MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(-11, m1xm2.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m3 = new Matrix2x2(new float[] {-2, 0, 1, 5});
        Matrix2x2 m4 = new Matrix2x2(new float[] {-9, 8, 7, 1});
        Matrix2x2 m3xm4 = m3.mul(m4);
        assertEquals(26, m3xm4.val[M00],  MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(40, m3xm4.val[M10],  MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(-13, m3xm4.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(5, m3xm4.val[M11],   MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void setToRotationDeg() {
        Matrix2x2 rot1 = new Matrix2x2();
        rot1.setToRotationDeg(0);
        assertEquals(1, rot1.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, rot1.val[M10], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, rot1.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(1, rot1.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 rot2 = new Matrix2x2();
        rot2.setToRotationDeg(90);
        assertEquals(0,  rot2.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(1,  rot2.val[M10], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(-1, rot2.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0,  rot2.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void setToScaling() {
        Matrix2x2 s1 = new Matrix2x2();
        s1.set(new float[] {4,5,6,7}); // should be ignored when setToScaling
        s1.setToScaling(4, 2);
        assertEquals(4, s1.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, s1.val[M10], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, s1.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(2, s1.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void det() {
        Matrix2x2 m1 = new Matrix2x2(new float[] {4,5,6,7});
        assertEquals(-2, m1.det(),   MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m2 = new Matrix2x2();
        assertEquals(1, m2.det(),    MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m3 = new Matrix2x2(new float[] {4,5,0,0});
        assertEquals(0, m3.det(),    MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m4 = new Matrix2x2().setToScaling(2,2);
        assertEquals(4.0f, m4.det(), MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void inv() {
        Matrix2x2 m1 = new Matrix2x2(new float[] {4,5,6,7});
        m1.inv();
        assertEquals(-7f/2, m1.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(5f/2, m1.val[M10],  MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(3,   m1.val[M01],   MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(-2, m1.val[M11],    MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m2 = new Matrix2x2(new float[] {0,0,0,0});
        Assertions.assertThrows(MathException.class, m2::inv);
    }

    @Test
    void transpose() {
        Matrix2x2 m1 = new Matrix2x2(new float[] {4,5,6,7});
        m1.transpose();
        assertEquals(4, m1.val[M00], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(6, m1.val[M10], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(5, m1.val[M01], MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(7, m1.val[M11], MathUtils.FLOAT_ROUNDING_ERROR);
    }

    @Test
    void solve2x2() {
        Matrix2x2 I = new Matrix2x2();
        Vector2 b = new Vector2(3,6);
        Vector2 solution1 = new Vector2();
        boolean solved1 = Matrix2x2.solve22(I, b, solution1);
        Assertions.assertTrue(solved1);
        assertEquals(3, solution1.x, MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(6, solution1.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 zero = new Matrix2x2(new float[] {0,0,0,0});
        Vector2 solution2 = new Vector2();
        boolean solved2 = Matrix2x2.solve22(zero, b, solution2);
        Assertions.assertFalse(solved2);
        assertEquals(0, solution2.x, MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(0, solution2.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Matrix2x2 m1 = new Matrix2x2(new float[] {3,7,12,-8});
        Vector2 b1 = new Vector2(-4, 3);
        Vector2 solution3 = new Vector2();
        boolean solved3 = Matrix2x2.solve22(m1, b1, solution3);
        Assertions.assertTrue(solved3);
        assertEquals(1f / 27, solution3.x, MathUtils.FLOAT_ROUNDING_ERROR);
        assertEquals(-37f / 108, solution3.y, MathUtils.FLOAT_ROUNDING_ERROR);
    }

}