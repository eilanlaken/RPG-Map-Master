package com.heavybox.jtix.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Shape2DPolygonTest {

    @Test
    public void getVertexTest() {
        Shape2DPolygon poly = new Shape2DPolygon(
                0f, 0f,
                2f, 0f,
                2f, 2f
        );

        Vector2 out_0 = new Vector2();
        poly.getVertex(0, out_0);
        Assertions.assertEquals(0f, out_0.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, out_0.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Vector2 out_1 = new Vector2();
        poly.getVertex(1, out_1);
        Assertions.assertEquals(2f, out_1.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, out_1.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Vector2 out_2 = new Vector2();
        poly.getVertex(2, out_2);
        Assertions.assertEquals(2f, out_2.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2f, out_2.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Assertions.assertThrows(MathException.class, () -> poly.getVertex(3, new Vector2()));
    }

    @Test
    public void getEdgeTest() {
        Shape2DPolygon poly = new Shape2DPolygon(
                0f, 0f,
                2f, 0f,
                2f, 2f
        );

        Vector2 edge0_Tail = new Vector2();
        Vector2 edge0_Head = new Vector2();
        poly.getEdge(0, edge0_Tail, edge0_Head);
        Assertions.assertEquals(0f, edge0_Tail.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, edge0_Tail.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2f, edge0_Head.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, edge0_Head.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Vector2 edge1_Tail = new Vector2();
        Vector2 edge1_Head = new Vector2();
        poly.getEdge(1, edge1_Tail, edge1_Head);
        Assertions.assertEquals(2f, edge1_Tail.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, edge1_Tail.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2f, edge1_Head.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2f, edge1_Head.y, MathUtils.FLOAT_ROUNDING_ERROR);

        Vector2 edge2_Tail = new Vector2();
        Vector2 edge2_Head = new Vector2();
        poly.getEdge(2, edge2_Tail, edge2_Head);
        Assertions.assertEquals(2f, edge2_Tail.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(2f, edge2_Tail.y, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, edge2_Head.x, MathUtils.FLOAT_ROUNDING_ERROR);
        Assertions.assertEquals(0f, edge2_Head.y, MathUtils.FLOAT_ROUNDING_ERROR);
    }

}
