package com.heavybox.jtix.assets;

import com.heavybox.jtix.memory.MemoryResource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class AssetDescriptor {

    public final Class<? extends MemoryResource> type;
    public final String                          path;
    public final long                            size;
    public final HashMap<String, Object>         options;

    public AssetDescriptor(Class<? extends MemoryResource> type, String filepath, @Nullable final HashMap<String, Object> options) {
        this.type = type;
        this.path = filepath;
        long s = 0;
        try {
            s = Assets.getFileSize(filepath);
        } catch (IOException e) {
            // TODO: see how to get the total file size for assets that are composed of multiple files.
            //throw new AssetsException(e.getMessage());
        }
        this.size = s;
        this.options = options;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof AssetDescriptor)) return false;
        if (this == obj) return true;
        AssetDescriptor otherDescriptor = (AssetDescriptor) obj;
        return Objects.equals(this.path, otherDescriptor.path) && this.type == otherDescriptor.type;
    }

}
