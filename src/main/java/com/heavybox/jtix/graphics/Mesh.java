package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.memory.MemoryUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

    public int     vertexArrayObjectId;
    public int     vertexCount;
    public boolean useIndices;
    public float   boundingBoxSize;
    public int     attributeBitmask;
    public int[]   vertexBufferObjects;

    public Material material;

    // Constructs a 2D Mesh
    public Mesh(float[] positions, float[] colors, float[] uvs, int[] indices, final Material material) {
        Array<VertexAttribute> attributesCollector = new Array<>();
        ArrayInt vbosCollector = new ArrayInt();
        this.vertexCount = indices != null ? indices.length : positions.length / 3;

        this.vertexArrayObjectId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexArrayObjectId);
        {
            storeDataInAttributeList(VertexAttribute.POSITION_2D, positions, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.COLOR, colors, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.TEXT_COORDS0, uvs, attributesCollector, vbosCollector);
            storeIndicesBuffer(indices, vbosCollector);
        }
        GL30.glBindVertexArray(0);

        this.attributeBitmask = VertexAttribute.generateBitmask(attributesCollector);
        this.useIndices = indices != null;
        this.vertexBufferObjects = vbosCollector.pack();
        this.material = material;

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < positions.length; i += 2) {
            float x = positions[i];
            float y = positions[i + 1];
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }

        float width = maxX - minX;
        float height = maxY - minY;
        this.boundingBoxSize = Math.max(width, height);
    }

    private void storeIndicesBuffer(int[] indices, ArrayInt vbosCollector) {
        if (indices == null) return;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = MemoryUtils.store(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        vbosCollector.add(vbo);
    }

    private void storeDataInAttributeList(final VertexAttribute attribute, final float[] data, Array<VertexAttribute> attributesCollector, ArrayInt vbosCollector) {
        if (data == null) return;
        int vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
        FloatBuffer buffer = MemoryUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribute.glslLocation, attribute.dimension, attribute.glType, attribute.normalized, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // unbind
        vbosCollector.add(vbo);
        attributesCollector.add(attribute);
    }

}
