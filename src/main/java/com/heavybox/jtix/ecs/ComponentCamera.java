package com.heavybox.jtix.ecs;

public interface ComponentCamera extends Component {

    @Override
    default int getBitmask() {
        return Type.CAMERA.bitmask;
    }

}
