package com.heavybox.jtix.graphics;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

public class FrameBufferBinder {

    private static FrameBuffer boundFrameBuffer = null;
    private static IntBuffer   bountAttachmentScreen = BufferUtils.createIntBuffer(1).put(GL30.GL_COLOR_ATTACHMENT0).flip();

    public static void bind() {
        bind(null);
    }

    public static void bind(@Nullable FrameBuffer frameBuffer) {
        //if (Renderer2D_new.isDrawing()) throw new GraphicsException("Cannot switch frame buffers during a drawing sequence (between Renderer2D.begin() and Renderer2D.end(). Call Renderer2D.end() and only then bind a new frame buffer.");
        if (boundFrameBuffer == frameBuffer) {
            return; // prevent redundant frame buffer binds.
        }

        if (frameBuffer == null) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
            GL30.glDrawBuffers(bountAttachmentScreen);
            boundFrameBuffer = null;
            GL20.glViewport(0, 0, Graphics.getWindowWidth(), Graphics.getWindowHeight());
            return;
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer.getHandle());
        GL30.glDrawBuffers(frameBuffer.boundAttachments);
        boundFrameBuffer = frameBuffer;
        GL20.glViewport(0, 0, frameBuffer.width, frameBuffer.height);
    }

}
