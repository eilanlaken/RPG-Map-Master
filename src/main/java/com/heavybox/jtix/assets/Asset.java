package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.memory.MemoryResource;

class Asset implements MemoryResource {

    public       int             refCount;
    public final MemoryResource  data;
    public final AssetDescriptor descriptor;
    public final Array<Asset>    dependencies;

    Asset(final MemoryResource data, final AssetDescriptor descriptor, Array<Asset> dependencies) {
        this.refCount = 1;
        this.data = data;
        this.descriptor = descriptor;
        this.dependencies = dependencies;
    }

    @Override
    public void delete() {
        for (Asset dependency : dependencies) dependency.delete();
        refCount--;
        if (refCount <= 0) data.delete();
    }

}
