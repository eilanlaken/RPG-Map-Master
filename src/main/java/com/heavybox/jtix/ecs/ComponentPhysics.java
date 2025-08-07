package com.heavybox.jtix.ecs;

public interface ComponentPhysics extends Component {

    @Override
    default int getBitmask() {
        return Type.PHYSICS.bitmask;
    }

}
