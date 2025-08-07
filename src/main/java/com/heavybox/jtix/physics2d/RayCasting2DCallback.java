package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.collections.Array;

public interface RayCasting2DCallback {

    void intersected(final Array<RayCasting2DIntersection> results);

}
