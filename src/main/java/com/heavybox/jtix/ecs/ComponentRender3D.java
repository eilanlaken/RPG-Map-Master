package com.heavybox.jtix.ecs;

import com.heavybox.jtix.graphics.Renderer3D;
import com.heavybox.jtix.math.Matrix4x4;

public abstract class ComponentRender3D implements ComponentRender {

    // TODO: but renderer3d is static.
    public abstract void render(Renderer3D renderer3D, final Matrix4x4 transform);

}
