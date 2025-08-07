package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.math.MathUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Physics2DTest {

    @Test
    void calculateMomentOfInertia() {
        Body2DColliderPolygon poly = new Body2DColliderPolygon(1,0,0,0,false,0, new float[] {0,1, 1,1, 1,0, 0,0});
        Body2DColliderRectangle rect = new Body2DColliderRectangle(1,0,0,0,false,0,1,1,0,0,0);

        float polyInertia = Physics2D.calculateMomentOfInertia(poly);
        float rectInertia = Physics2D.calculateMomentOfInertia(rect);
        Assertions.assertEquals(polyInertia, rectInertia, MathUtils.FLOAT_ROUNDING_ERROR);
    }

}