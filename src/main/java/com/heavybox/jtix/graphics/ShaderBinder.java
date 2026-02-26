package com.heavybox.jtix.graphics;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL20;

@Deprecated public class ShaderBinder {

    private static int boundProgram = -1;

    public static boolean bind(@NotNull final Shader shader) {
        if (boundProgram == shader.program) return false;
        GL20.glUseProgram(shader.program);
        boundProgram = shader.program;
        return true;
    }

}
