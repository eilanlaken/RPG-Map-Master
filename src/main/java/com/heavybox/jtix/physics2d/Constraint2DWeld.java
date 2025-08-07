package com.heavybox.jtix.physics2d;

// TODO
public class Constraint2DWeld extends Constraint2D {

    public Constraint2DWeld(Body2D a, Body2D b) {
        super(a, b);
    }

    @Override
    void prepare(float delta) {

    }

    @Override
    void solveVelocity(float delta) {

    }

    @Override
    boolean solvePosition(float delta) {
        return false;
    }

}
