package com.heavybox.jtix.ecs;

import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Transform3D;

public class ComponentTransform3D implements ComponentTransform {

    private Transform3D transform;
    private boolean dirty = true; // after every matrix update, dirty is reset to false. After every transform change, dirty is set to true.
    private Matrix4x4 world;

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

    @Override
    public ComponentTransform getWorld() {
        return null;
    }

    @Override
    public boolean isStatic() {
        return false;
    }
}
