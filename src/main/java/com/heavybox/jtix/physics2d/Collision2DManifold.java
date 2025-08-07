package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;

public final class Collision2DManifold implements MemoryPool.Reset {

    public Body2DCollider collider_a = null;
    public Body2DCollider collider_b = null;
    public float          depth      = 0;
    public Vector2        normal     = new Vector2();
    public int            contacts   = 0;
    public Vector2        contact_a  = new Vector2();
    public Vector2        contact_b  = new Vector2();

    public Collision2DManifold() {}

    @Override
    public void reset() {
        this.contacts = 0;
    }

}
