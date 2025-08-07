package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;

public class SystemGUI implements System {

    public static final int SYSTEM_BITMASK = Component.Type.REGION.bitmask | Component.Type.RENDER.bitmask;

    private final EntityContainer container;

    private final Array<Entity> entities = new Array<>(false, 10);

    SystemGUI(final EntityContainer container) {
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
        return "System GUI Entities      : " + entities.size;
    }

}
