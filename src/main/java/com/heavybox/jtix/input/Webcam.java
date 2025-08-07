package com.heavybox.jtix.input;

import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TextureBinder;
import com.heavybox.jtix.memory.MemoryResourceHolder;
import nu.pattern.OpenCV;
import org.lwjgl.opengl.GL11;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.nio.ByteBuffer;

// TODO: add functionality to test if webcam is even available
public class Webcam implements MemoryResourceHolder {

    private boolean      init      = false;
    private VideoCapture capture   = null;
    private Mat          mat       = null;
    private Texture      feed      = null;
    private byte[]       rgbData   = null;
    private ByteBuffer   rgbBuffer = null;

    public void init() {
        if (init) return;
        OpenCV.loadShared();
        this.capture = new VideoCapture(0);
        this.mat = new Mat();
        init = true;
    }

    public Texture getFeed() {
        if (!init) init();
        if (capture.read(mat)) {
            int width = mat.width();
            int height = mat.height();
            if (rgbData == null) {
                rgbData = new byte[width * height * 3]; // Example RGB data
                ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 3);
                this.feed = new Texture(width, height, buffer,
                        Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                        Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE,1,false);
                rgbBuffer = ByteBuffer.allocateDirect(width * height * 3);
            }
            mat.get(0,0, rgbData);

            rgbBuffer.put(rgbData);
            rgbBuffer.flip();
            TextureBinder.bind(feed);
            GL11.glTexSubImage2D(
                    GL11.GL_TEXTURE_2D, // Target
                    0,                  // Mipmap level
                    0,
                    0,
                    width,
                    height,
                    GL11.GL_RGB,
                    GL11.GL_UNSIGNED_BYTE,
                    rgbBuffer          // Data
            );
        }

        return feed;
    }

    @Override
    public void deleteAll() {
        if (capture != null) capture.release();
        if (feed != null) feed.delete();
    }

}
