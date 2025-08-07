package com.heavybox.jtix.ecs;

public interface ComponentTransform extends Component {

    @Override
    default int getBitmask() {
        return Type.TRANSFORM.bitmask;
    }

    float getPositionX();
    float getPositionY();
    float getPositionZ();

    ComponentTransform getWorld();

    boolean isStatic();

}
