package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.collections.Array;

public abstract class Constraint2D {

    public final Body2D body1;
    public final Body2D body2;

    Constraint2D(Body2D body1, Body2D body2) {
        if (body1 == null)  throw new Physics2DException("Constraint must have at least 1 body.");
        if (body1 == body2) throw new Physics2DException("body_a cannot be equal to body_b");
        this.body1 = body1;
        this.body2 = body2;
    }

    Constraint2D(Body2D body1) {
        if (body1 == null) throw new Physics2DException("Constraint must have at least 1 body.");
        this.body1 = body1;
        this.body2 = null;
    }

    public final void getBodies(Array<Body2D> out) {
        out.clear();
        out.add(body1);
        if (body2 != null) out.add(body2);
    }

    abstract void    prepare(float delta);
    abstract void    solveVelocity(float delta);
    abstract boolean solvePosition(float delta);

}
