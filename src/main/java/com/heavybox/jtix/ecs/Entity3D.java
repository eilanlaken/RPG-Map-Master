package com.heavybox.jtix.ecs;

import org.jetbrains.annotations.NotNull;

public abstract class Entity3D extends Entity {

    public final ComponentTransform3D transform;

    protected Entity3D() {
        this(false,0,0,0,0,0,0,1,1,1);
    }

    protected Entity3D(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        this(false, x, y, z, degX, degY, degZ, sclX, sclY, sclZ);
    }

    protected Entity3D(boolean isStatic, float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        this.transform = new ComponentTransform3D(isStatic, x, y, z, degX, degY, degZ, sclX, sclY, sclZ);
    }

    @Override protected          ComponentTransform3D createComponentTransform() { return transform; }
    @Override protected abstract ComponentAudio       createComponentAudio();
    @Override protected abstract ComponentRender3D    createComponentRender();
    @Override protected abstract ComponentCamera3D    createComponentCamera();
    @Override protected abstract ComponentPhysics3D   createComponentPhysics();
    @Override protected abstract ComponentLogics      createComponentLogics();
    @Override protected abstract ComponentRegion      createComponentRegion();

    @Override public final ComponentTransform3D getComponentTransform() {
        return transform;
    }

    public @NotNull EntityLayer3D getLayer() {
        return EntityLayer3D.DEFAULT;
    }

}
