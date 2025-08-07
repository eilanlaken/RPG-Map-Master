package com.heavybox.jtix.ecs;

public interface ComponentRender extends Component {

    @Override
    default int getBitmask() {
        return Type.RENDER.bitmask;
    }

}
