package com.heavybox.jtix.graphics;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationException;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FreeType;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.glfwCreateCursor;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;

public final class Graphics {

    /* graphics state and parameters */
    private static boolean isContinuous      = true;
    private static long    lastFrameTime     = -1;
    private static float   deltaTime;
    private static float   elapsedTime;
    private static boolean resetDeltaTime    = false;
    private static long    frameId           = 0;
    private static long    frameCounterStart = 0;
    private static int     frames            = 0;
    private static int     fps;
    private static int     targetFps         = 120;
    private static int     prevTargetFps     = targetFps;
    private static int     idleFps           = 10;
    private static int     maxTextureSize    = -1;
    private static int     maxAnisotropy     = 0;
    private static int     anisotropicFilteringSupported = -1;

    /* custom cursors */
    private static final HashMap<String, Long> customCursors = new HashMap<>();
    private static long cursorText             = -1;
    private static long cursorPointer          = -1;
    private static long cursorCross            = -1;
    private static long cursorHorizontalResize = -1;
    private static long cursorVerticalResize   = -1;
    private static long cursorNotAllowed       = -1;
    private static long cursorPointingHand     = -1;
    private static long cursorResizeNESW       = -1;
    private static long cursorResizeNWSE       = -1;
    private static long cursorResizeAll        = -1;

    /* freetype library */
    private static long freeType = -1;

    /* useful textures */
    private static Texture singleWhitePixel;
    private static Texture singleTransparentPixel;
    private static Texture singleBlackPixel;
    private static Texture singleNormalMapPixel;

    private Graphics() {}

    public static void update() {
        long time = System.nanoTime();
        if (lastFrameTime == -1) lastFrameTime = time;
        if (resetDeltaTime) {
            resetDeltaTime = false;
            deltaTime = 0;
        } else {
            deltaTime = (time - lastFrameTime) / 1000000000.0f;
        }
        lastFrameTime = time;
        elapsedTime += deltaTime;

        if (time - frameCounterStart >= 1000000000) {
            fps = frames;
            frames = 0;
            frameCounterStart = time;
        }
        frames++;
        frameId++;
    }

