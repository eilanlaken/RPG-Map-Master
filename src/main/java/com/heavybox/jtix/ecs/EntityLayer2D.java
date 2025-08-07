package com.heavybox.jtix.ecs;

public enum EntityLayer2D {

    DEFAULT,
    LAYER_1,
    LAYER_2,
    LAYER_3,
    LAYER_4,
    LAYER_5,
    LAYER_6,
    LAYER_7,
    LAYER_8,
    LAYER_9,
    LAYER_10,
    LAYER_11,
    LAYER_12,
    LAYER_13,
    LAYER_14,
    LAYER_15,
    LAYER_16,
    LAYER_17,
    LAYER_18,
    LAYER_19,
    LAYER_20,
    LAYER_21,
    LAYER_22,
    LAYER_23,
    LAYER_24,
    LAYER_25,
    LAYER_26,
    LAYER_27,
    LAYER_28,
    LAYER_29,
    LAYER_30,
    LAYER_31,
    ;

    public final int bitmask;

    EntityLayer2D() {
        this.bitmask = 0b000001 << ordinal();
    }

}
