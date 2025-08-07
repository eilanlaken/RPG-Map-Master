package com.heavybox.jtix.ecs;

public interface Component {

    enum Type {

        AUDIO,
        RENDER,
        CAMERA,
        LOGICS,
        PHYSICS,
        REGION,
        TRANSFORM,
        ;

        public final int bitmask;

        Type() {
            this.bitmask = 0b000001 << ordinal();
        }

    }

    int getBitmask();

}
