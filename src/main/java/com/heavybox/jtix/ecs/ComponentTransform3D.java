package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.Matrix4x4;

public final class ComponentTransform3D extends Matrix4x4 implements ComponentTransform {

    // TODO
    ComponentTransform3D parent;
    ComponentTransform3D world;

    public final boolean isStatic;

    public ComponentTransform3D() {
        super();
        this.isStatic = false;
    }

    public ComponentTransform3D(final ComponentTransform3D other) {
        super(other);
        this.isStatic = false;
    }

    public ComponentTransform3D(boolean isStatic, float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        super();
        setToTranslationEulerScaling(x, y, z, degX, degY, degZ, sclX, sclY, sclZ);
        this.isStatic = false;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public ComponentTransform3D getWorld() {
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
