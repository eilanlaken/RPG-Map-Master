package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Texture;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

public class AssetLoaderTexture implements AssetLoader<Texture> {

    private int        width;
    private int        height;
    private ByteBuffer buffer;
    private HashMap<String, Object> options;

    @Override
    public void beforeLoad(String path, HashMap<String, Object> options) {
        if (!Assets.fileExists(path)) throw new AssetsException("File does not exist: " + path);
    }

    @Override
    public Array<AssetDescriptor> load(String path, final HashMap<String, Object> options) {
        this.options = options;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);
            buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (buffer == null) throw new AssetsException("Failed to load a texture file. Check that the path is correct: " + path
                    + System.lineSeparator() + "STBImage error: "
                    + STBImage.stbi_failure_reason());
            width = widthBuffer.get();
            height = heightBuffer.get();
        }
        return null;
    }

    @Override
    public Texture afterLoad() {
        final int anisotropy = options == null || options.get("anisotropy") == null ? Graphics.getMaxAnisotropy() : (int) options.get("anisotropy");
        final Texture.FilterMag magFilter = options == null || options.get("magFilter") == null ? null : (Texture.FilterMag) options.get("magFilter");
        final Texture.FilterMin minFilter = options == null || options.get("minFilter") == null ? null : (Texture.FilterMin) options.get("minFilter");
        final Texture.Wrap uWrap = options == null || options.get("uWrap") == null ? null : (Texture.Wrap) options.get("uWrap");
        final Texture.Wrap vWrap = options == null || options.get("vWrap") == null ? null : (Texture.Wrap) options.get("vWrap");
        Texture texture = new Texture(width, height, buffer, magFilter, minFilter, uWrap, vWrap, anisotropy); // TODO: query for format options and use the all args texture constructor.
        STBImage.stbi_image_free(buffer);
        return texture;
    }

}