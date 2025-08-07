package com.heavybox.jtix.graphics;

import com.heavybox.jtix.assets.AssetsException;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.memory.MemoryResource;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

// TODO: refactor into a base class and a specific class: Texture + Texture2D
// TODO: unify texture constructors.
public class Texture implements MemoryResource {

    private       int       handle;
    private       int       slot;
    public  final int       width;
    public  final int       height;
    public  final float     invWidth;
    public  final float     invHeight;
    public  final FilterMag filterMag;
    public  final FilterMin filterMin;
    public  final Wrap      sWrap;
    public  final Wrap      tWrap;
    private       int       anisotropy;
    private       float     biasLOD; // higher LOD bias will sample from higher mip level, which means lower texture quality.

    @Nullable private ByteBuffer pixmapBytes = null;

    // create an empty texture. Mainly for use of a frame buffer.
    public Texture(int width, int height) {
        this.handle = GL11.glGenTextures();
        this.slot = -1;

        int maxTextureSize = Graphics.getMaxTextureSize();
        if (width > maxTextureSize || height > maxTextureSize)
            throw new GraphicsException("Trying to create " + Texture.class + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);

        this.width = width;
        this.height = height;
        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;

        this.filterMag = FilterMag.LINEAR;
        this.filterMin = FilterMin.LINEAR;
        this.sWrap = Texture.Wrap.CLAMP_TO_EDGE;
        this.tWrap = Texture.Wrap.CLAMP_TO_EDGE;
        this.anisotropy = 1;
        this.biasLOD = 0;

        TextureBinder.bind(this);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0);
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
    }

    public Texture(int width, int height, int internalFormat, int format) {
        this.handle = GL11.glGenTextures();
        this.slot = -1;

        int maxTextureSize = Graphics.getMaxTextureSize();
        if (width > maxTextureSize || height > maxTextureSize)
            throw new GraphicsException("Trying to create " + Texture.class + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);

        this.width = width;
        this.height = height;
        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;

        this.filterMag = FilterMag.LINEAR;
        this.filterMin = FilterMin.LINEAR;
        this.sWrap = Texture.Wrap.CLAMP_TO_EDGE;
        this.tWrap = Texture.Wrap.CLAMP_TO_EDGE;
        this.anisotropy = 1;
        this.biasLOD = 0;

        TextureBinder.bind(this);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, 0);
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
    }

    public Texture(int width, int height, ByteBuffer bytes, FilterMag filterMag, FilterMin filterMin, Wrap sWrap, Wrap tWrap, int anisotropy, boolean useAlpha) {
        this.handle = GL11.glGenTextures();
        this.slot = -1;

        int maxTextureSize = Graphics.getMaxTextureSize();
        if (width > maxTextureSize || height > maxTextureSize)
            throw new IllegalStateException("Trying to create " + Texture.class + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);

        this.width = width;
        this.height = height;
        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;

        this.filterMag = filterMag != null ? filterMag : FilterMag.NEAREST;
        this.filterMin = filterMin != null ? filterMin : FilterMin.NEAREST_MIPMAP_NEAREST;
        this.sWrap = sWrap != null ? sWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.tWrap = tWrap != null ? tWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.anisotropy = MathUtils.nextPowerOf2i(MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy()));
        this.biasLOD = 0;

        TextureBinder.bind(this);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        if (useAlpha) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bytes);
        } else {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, bytes);
        }
        if (this.filterMin == FilterMin.NEAREST_MIPMAP_LINEAR ||
                this.filterMin == FilterMin.LINEAR_MIPMAP_LINEAR  ||
                this.filterMin == FilterMin.LINEAR_MIPMAP_NEAREST ||
                this.filterMin == FilterMin.NEAREST_MIPMAP_NEAREST) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            this.anisotropy = MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy());
            if (Graphics.isAnisotropicFilteringSupported()) GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.anisotropy);
        } else {
            this.anisotropy = 1;
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
        }
    }

    public Texture(int width, int height, ByteBuffer bytes, FilterMag filterMag, FilterMin filterMin, Wrap sWrap, Wrap tWrap, int anisotropy) {
        this.handle = GL11.glGenTextures();
        this.slot = -1;

        int maxTextureSize = Graphics.getMaxTextureSize();
        if (width > maxTextureSize || height > maxTextureSize)
            throw new IllegalStateException("Trying to create " + Texture.class + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);

        this.width = width;
        this.height = height;
        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;

        this.filterMag = filterMag != null ? filterMag : FilterMag.NEAREST;
        this.filterMin = filterMin != null ? filterMin : FilterMin.NEAREST_MIPMAP_NEAREST;
        this.sWrap = sWrap != null ? sWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.tWrap = tWrap != null ? tWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.anisotropy = MathUtils.nextPowerOf2i(MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy()));
        this.biasLOD = 0;

        TextureBinder.bind(this);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bytes);
        //GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_R16, width, height, 0, GL11.GL_RED, GL11.GL_UNSIGNED_SHORT, bytes); // TODO: support this too.
        if (this.filterMin == FilterMin.NEAREST_MIPMAP_LINEAR ||
            this.filterMin == FilterMin.LINEAR_MIPMAP_LINEAR  ||
            this.filterMin == FilterMin.LINEAR_MIPMAP_NEAREST ||
            this.filterMin == FilterMin.NEAREST_MIPMAP_NEAREST) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            this.anisotropy = MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy());
            if (Graphics.isAnisotropicFilteringSupported()) GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.anisotropy);
        } else {
            this.anisotropy = 1;
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
        }
    }

    public Texture(final String filepath) {
        this.handle = GL11.glGenTextures();
        this.slot = -1;

        ByteBuffer buffer;
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);
        buffer = STBImage.stbi_load(filepath, widthBuffer, heightBuffer, channelsBuffer, 4);
        if (buffer == null) throw new AssetsException("Failed to load a texture file. Check that the path is correct: " + filepath
                + System.lineSeparator() + "STBImage error: "
                + STBImage.stbi_failure_reason());
        width = widthBuffer.get();
        height = heightBuffer.get();
        int maxTextureSize = Graphics.getMaxTextureSize();
        if (width > maxTextureSize || height > maxTextureSize) throw new AssetsException("Trying to load texture " + filepath + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);

        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;

        // defaults
        this.filterMag = FilterMag.NEAREST;
        this.filterMin = FilterMin.NEAREST_MIPMAP_NEAREST;
        this.sWrap = Texture.Wrap.CLAMP_TO_EDGE;
        this.tWrap = Texture.Wrap.CLAMP_TO_EDGE;
        this.anisotropy = MathUtils.nextPowerOf2i(MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy()));
        this.biasLOD = 0;

        TextureBinder.bind(this);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        this.anisotropy = MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy());
        if (Graphics.isAnisotropicFilteringSupported()) GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.anisotropy);
        STBImage.stbi_image_free(buffer);
    }

    // TODO: test
    // TODO: use this all args constructor.
    public Texture(int width, int height, ByteBuffer bytes, FilterMag filterMag, FilterMin filterMin, Wrap sWrap, Wrap tWrap, int anisotropy, int internalFormat, int externalFormat) {
        this.handle = GL11.glGenTextures();
        this.slot = -1;

        int maxTextureSize = Graphics.getMaxTextureSize();
        if (width > maxTextureSize || height > maxTextureSize)
            throw new IllegalStateException("Trying to create " + Texture.class + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);

        this.width = width;
        this.height = height;
        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;

        this.filterMag = filterMag != null ? filterMag : FilterMag.NEAREST;
        this.filterMin = filterMin != null ? filterMin : FilterMin.NEAREST_MIPMAP_NEAREST;
        this.sWrap = sWrap != null ? sWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.tWrap = tWrap != null ? tWrap : Texture.Wrap.CLAMP_TO_EDGE;
        this.anisotropy = MathUtils.nextPowerOf2i(MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy()));
        this.biasLOD = 0;

        TextureBinder.bind(this);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, width, height, 0, externalFormat, GL11.GL_UNSIGNED_BYTE, bytes);
        if (this.filterMin == FilterMin.NEAREST_MIPMAP_LINEAR ||
                this.filterMin == FilterMin.LINEAR_MIPMAP_LINEAR  ||
                this.filterMin == FilterMin.LINEAR_MIPMAP_NEAREST ||
                this.filterMin == FilterMin.NEAREST_MIPMAP_NEAREST) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            this.anisotropy = MathUtils.clampInt(anisotropy,1, Graphics.getMaxAnisotropy());
            if (Graphics.isAnisotropicFilteringSupported()) GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.anisotropy);
        } else {
            this.anisotropy = 1;
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
            GL11.glTexParameteri(GL20.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
        }
    }

    // 16 bit images
    // TODO: placeholder for now.
    public Texture(final String filepath, Precision precision, boolean generateMipMaps) {
        this.handle = GL11.glGenTextures();
        this.slot = -1;

        ShortBuffer buffer;
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);
        buffer = STBImage.stbi_load_16(filepath, widthBuffer, heightBuffer, channelsBuffer, 1);
        if (buffer == null) throw new AssetsException("Failed to load a texture file. Check that the path is correct: " + filepath
                + System.lineSeparator() + "STBImage error: "
                + STBImage.stbi_failure_reason());
        width = widthBuffer.get();
        height = heightBuffer.get();
        int maxTextureSize = Graphics.getMaxTextureSize();
        if (width > maxTextureSize || height > maxTextureSize) throw new AssetsException("Trying to load texture " + filepath + " with resolution (" + width + "," + height + ") greater than allowed on your GPU: " + maxTextureSize);

        this.invWidth = 1.0f / width;
        this.invHeight = 1.0f / height;

        // defaults
        this.filterMag = FilterMag.NEAREST;
        this.filterMin = FilterMin.NEAREST;
        this.sWrap = Texture.Wrap.CLAMP_TO_EDGE;
        this.tWrap = Texture.Wrap.CLAMP_TO_EDGE;
        this.biasLOD = 0;

        TextureBinder.bind(this);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_R16, width, height, 0, GL30.GL_RED, GL11.GL_UNSIGNED_SHORT, buffer);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_R, GL11.GL_RED);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_G, GL11.GL_RED);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_B, GL11.GL_RED);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL33.GL_TEXTURE_SWIZZLE_A, GL11.GL_ONE);

        if (generateMipMaps) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            this.anisotropy = MathUtils.clampInt(anisotropy, 1, Graphics.getMaxAnisotropy());
            if (Graphics.isAnisotropicFilteringSupported())
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, this.anisotropy);
        } else {
            this.anisotropy = 1;
        }
        STBImage.stbi_image_free(buffer);
    }

    void setSlot  (final int slot) { this.slot = slot; }
    int  getSlot  ()               { return slot; }
    int  getHandle()               { return handle; }

    public int   getAnisotropy() {
        return anisotropy;
    }
    public float getBiasLOD   () {
        return biasLOD;
    }

    public Color getPixelColor(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) throw new IndexOutOfBoundsException("Trying to read out of bounds pixel: (" + x + ", " + y + ") of " + Texture.class.getSimpleName() + " with dimensions: " + "(" + width + ", " + height + ")");

        if (pixmapBytes == null) {
            pixmapBytes = BufferUtils.createByteBuffer(width * height * 4);
            int slot = TextureBinder.bind(this);
            GL13.glActiveTexture(GL20.GL_TEXTURE0 + slot);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixmapBytes);
        }

        int index = (x + y * width) * 4;
        int r = pixmapBytes.get(index + 0) & 0xFF;
        int g = pixmapBytes.get(index + 1) & 0xFF;
        int b = pixmapBytes.get(index + 2) & 0xFF;
        int a = pixmapBytes.get(index + 3) & 0xFF;
        return new Color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
    }

    @Override
    public void delete() {
        TextureBinder.unbind(this);
        GL11.glDeleteTextures(handle);
        handle = -1;
    }

    public enum Precision {

        BITS_8,
        BITS_16,
        BITS_32,

    }

    public enum FilterMag {

        NEAREST (GL20.GL_NEAREST),
        LINEAR  (GL20.GL_LINEAR),
        ;

        public final int glValue;

        FilterMag(final int glValue) {
            this.glValue = glValue;
        }
    }

    public enum FilterMin {

        NEAREST                (GL20.GL_NEAREST),
        LINEAR                 (GL20.GL_LINEAR),
        NEAREST_MIPMAP_NEAREST (GL20.GL_NEAREST_MIPMAP_NEAREST),
        LINEAR_MIPMAP_NEAREST  (GL20.GL_LINEAR_MIPMAP_NEAREST),
        NEAREST_MIPMAP_LINEAR  (GL20.GL_NEAREST_MIPMAP_LINEAR),
        LINEAR_MIPMAP_LINEAR   (GL20.GL_LINEAR_MIPMAP_LINEAR)
        ;

        public final int glValue;

        FilterMin(final int glValue) {
            this.glValue = glValue;
        }

    }

    public enum Wrap {

        MIRRORED_REPEAT(GL20.GL_MIRRORED_REPEAT),
        CLAMP_TO_EDGE(GL20.GL_CLAMP_TO_EDGE),
        REPEAT(GL20.GL_REPEAT)
        ;

        public final int glValue;

        Wrap(int glValue) {
            this.glValue = glValue;
        }

    }

    // TODO: use this as input?
    public enum Format {

        RED(GL11.GL_RED),
        RGB(GL11.GL_RGB),
        RGBA(GL11.GL_RGBA),
        SRGB(GL30.GL_SRGB),
        SRGB_ALPHA(GL30.GL_SRGB_ALPHA),
        ;

        public final int glValue;

        Format(int glValue) {
            this.glValue = glValue;
        }

    }

}
