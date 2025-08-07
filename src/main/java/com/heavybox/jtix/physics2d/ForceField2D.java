package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.math.Vector2;

public abstract class ForceField2D {

    protected final World2D world;

    protected ForceField2D(World2D world) {
        this.world = world;
    }

    public abstract void calculateForce(Body2D body, Vector2 out);

}
