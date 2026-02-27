package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.memory.MemoryResource;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

// TODO: implement.
// https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/glutils/FrameBuffer.java
// https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/glutils/GLFrameBuffer.java#L57
public class FrameBuffer implements MemoryResource {

    public  final int width;
    public  final int height;
    private final int handle;
    private final int depthStencilRBO;

    private Map<String, ColorAttachment> colorAttachmentsMap = new HashMap<>();
    private Array<Texture> colorAttachmentsArray = new Array<>(true, 4);
    @Deprecated private Texture colorAttachment0;
    @Deprecated private Texture colorAttachment1;

    private Texture   depthAttachment;
    final   IntBuffer boundAttachments;
    private IntBuffer activeAttachments;

    public FrameBuffer(int width, int height, Array<String> colorAttachmentsNames, int depthAttachment, int stencilAttachment) {
        this.width = width;
        this.height = height;
        handle = GL30.glGenFramebuffers();

        this.boundAttachments = BufferUtils.createIntBuffer(colorAttachmentsNames.size);
        this.activeAttachments = BufferUtils.createIntBuffer(colorAttachmentsNames.size);
        for (int i = 0; i < colorAttachmentsNames.size; i++) {
            this.boundAttachments.put(GL30.GL_COLOR_ATTACHMENT0 + i);
        }
        this.boundAttachments.flip();
        Graphics.bindFrameBuffer(this);

        for (int i = 0; i < colorAttachmentsNames.size; i++) {
            String name = colorAttachmentsNames.get(i);
            Texture colorAttachment = new Texture(width, height, GL30.GL_RGBA16F, GL30.GL_RGBA);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + i, GL11.GL_TEXTURE_2D, colorAttachment.getHandle(), 0);
            colorAttachmentsArray.add(colorAttachment);
            colorAttachmentsMap.put(name, new ColorAttachment(colorAttachment, i));
        }
        colorAttachment0 = colorAttachmentsArray.first();

        if (depthAttachment == 1 && stencilAttachment == 1) {
            depthStencilRBO = GL30.glGenRenderbuffers();
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthStencilRBO);
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, depthStencilRBO);
        } else {
            depthStencilRBO = -1;
            // TODO
        }

        // Check if FrameBuffer is complete
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new GraphicsException("Could not create FrameBuffer. Error: " + "TODO.");
        }
        Graphics.bindFrameBufferScreen();
    }

    public void setRenderTargets(final String ...targets) {
        if (!Graphics.frameBufferIsBound(this)) throw new GraphicsException("FrameBuffer must be bound when calling setRenderTargets. Bind a FrameBuffer using FrameBufferBinder.bind(frameBuffer)");
        activeAttachments.clear();
        for (String target : targets) {
            int index = colorAttachmentsMap.get(target).getGLAttachmentIndex();
            this.activeAttachments.put(index);
        }
        this.activeAttachments.flip();
        GL30.glDrawBuffers(this.activeAttachments);
    }

    // TODO: customize constructor to support creation of HDR FrameBuffers, Bordered FrameBuffers etc.
    @Deprecated public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        // Create FrameBuffer
        handle = GL30.glGenFramebuffers();

        this.boundAttachments = BufferUtils.createIntBuffer(1);
        this.boundAttachments.put(GL30.GL_COLOR_ATTACHMENT0);
        this.boundAttachments.flip();

        Graphics.bindFrameBuffer(this);
        //GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

        colorAttachment0 = new Texture(width, height, GL30.GL_RGBA16F, GL30.GL_RGBA);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorAttachment0.getHandle(), 0);
        colorAttachment1 = null;

        depthStencilRBO = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthStencilRBO);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, depthStencilRBO);

        // Check if framebuffer is complete
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new GraphicsException("Could not create FrameBuffer. Error: " + "TODO.");
        }

        Graphics.bindFrameBufferScreen();
    }

    public FrameBuffer(int width, int height, boolean texturesDepth) {
        this.width = width;
        this.height = height;
        // Create FrameBuffer
        handle = GL30.glGenFramebuffers();

        this.boundAttachments = BufferUtils.createIntBuffer(1);
        this.boundAttachments.put(GL30.GL_COLOR_ATTACHMENT0);
        this.boundAttachments.flip();

        Graphics.bindFrameBuffer(this);
        //GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

        colorAttachment0 = new Texture(width, height, GL30.GL_RGBA16F, GL30.GL_RGBA);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorAttachment0.getHandle(), 0);
        colorAttachment1 = null;

        depthAttachment = new Texture(width, height, GL30.GL_DEPTH_COMPONENT32F, GL11.GL_DEPTH_COMPONENT);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthAttachment.getHandle(), 0);


        depthStencilRBO = -1;
