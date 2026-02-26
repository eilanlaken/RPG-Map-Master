package com.heavybox.jtix.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Transform3DTest {

    @Test
    public void transformTest() {
        Vector3 p1 = new Vector3(1,0,0);
        Vector3 p2 = new Vector3(1,0,0);
        Transform3D t1 = new Transform3D();
        t1.position.set(1,1,0);
        t1.scale.set(2,2,2);
        t1.rotation.setFromAxisDeg(0,0,1,45);
        t1.transform(p1);
        Matrix4x4 m1 = new Matrix4x4();
        m1.setToTranslationRotationScaling(t1.position, t1.rotation, t1.scale);
        p2.mul(m1);
        Assertions.assertEquals(p1, p2);
    }

}
