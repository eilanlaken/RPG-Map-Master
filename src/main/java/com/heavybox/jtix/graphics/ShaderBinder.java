package com.heavybox.jtix.graphics;

import org.lwjgl.opengl.GL20;

public class ShaderBinder {

    private static int boundProgram = -1;

    public static boolean bind(final Shader shader) {
        if (boundProgram == shader.program) return false;
        GL20.glUseProgram(shader.program);
        boundProgram = shader.program;
        return true;
    }

    // TODO: see if there's a real need for that
    @Deprecated public static void unbind() {
        GL20.glUseProgram(0);
        boundProgram = -1;
    }

}
