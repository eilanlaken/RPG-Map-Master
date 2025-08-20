package com.heavybox.jtix.graphics;

public final class FrameBufferBuilder {

    private static final FrameBufferBuilder builder = new FrameBufferBuilder();

    private static boolean building       = false;
    private static int     internalFormat = -1;
    private static int     width          = -1;
    private static int     height         = -1;

    private FrameBufferBuilder() {}

    public static FrameBufferBuilder begin() {
        if (building) throw new GraphicsException("Error: nesting FrameBufferBuilder.begin() and FrameBufferBuilder.end()" +
                " calls is not allowed. Must call FrameBufferBuilder.end() before building a new FrameBuffer.");
        building = true;
        return builder;
    }

    // TODO: change to a gl enum
    public static FrameBufferBuilder setFormat(int format) {
        if (!building) throw new GraphicsException("Must call " + FrameBufferBuilder.class.getSimpleName() + ".begin() first.");
        internalFormat = format;
        return builder;
    }

    public static FrameBuffer end() {
        if (!building) throw new GraphicsException("Must call " + FrameBufferBuilder.class.getSimpleName() + ".begin() first.");
        if (width <= 0) throw new GraphicsException("""
                Must set width of the FrameBuffer using setWidth(). Example:
                 FrameBufferBuilder.begin();
                 FrameBufferBuilder.setWidth(1920);
                 FrameBufferBuilder.setWidth(1080);
                 ...
                 FrameBufferBuilder.end();""");
        FrameBuffer frameBuffer = new FrameBuffer(width, height);// TODO: use the proper all args constructor
        reset();

        return frameBuffer; // TODO: return a FrameBuffer using all args constructor.
    }

    private static void reset() {
        building = false;
        width = -1;
        height = -1;
    }

}
