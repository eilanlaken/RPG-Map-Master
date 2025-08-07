package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Font;

import java.nio.ByteBuffer;
import java.util.HashMap;

// TODO: here is the solution: check if suffix is .ttf or .yaml or yml.
// If ttf generate bitmap, then load the bitmap font
// if bitmap, load the bitmap font.
public class AssetLoaderFont implements AssetLoader<Font> {

    private ByteBuffer fontDataBuffer;

    @Override
    public void beforeLoad(String path, HashMap<String, Object> options) {
        if (!Assets.fileExists(path)) throw new AssetsException("File does not exist: " + path); // trying to load a bitmap font that does not exist
    }

    @Override
    public Array<AssetDescriptor> load(final String path, final HashMap<String, Object> options) {
        try {
            fontDataBuffer = Assets.fileToByteBuffer(path);
        } catch (Exception e) {
            throw new AssetsException("Could not read " + path + " into ByteBuffer. Exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Font afterLoad() {
        return new Font(fontDataBuffer);
    }

}
