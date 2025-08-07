package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.HashSet;
import java.util.Set;

// TODO: support GPU instancing.
// TODO: on my AMD 6900, glsl allows 16 attributes.
public enum VertexAttribute {

    // TODO: see how to handle this later.
    //POSITION_2D        (GL20.GL_FLOAT_VEC2,2,"a_position"   , 0,  GL11.GL_FLOAT,         false),
    //NORMAL_2D          (GL20.GL_FLOAT_VEC2,2,"a_normal"     , 4,  GL11.GL_FLOAT,         false),
    //TANGENT_2D         (GL20.GL_FLOAT_VEC2,2,"a_tangent"    , 5,  GL11.GL_FLOAT,         false), // TODO: normalize. Look at the docs.

    POSITION       (GL20.GL_FLOAT_VEC3,3,"a_position"   , 0,  GL11.GL_FLOAT,         false),
    @Deprecated COLOR          (GL20.GL_FLOAT_VEC4,4,"a_color"      , 1,  GL11.GL_UNSIGNED_BYTE, true),
    TEXT_COORDS0   (GL20.GL_FLOAT_VEC2,2,"a_textCoords0", 2,  GL11.GL_FLOAT,         false),
    TEXT_COORDS1   (GL20.GL_FLOAT_VEC2,2,"a_textCoords1", 3,  GL11.GL_FLOAT,         false),
    NORMAL         (GL20.GL_FLOAT_VEC3,3,"a_normal"     , 4,  GL11.GL_FLOAT,         false), // TODO: normalize. Look at the docs.
    TANGENT        (GL20.GL_FLOAT_VEC3,3,"a_tangent"    , 5,  GL11.GL_FLOAT,         false), // TODO: normalize. Look at the docs.
    @Deprecated BI_TANGENT     (GL20.GL_FLOAT_VEC3,3,"a_biTangent"  , 6,  GL11.GL_FLOAT,         false), // TODO: normalize. Look at the docs.
    BONE_WEIGHT0   (GL20.GL_FLOAT_VEC3,2,"a_boneWeight0", 7,  GL11.GL_FLOAT,         false),
    BONE_WEIGHT1   (GL20.GL_FLOAT_VEC3,2,"a_boneWeight1", 8,  GL11.GL_FLOAT,         false),
    BONE_WEIGHT2   (GL20.GL_FLOAT_VEC3,2,"a_boneWeight2", 9,  GL11.GL_FLOAT,         false),
    BONE_WEIGHT3   (GL20.GL_FLOAT_VEC3,2,"a_boneWeight3", 10, GL11.GL_FLOAT,         false),
    BONE_WEIGHT4   (GL20.GL_FLOAT_VEC3,2,"a_boneWeight4", 11, GL11.GL_FLOAT,         false),
    ;

    public final int     glslVariableType;
    public final int     dimension;
    public final String  glslVariableName;
    public final int     glslLocation;
    public final int     glType;
    public final boolean normalized;
    public final int     bitmask;

    VertexAttribute(final int glslVariableType, final int dimension, final String glslVariableName, final int glslLocation, final int glType, final boolean normalized) {
        this.glslVariableType = glslVariableType;
        this.dimension = dimension;
        this.glslVariableName = glslVariableName;
        this.glslLocation = glslLocation;
        this.glType = glType;
        this.normalized = normalized;
        this.bitmask = 0b000001 << glslLocation;
    }

    public static int getShaderBitmask(final String[] shaderAttributeNames) {
        int bitmask = 0;

        for (final String shaderAttributeName : shaderAttributeNames) {
            for (VertexAttribute vertexAttribute : values()) {
                if (vertexAttribute.glslVariableName.equals(shaderAttributeName)) {
                    bitmask |= vertexAttribute.bitmask;
                    break;
                }
            }
        }

        return bitmask;
    }

    public static int getBitmask(final VertexAttribute... vertexAttributes) {
        int bitmask = 0;

        for (final VertexAttribute vertexAttribute : vertexAttributes) {
            bitmask |= vertexAttribute.bitmask;
        }
        return bitmask;
    }

    public static int generateBitmask(final Array<VertexAttribute> attributes) {
        int bitmask = 0b0000;

        for (final VertexAttribute attribute : attributes) {
            bitmask |= attribute.bitmask;
        }
        return bitmask;
    }

    public static int getAttributeLocation(final String name) {
        for (VertexAttribute attribute : values()) {
            if (attribute.glslVariableName.equals(name)) return attribute.glslLocation;
        }
        return -1;
    }

    public static boolean isValidGlslAttributeName(final String name) {
        for (VertexAttribute attribute : values()) {
            if (attribute.glslVariableName.equals(name)) return true;
        }
        return false;
    }

    public static Set<String> getValidAttributeNames() {
        Set<String> validAttributeNames = new HashSet<>();
        for (VertexAttribute attribute : values()) {
            validAttributeNames.add(attribute.glslVariableName);
        }
        return validAttributeNames;
    }

}
