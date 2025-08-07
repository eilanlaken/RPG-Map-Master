package com.heavybox.jtix.ecs;

public interface ComponentPhysics2D extends ComponentPhysics {

    @Override
    default int getBitmask() {
        return Type.PHYSICS.bitmask;
    }

}
