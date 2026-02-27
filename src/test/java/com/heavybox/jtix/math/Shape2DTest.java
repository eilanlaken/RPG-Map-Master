package com.heavybox.jtix.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Shape2DTest {

    @Test
    public void containsPoint_circle() {
        Shape2DCircle c = new Shape2DCircle(5f);
        Assertions.assertTrue(c.containsPoint(0f, 0f));
        Assertions.assertTrue(c.containsPoint(3f, 4f)); // distance = 5
        Assertions.assertTrue(c.containsPoint(2f, 1f));
        Assertions.assertFalse(c.containsPoint(6f, 0f));
        Assertions.assertFalse(c.containsPoint(0f, -6f));
        Assertions.assertFalse(c.containsPoint(4f, 4f)); // > 5

        Transform2D t1 = new Transform2D();
        t1.x = 10f;
        t1.y = -5f;
        t1.deg = 0f;
        t1.sclX = 1f;
        t1.sclY = 1f;
        Assertions.assertTrue(c.containsPoint(10f, -5f, t1));  // center
        Assertions.assertTrue(c.containsPoint(13f, -1f, t1));  // inside
        Assertions.assertFalse(c.containsPoint(20f, -5f, t1)); // outside

        Transform2D t2 = new Transform2D();
        t2.x = 0f;
        t2.y = 0f;
        t2.deg = 0f;
        t2.sclX = 2f;
        t2.sclY = 2f;
        Assertions.assertTrue(c.containsPoint(9f, 0f, t2));
        Assertions.assertFalse(c.containsPoint(11f, 0f, t2));
    }

    @Test
    void containsPoint_polygon() {
        // polygons
        Shape2DPolygon p1 = new Shape2DPolygon(
                0f, 0f,
                2f, 0f,
                2f, 2f,
                0f, 2f
        ); // square

        Shape2DPolygon p2 = new Shape2DPolygon(
                0f, 0f,
                4f, 0f,
                4f, 2f,
                2f, 1f,
                0f, 2f
        ); // concave

        Shape2DPolygon p3 = new Shape2DPolygon(
                -1f, -1f,
                1f, -1f,
                1f, 1f,
                -1f, 1f
        ); // centered at origin

        // transforms
        Transform2D t1 = new Transform2D();
        t1.x = 1f; t1.y = 1f; t1.deg = 0f; t1.sclX = 1f; t1.sclY = 1f;

        Transform2D t2 = new Transform2D();
        t2.x = 0f; t2.y = 0f; t2.deg = 90f; t2.sclX = 1f; t2.sclY = 1f;

        Transform2D t3 = new Transform2D();
        t3.x = -2f; t3.y = -2f; t3.deg = 0f; t3.sclX = 2f; t3.sclY = 2f;

        // p1 tests (points strictly inside)
        Assertions.assertTrue(p1.containsPoint(1f, 1f));
        Assertions.assertTrue(p1.containsPoint(0.5f, 0.5f));
        Assertions.assertFalse(p1.containsPoint(2.1f, 1f));
        Assertions.assertFalse(p1.containsPoint(-0.1f, 1f));

        // p1 with transform t1 (translate +1,+1)
        Assertions.assertTrue(p1.containsPoint(2f, 2f, t1));
        Assertions.assertTrue(p1.containsPoint(1.5f, 1.5f, t1));
        Assertions.assertFalse(p1.containsPoint(3f, 2f, t1));
        Assertions.assertFalse(p1.containsPoint(0f, 1f, t1));

        // p2 tests
        Assertions.assertTrue(p2.containsPoint(1f, 1f));
        Assertions.assertTrue(p2.containsPoint(1f, 0.5f));
        Assertions.assertTrue(p2.containsPoint(3.5f, 1.4f));
        Assertions.assertFalse(p2.containsPoint(4.1f, 1f));

        // p2 with transform t2 (rotate 90°)
        Assertions.assertTrue(p2.containsPoint(-1f, 1f, t2));
        Assertions.assertTrue(p2.containsPoint(-0.5f, 1f, t2));
        Assertions.assertFalse(p2.containsPoint(1f, 1f, t2));
        Assertions.assertFalse(p2.containsPoint(-2f, 0f, t2));

        // p3 tests
        Assertions.assertTrue(p3.containsPoint(0f, 0f));
        Assertions.assertTrue(p3.containsPoint(0.5f, 0.5f));
        Assertions.assertFalse(p3.containsPoint(1.1f, 0f));
        Assertions.assertFalse(p3.containsPoint(-1.1f, 0f));

        // p3 with transform t3 (translate -2,-2, scale 2)
        Assertions.assertTrue(p3.containsPoint(-2f, -2f, t3));
        Assertions.assertTrue(p3.containsPoint(-1.5f, -1.5f, t3));
        Assertions.assertFalse(p3.containsPoint(0.1f, 0f, t3));
        Assertions.assertFalse(p3.containsPoint(-4.1f, -2f, t3));
    }

}