    public static float getContentScaleX() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        FloatBuffer px = BufferUtils.createFloatBuffer(1);
        FloatBuffer py = BufferUtils.createFloatBuffer(1);
        GLFW.glfwGetMonitorContentScale(monitor, px, py);
        return px.get(0);
    }

    public static float getContentScaleY() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        FloatBuffer px = BufferUtils.createFloatBuffer(1);
        FloatBuffer py = BufferUtils.createFloatBuffer(1);
        GLFW.glfwGetMonitorContentScale(monitor, px, py);
        return py.get(0);
    }

    public static long getFrameId() { return frameId; }

    public static int getFps() {
        return fps;
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static float getElapsedTime() {
        return elapsedTime;
    }

    public static int getIdleFps() {
        return idleFps;
    }

    public static void setIdleFps(int idleFps) {
        Graphics.idleFps = idleFps;
    }

    public static int getTargetFps() {
        return targetFps;
    }

    public static void setTargetFps(int targetFps) {
        prevTargetFps = Graphics.targetFps;
        Graphics.targetFps = targetFps;
    }

    public static int getMaxTextureSize() {
        if (maxTextureSize == -1) maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
        return maxTextureSize;
    }

    public static int getFrameCount() {
        return frames;
    }

    public static void setContinuousRendering(boolean isContinuous) {
        Graphics.isContinuous = isContinuous;
    }

    public static boolean isContinuousRendering () {
        return isContinuous;
    }

    public static int getMonitorWidth() {
        return Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())).width();
    }

    public static int getMonitorHeight() {
        return Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())).height();
    }

    public static int getWindowHeight() {
        return Application.getWindowHeight();
    }

    public static int getWindowWidth() {
        return Application.getWindowWidth();
    }

    public static float getMonitorAspectRatio() {
        return getMonitorWidth() / (float) getMonitorHeight();
    }

    public static int getMaxVerticesPerDrawCall() {
        return GL11.glGetInteger(GL20.GL_MAX_ELEMENTS_VERTICES);
    }

    public static int getMaxIndicesPerDrawCall() {
        return GL11.glGetInteger(GL20.GL_MAX_ELEMENTS_INDICES);
    }

    public static float getWindowAspectRatio() {
        return getWindowWidth() / (float) getWindowHeight();
    }

    // TODO: test
    public static void enableVSync() {
        int refreshRate = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()).refreshRate();
        setTargetFps(refreshRate);
        Application.enableVSync();
    }

    // TODO: test
    public static void disableVSync() {
        GLFW.glfwSwapInterval(1);
        setTargetFps(prevTargetFps); // restore target refresh rate before vsync.
        Application.disableVSync();
    }

    // TODO: test
    public static boolean isVSyncEnabled() {
        return Application.isVSyncEnabled();
    }

    public static int getMaxFragmentShaderTextureUnits() {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL20.GL_MAX_TEXTURE_IMAGE_UNITS, intBuffer);
        return intBuffer.get(0);
    }

    public static int getMaxBoundTextureUnits() {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, intBuffer);
        return intBuffer.get(0);
    }

    public static boolean isAnisotropicFilteringSupported() {
        if (anisotropicFilteringSupported == -1) {
            boolean supported = GLFW.glfwExtensionSupported("GL_EXT_texture_filter_anisotropic");
            if (supported) anisotropicFilteringSupported = 1;
            else anisotropicFilteringSupported = 0;
        }

        return anisotropicFilteringSupported == 1;
    }

    public static int getMaxMSAA() {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);
        GL11.glGetIntegerv(GL30.GL_MAX_SAMPLES, intBuffer);
        return intBuffer.get(0);
    }

    public static int getMaxShaderAttributes() {
        return GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS);
    }

    public static int getMaxAnisotropy() {
        if (maxAnisotropy > 0) return maxAnisotropy;

        if (GLFW.glfwExtensionSupported("GL_EXT_texture_filter_anisotropic")) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
            buffer.position(0);
            buffer.limit(buffer.capacity());
            GL20.glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, buffer);
            maxAnisotropy = (int) buffer.get(0);
        } else {
            maxAnisotropy = 1;
        }

        return maxAnisotropy;
    }

    public static float getMaxLineWidth() {
        float[] lineWidth = new float[2];
        GL11.glGetFloatv(GL11.GL_LINE_WIDTH_RANGE, lineWidth);
        return lineWidth[1];
    }

    /* Textures */

    public static Texture getTextureSingleWhitePixel() {
        if (singleWhitePixel != null) return singleWhitePixel;

        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.put((byte) ((0xFFFFFFFF >> 16) & 0xFF)); // Red component
        buffer.put((byte) ((0xFFFFFFFF >> 8) & 0xFF));  // Green component
        buffer.put((byte) (0xFF));                      // Blue component
        buffer.put((byte) ((0xFFFFFFFF >> 24) & 0xFF)); // Alpha component
        buffer.flip();

        singleWhitePixel = new Texture(1, 1, buffer,
                Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE,1);
        return singleWhitePixel;
    }

    public static Texture getTextureSingleBlackPixelTransparent() {
        if (singleTransparentPixel != null) return singleTransparentPixel;

        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.put((byte) 0x00); // Red
        buffer.put((byte) 0x00); // Green
        buffer.put((byte) 0x00); // Blue
        buffer.put((byte) 0x00); // Alpha (fully transparent)
        buffer.flip();

        singleTransparentPixel = new Texture(1, 1, buffer,
                Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        return singleTransparentPixel;
    }

    public static Texture getTextureSingleBlackPixelOpaque() {
        if (singleBlackPixel != null) return singleBlackPixel;

        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.put((byte) (0)); // Red component
        buffer.put((byte) (0)); // Green component
        buffer.put((byte) (0)); // Blue component
        buffer.put((byte) ((0xFFFFFFFF >> 24) & 0xFF)); // Alpha component
        buffer.flip();

        singleBlackPixel = new Texture(1, 1, buffer,
                Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE,1);
        return singleBlackPixel;
    }

    public static Texture getTextureSinglePixelNormalMap() {
        if (singleNormalMapPixel != null) return singleNormalMapPixel;

        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.put((byte) 0x80); // Red component (128)
        buffer.put((byte) 0x80); // Green component (128)
        buffer.put((byte) 0xFF); // Blue component (255)
        buffer.put((byte) 0xFF); // Alpha component (255)
        buffer.flip();

        singleNormalMapPixel = new Texture(1, 1, buffer,
                Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        return singleNormalMapPixel;
    }

    /* set cursor */

    // create cursors

    public static void setCursorDefault() {
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), 0);
    }

    public static void setCursorText() {
        if (cursorText == -1) cursorText = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), cursorText);
    }

    public static void setCursorPointer() {
        if (cursorPointer == -1) cursorPointer = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), cursorPointer);
    }

    public static void setCursorCross() {
        if (cursorCross == -1) cursorCross = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR);
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), cursorCross);
    }

    public static void setCursorResizeHorizontal() {
        if (cursorHorizontalResize == -1) cursorHorizontalResize = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), cursorHorizontalResize);
    }

    public static void setCursorResizeVertical() {
        if (cursorVerticalResize == -1) cursorVerticalResize = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), cursorVerticalResize);
    }

    public static void setCursorNotAllowed() {
        if (cursorNotAllowed == -1) cursorNotAllowed = GLFW.glfwCreateStandardCursor(GLFW.GLFW_NOT_ALLOWED_CURSOR);
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), cursorNotAllowed);
    }

    public static void setCursorPointingHand() {
        if (cursorPointingHand == -1) cursorPointingHand = GLFW.glfwCreateStandardCursor(GLFW.GLFW_POINTING_HAND_CURSOR);
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), cursorPointingHand);
    }

    public static void setCursorResizeNESW() {
        if (cursorResizeNESW == -1) cursorResizeNESW = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NESW_CURSOR);
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), cursorResizeNESW);
    }

    public static void setCursorResizeNWSE() {
        if (cursorResizeNWSE == -1) cursorResizeNWSE = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR);
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), cursorResizeNWSE);
    }

    public static void setCursorResizeAll() {
        if (cursorResizeAll == -1) cursorResizeAll = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_ALL_CURSOR);
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        GLFW.glfwSetCursor(Application.getWindowHandle(), cursorResizeAll);
    }

    public static void setCursorNone() {
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
    }

    public static void setCursorCustom(final String path) {
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        Long cursor = customCursors.get(path);
        if (cursor != null) {
            GLFW.glfwSetCursor(Application.getWindowHandle(), cursor);
        } else {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer widthBuffer = stack.mallocInt(1);
                IntBuffer heightBuffer = stack.mallocInt(1);
                IntBuffer channelsBuffer = stack.mallocInt(1);
                ByteBuffer buffer = STBImage.stbi_load(path, widthBuffer, heightBuffer, channelsBuffer, 4);
                if (buffer == null) throw new ApplicationException("Failed to load a texture file for custom cursor. Check that the path is correct: " + path
                        + System.lineSeparator() + "STBImage error: "
                        + STBImage.stbi_failure_reason());
                int width = widthBuffer.get();
                int height = heightBuffer.get();
                GLFWImage cursorImage = GLFWImage.malloc();
                cursorImage.width(width);
                cursorImage.height(height);
                cursorImage.pixels(buffer);

                long customCursor = glfwCreateCursor(cursorImage, width / 2, height / 2); // Hotspot at center
                GLFW.glfwSetCursor(Application.getWindowHandle(), customCursor);
                customCursors.put(path, customCursor);
            }
        }
    }

    public static void cleanup() {
        /* destroy cursors */
        if (cursorText != -1) GLFW.glfwDestroyCursor(cursorText);
        if (cursorPointer != -1) GLFW.glfwDestroyCursor(cursorPointer);
        if (cursorCross != -1) GLFW.glfwDestroyCursor(cursorCross);
        if (cursorHorizontalResize != -1) GLFW.glfwDestroyCursor(cursorHorizontalResize);
        if (cursorVerticalResize != -1) GLFW.glfwDestroyCursor(cursorVerticalResize);

        if (cursorPointingHand != -1) GLFW.glfwDestroyCursor(cursorPointingHand);
        if (cursorNotAllowed != -1) GLFW.glfwDestroyCursor(cursorNotAllowed);
        if (cursorResizeNESW != -1) GLFW.glfwDestroyCursor(cursorResizeNESW);
        if (cursorResizeNWSE != -1) GLFW.glfwDestroyCursor(cursorResizeNWSE);
        if (cursorResizeAll != -1) GLFW.glfwDestroyCursor(cursorResizeAll);

        //Renderer2D_new.delete();
        // TODO: delete internal textures

        for (Map.Entry<String, Long> cursorEntry : customCursors.entrySet()) {
            long cursor = cursorEntry.getValue();
            GLFW.glfwDestroyCursor(cursor);
        }
    }

    /* FreeType */

    public static long getFreeType() {
        if (freeType != -1) return freeType;
        PointerBuffer libPointerBuffer = BufferUtils.createPointerBuffer(1);
        FreeType.FT_Init_FreeType(libPointerBuffer);
        freeType = libPointerBuffer.get(0);
        return freeType;
    }

}
