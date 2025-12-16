package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;

public final class Tools {

    private Tools() {}

    public static Array<Tool> tools = new Array<>(true, 10);
    public static int activeToolIndex = 0;

    static void initTools() {

    }

}
