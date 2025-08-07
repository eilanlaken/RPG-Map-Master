package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.memory.MemoryPool;

public final class RayCasting2DIntersection implements MemoryPool.Reset {

    public Body2DCollider collider  = null;
    public Vector2      point     = new Vector2();
    public Vector2      direction = new Vector2();
    public float        dst2      = 0;

    public RayCasting2DIntersection() {}

    @Override
    public void reset() {
        collider = null;
    }

}
