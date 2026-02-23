package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.Matrix4x4;

@Deprecated
public final class ComponentTransform3D_old extends Matrix4x4 implements ComponentTransform {

    // TODO
    ComponentTransform3D_old parent;
    ComponentTransform3D_old world;

    public final boolean isStatic;

    public ComponentTransform3D_old() {
        super();
        this.isStatic = false;
    }

    public ComponentTransform3D_old(final ComponentTransform3D_old other) {
        super(other);
        this.isStatic = false;
    }

    public ComponentTransform3D_old(boolean isStatic, float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        super();
        setToTranslationEulerScaling(x, y, z, degX, degY, degZ, sclX, sclY, sclZ);
        this.isStatic = false;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public ComponentTransform3D_old getWorld() {
        return world == null ? this : world;
    }

    @Override
    public float getPositionX() {
        return 0;
    }

    @Override
    public float getPositionY() {
        return 0;
    }

    @Override
    public float getPositionZ() {
        return 0;
    }
}
