package com.heavybox.jtix.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

// TODO: use this.
// https://www.youtube.com/watch?v=cazGoYbvPIE

// https://github.com/emanueles/opengl/blob/master/texture/texture3d.c
// https://moddb.fandom.com/wiki/OpenGL:Tutorials:3D_Textures
// https://github.com/damdoy/opengl_examples
public class Texture3D {

    // TODO: implement this constructor using STBI to load the textures and store them in the byte buffers, one after another.
    public Texture3D(final String... filePaths) {
        //GL30.glTexImage3D();
        int width = 64, height = 64, depth = 64;
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * depth * 4); // RGBA

        // Fill data (example: RGB = position, A = 255)
        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    buffer.put((byte) x);      // R
                    buffer.put((byte) y);      // G
                    buffer.put((byte) z);      // B
                    buffer.put((byte) 255);    // A
                }
            }
        }
        buffer.flip();

//        int texID = glGenTextures();
//        glBindTexture(GL_TEXTURE_3D, texID);
//
//        GL11.glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//        GL11.glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//        GL11.glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        GL11.glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//        GL11.glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
//
//        GL30.glTexImage3D(GL_TEXTURE_3D, 0, GL_RGBA, width, height, depth, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }

    public Texture3D(final String filePath, int depth) {
        if (depth <= 0) throw new GraphicsException("Depth must be an integer >= 1. Got: " + depth);
    }

}
