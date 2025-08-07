package com.heavybox.jtix.physics3d;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Vector3;

public class World3D {

    public static float DELTA = 1f / 60f;

    public Vector3 gravity = new Vector3();


    private final Array<Body3D> allBodies      = new Array<>(false, 5);
    private final Array<Body3D> bodiesToAdd    = new Array<>(false, 10);
    private final Array<Body3D> bodiesToRemove = new Array<>(false, 10);

    public void update(float delta) {
        /* add and remove bodies */
        {
            for (Body3D body : bodiesToRemove) {
                allBodies.removeValue(body, true);
            }
            for (Body3D body : bodiesToAdd) {
                allBodies.add(body);
            }
            bodiesToRemove.clear();
            bodiesToAdd.clear();
        }

        /* Euler integration */
        for (Body3D body : allBodies) {
            if (body.motionType == Body3D.MotionType.STATIC) continue;

            if (body.motionType == Body3D.MotionType.DYNAMIC) {
                body.netForce.add(gravity);
                body.velocity.x += body.mass_inverse * delta * body.netForce.x;
                body.velocity.y += body.mass_inverse * delta * body.netForce.y;
                body.velocity.z += body.mass_inverse * delta * body.netForce.z;
            }
            body.position.x += delta * body.velocity.x;
            body.position.y += delta * body.velocity.y;
            body.position.y += delta * body.velocity.z;

            body.netForce.set(0,0,0);
        }

    }


}
