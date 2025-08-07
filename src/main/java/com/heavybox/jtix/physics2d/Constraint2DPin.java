package com.heavybox.jtix.physics2d;

public class Constraint2DPin extends Constraint2D {

    public Constraint2DPin(final Body2D body) {
        super(body);
    }

    @Override
    void prepare(float delta) {

    }

    @Override
    void solveVelocity(float delta) {

    }

    @Override
    boolean solvePosition(float delta) {
        return true;
    }

}
