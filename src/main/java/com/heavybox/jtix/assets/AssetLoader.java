package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.memory.MemoryResource;

import java.util.HashMap;

interface AssetLoader<T extends MemoryResource> {

    // TODO: make use of the before load.
    default void beforeLoad(final String path, final HashMap<String, Object> options) {
        if (!Assets.fileExists(path)) throw new AssetsException("File does not exist: " + path);
    }

    Array<AssetDescriptor> load(final String path, final HashMap<String, Object> options);
    T afterLoad();

}
