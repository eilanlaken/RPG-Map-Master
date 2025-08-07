package com.heavybox.jtix.graphics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VertexAttributeTest {

    @Test
    void getShaderBitmask() {
    }

    @Test
    void getBitmask() {
    }

    @Test
    void getAttributeLocation() {
    }

    @Test
    void isValidGlslAttributeName() {
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_position"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_color"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_textCoords0"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_textCoords1"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_normal"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_tangent"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_biNormal"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_boneWeight0"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_boneWeight1"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_boneWeight2"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_boneWeight3"));
        Assertions.assertTrue(VertexAttribute.isValidGlslAttributeName("a_boneWeight4"));

        Assertions.assertFalse(VertexAttribute.isValidGlslAttributeName("position"));
        Assertions.assertFalse(VertexAttribute.isValidGlslAttributeName("A_NORMAL"));
        Assertions.assertFalse(VertexAttribute.isValidGlslAttributeName(""));
        Assertions.assertFalse(VertexAttribute.isValidGlslAttributeName(null));
    }
}