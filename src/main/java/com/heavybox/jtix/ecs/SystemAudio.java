package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;

public class SystemAudio implements System {

    public static final int SYSTEM_BITMASK = Component.Type.AUDIO.bitmask;

    private final EntityContainer container;
    private final Array<Entity> entities = new Array<>(false, 10);

    SystemAudio(final EntityContainer container) {
        this.container = container;
    }

    @Override
    public boolean shouldProcess(Entity entity) {
        return (entity.bitmask & SYSTEM_BITMASK) > 0;
    }

    @Override
    public void add(Entity entity) {

    }

    @Override
    public void remove(Entity entity) {

    }

    @Override
    public void frameUpdate(float delta) {

    }

    @Override
    public void fixedUpdate(float delta) {

    }

    @Override
    public String toString() {
        return "System Audio Entities    : " + entities.size;
    }

}
