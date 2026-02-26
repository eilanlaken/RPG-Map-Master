package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

public interface Texture extends MemoryResource {

    void setSlot  (final int slot);
    int  getSlot  ();
    int  getHandle();

}
