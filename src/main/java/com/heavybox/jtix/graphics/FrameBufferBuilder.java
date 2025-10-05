package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import org.lwjgl.opengl.GL30;

public final class FrameBufferBuilder {

    private static final FrameBufferBuilder builder = new FrameBufferBuilder();

    private static boolean       building          = false;
    private static int           internalFormat    = GL30.GL_RGBA16F; // TODO: allow all selections.
    private static int           width             = 0;
    private static int           height            = 0;
    private static Array<String> colorAttachments  = new Array<>();
    private static int           depthAttachment   = 0; // 0 == none, 1 = buffer, 2 = texture
    private static int           stencilAttachment = 0; // 0 == none, 1 = buffer, 2 = texture

    private FrameBufferBuilder() {}

    public FrameBufferBuilder begin() {
        if (building) throw new GraphicsException("Error: nesting FrameBufferBuilder.begin() and FrameBufferBuilder.end()" +
                " calls is not allowed. Must call FrameBufferBuilder.end() before building a new FrameBuffer.");
        building = true;
        return builder;
    }

    // TODO: change to a gl enum
//    public FrameBufferBuilder setFormat(int format) {
//        if (!building) throw new GraphicsException("Must call " + FrameBufferBuilder.class.getSimpleName() + ".begin() first.");
//        internalFormat = format;
//        return builder;
//    }

    public FrameBufferBuilder addColorAttachment(final String name) {
        if (!building) throw new GraphicsException("Must call " + FrameBufferBuilder.class.getSimpleName() + ".begin() first.");
        if (name == null) throw new GraphicsException("Must use a non-null name for each color attachment.");
        if (colorAttachments.contains(name,false)) throw new GraphicsException("Color attachment with name " + name + " already added. Use a different name.");
        colorAttachments.add(name);
        return builder;
    }

    public FrameBufferBuilder useDepthBuffer(boolean useTexture) {
        if (!building) throw new GraphicsException("Must call " + FrameBufferBuilder.class.getSimpleName() + ".begin() first.");
        if (depthAttachment != 0) throw new GraphicsException("Already called useDepthBuffer(). Depth buffer can only be configured once.");
        depthAttachment = useTexture ? 2 : 1;
        return builder;
    }

    public FrameBufferBuilder useStencilBuffer(boolean useTexture) {
        if (!building) throw new GraphicsException("Must call " + FrameBufferBuilder.class.getSimpleName() + ".begin() first.");
        if (stencilAttachment != 0) throw new GraphicsException("Already called useStencilBuffer(). Stencil buffer can only be configured once.");
        stencilAttachment = useTexture ? 2 : 1;
        return builder;
    }

    public FrameBuffer end() {
        if (!building) throw new GraphicsException("Must call " + FrameBufferBuilder.class.getSimpleName() + ".begin() first.");
        if (width <= 0 || height <= 0) throw new GraphicsException("""
                Must set width of the FrameBuffer using setWidth(). Example:
                 FrameBufferBuilder.begin()
                 .setWidth(1920)
                 .setWidth(1080)
                 ...
                 .end();""");
        if (colorAttachments.isEmpty()) throw new GraphicsException("Must add at least 1 color attachment by calling addColorAttachment().");
        FrameBuffer frameBuffer = new FrameBuffer(width, height);// TODO: use the proper all args constructor

        reset();
        return frameBuffer; // TODO: return a FrameBuffer using all args constructor.
    }

    private static void reset() {
        building = false;
        width = 0;
        height = 0;
        colorAttachments.clear();
        depthAttachment = 0;
        stencilAttachment = 0;
    }

}
