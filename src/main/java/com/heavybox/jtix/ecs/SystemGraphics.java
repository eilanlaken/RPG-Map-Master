package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;

public class SystemGraphics implements System {

    public static final int SYSTEM_BITMASK = Component.Type.RENDER.bitmask | Component.Type.CAMERA.bitmask;

    private final EntityContainer container;

    private final Array<Entity2D> renders_2d = new Array<>(false, 10);
    private final Array<Entity2D> cameras_2d = new Array<>(false, 10);
    private final Array<Entity3D> renders_3d = new Array<>(false, 10);
    private final Array<Entity3D> cameras_3d = new Array<>(false, 10);

    public final Color backgroundColor = new Color(0,0,0,0);

    SystemGraphics(final EntityContainer container) {
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
        return "System Rendering Entities: " + (renders_2d.size + cameras_2d.size + renders_3d.size + cameras_3d.size);
    }

    /* System API */

    public void setBackgroundColor(final Color color) {
        this.backgroundColor.set(color);
    }

}