//        depthStencilRBO = GL30.glGenRenderbuffers();
//        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthStencilRBO);
//        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
//        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, depthStencilRBO);



        // Check if framebuffer is complete
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new GraphicsException("Could not create FrameBuffer. Error: " + "TODO.");
        }

        Graphics.bindFrameBufferScreen();
    }

    // TODO: standardize "all args" constructor.
    @Deprecated public FrameBuffer(int width, int height, int colorAttachments) {
        this.width = width;
        this.height = height;
        this.handle = GL30.glGenFramebuffers();

        this.boundAttachments = BufferUtils.createIntBuffer(2);
        this.boundAttachments.put(GL30.GL_COLOR_ATTACHMENT0);
        this.boundAttachments.put(GL30.GL_COLOR_ATTACHMENT1);
        this.boundAttachments.flip();

        Graphics.bindFrameBuffer(this);

        colorAttachment0 = new Texture(width, height, GL30.GL_RGBA16F, GL30.GL_RGBA);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorAttachment0.getHandle(), 0);

        colorAttachment1 = new Texture(width, height, GL30.GL_RGBA16F, GL30.GL_RGBA);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL11.GL_TEXTURE_2D, colorAttachment1.getHandle(), 0);



        depthStencilRBO = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthStencilRBO);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, depthStencilRBO);

        // Check if framebuffer is complete
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new GraphicsException("Could not create FrameBuffer. Error: " + "TODO.");
        }

        Graphics.bindFrameBufferScreen();
    }

    public int getHandle() {
        return handle;
    }

    public Texture getDefaultColorAttachment() {
        return colorAttachment0;
    }
    public Texture getColorAttachment(final int index) {
        return colorAttachmentsArray.get(index);
    }
    public Texture getColorAttachment(final String name) {
        return colorAttachmentsMap.get(name).texture;
    }
    public Texture getDepthAttachment() {
        return depthAttachment;
    }

    @Override
    public void delete() {
        GL30.glDeleteFramebuffers(handle);
        if (colorAttachment0 != null) colorAttachment0.delete();
        if (colorAttachment1 != null) colorAttachment1.delete();
        GL30.glDeleteRenderbuffers(depthStencilRBO);
    }

    // TODO: test
    public void bind() {
        Graphics.bindFrameBuffer(this);
    }

    // TODO: test
    public static void bind(@Nullable FrameBuffer frameBuffer) {
        Graphics.bindFrameBuffer(frameBuffer);
    }

    // TODO: add format customization support.
    private static class ColorAttachment {

        final Texture texture;
        final int     index;

        ColorAttachment(final Texture texture, final int index) {
            this.texture = texture;
            this.index = index;
        }

        int getGLAttachmentIndex() {
            return GL30.GL_COLOR_ATTACHMENT0 + index;
        }

    }

}



/*
// TODO: when doing FrameBuffer.begin(), change the gl viewport to the size of the framebuffer.
// Then, change it back to the OG framebuffer dimensions.

try (MemoryStack stack = MemoryStack.stackPush()) {
    IntBuffer fbWidth = stack.mallocInt(1);
    IntBuffer fbHeight = stack.mallocInt(1);
    GLFW.glfwGetFramebufferSize(windowHandle, fbWidth, fbHeight);
    GL20.glViewport(0, 0, fbWidth.get(0), fbHeight.get(0));
}

 */