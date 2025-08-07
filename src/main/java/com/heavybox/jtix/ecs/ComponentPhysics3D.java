package com.heavybox.jtix.ecs;

public interface ComponentPhysics3D extends ComponentPhysics {

    @Override
    default int getBitmask() {
        return Type.PHYSICS.bitmask;
    }

}
