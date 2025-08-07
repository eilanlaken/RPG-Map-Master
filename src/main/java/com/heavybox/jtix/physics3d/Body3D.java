package com.heavybox.jtix.physics3d;

import com.heavybox.jtix.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class Body3D {

    public final MotionType motionType;
    public final float      mass_inverse;

    protected final Vector3 position;
    protected final Vector3 velocity;
    protected final Vector3 netForce;

    // always start with an all args constructor in mind
    public Body3D(@NotNull MotionType motionType, float mass,
                  float x, float y, float z,
                  float vx, float vy, float vz) {
        this.motionType = motionType;
        this.mass_inverse = 1f / mass;
        this.position = new Vector3(x,y,z);
        this.velocity = new Vector3(vx,vy,vz);
        this.netForce = new Vector3(0,0,0);
    }

    public void applyForce(float fx, float fy, float fz) {
        netForce.add(fx, fy, fz);
    }

    public enum MotionType {
        STATIC,
        DYNAMIC,
        KINEMATIC,
    }

}
