package com.heavybox.jtix.graphics;

public final class FrameBufferBuilder {

    private static final FrameBufferBuilder builder = new FrameBufferBuilder();

    private static boolean building       = false;
    private static int     internalFormat = -1;
    private static int     width          = -1;
    private static int     height         = -1;

    private FrameBufferBuilder() {}

    public static FrameBufferBuilder begin() {
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
        building = false;
        return null; // TODO: return a FrameBuffer using all args constructor.
    }

}
