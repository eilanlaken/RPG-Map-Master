package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.memory.MemoryUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

// primitives:
// https://github.com/mrdoob/three.js/tree/dev/src/geometries
public class ModelMesh implements MemoryResource {

    public int     vertexArrayObjectId;
    public int     vertexCount;
    public boolean useIndices;
    public float   boundingSphereRadius;
    public int     attributeBitmask;
    public int[]   vertexBufferObjects;

    public ModelMesh(float[] positions, float[] uvs, @Deprecated float[] colors, float[] normals, float[] tangents, @Deprecated float[] biTangents, int[] indices, float boundingSphereRadius) {
        Array<VertexAttribute> attributesCollector = new Array<>();
        ArrayInt vbosCollector = new ArrayInt();
        this.vertexCount = indices != null ? indices.length : positions.length / 3;

        this.vertexArrayObjectId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexArrayObjectId);
        {
            storeDataInAttributeList(VertexAttribute.POSITION, positions, attributesCollector, vbosCollector);
            storeIndicesBuffer(indices, vbosCollector);
            storeDataInAttributeList(VertexAttribute.TEXT_COORDS0, uvs, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.NORMAL, normals, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.TANGENT, tangents, attributesCollector, vbosCollector);
            storeDataInAttributeList(VertexAttribute.BI_TANGENT, biTangents, attributesCollector, vbosCollector); // TODO: remove
        }
        GL30.glBindVertexArray(0);

        this.attributeBitmask = VertexAttribute.generateBitmask(attributesCollector);
        this.useIndices = indices != null;
        this.boundingSphereRadius = boundingSphereRadius;
        this.vertexBufferObjects = vbosCollector.pack();
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

    public boolean hasVertexAttribute(final VertexAttribute attribute) {
        return (attributeBitmask & attribute.bitmask) != 0;
    }

    // TODO: confirm it works
    @Override
    public void delete() {
        GL30.glDeleteVertexArrays(vertexArrayObjectId);
        for (int vbo : vertexBufferObjects) {
            GL30.glDeleteBuffers(vbo);
        }
    }


    public static ModelMesh createPlane(float width, float height, int subdivisionsHorizontal, int subdivisionsVertical) {
        float width_half = width / 2.0f;
        float height_half = height / 2.0f;

        int gridX = subdivisionsHorizontal;
        int gridY = subdivisionsVertical;

        int gridX1 = gridX + 1;
        int gridY1 = gridY + 1;

        float segment_width = width / gridX;
        float segment_height = height / gridY;

        ArrayFloat positions = new ArrayFloat(true,gridX1 * gridY1 * 3);
        ArrayFloat normals = new ArrayFloat(true,gridX1 * gridY1 * 3);
        ArrayFloat tangents = new ArrayFloat(true,gridX1 * gridY1 * 3);
        ArrayFloat biTangents = new ArrayFloat(true,gridX1 * gridY1 * 3);
        ArrayFloat uvs = new ArrayFloat(true,gridX1 * gridY1 * 2);
        ArrayInt indices = new ArrayInt(true, gridX * gridY * 6);

        // set vertex array buffer
        for (int iy = 0; iy < gridY1; iy++) {
			float y = iy * segment_height - height_half;
            for (int ix = 0; ix < gridX1; ix++) {
				float x = ix * segment_width - width_half;
                positions.add(x, -y, 0);
                normals.add(0, 0, 1);
                tangents.add(1, 0, 0);
                biTangents.add(0, 1, 0);
                uvs.add(ix / (float) gridX);
                uvs.add(1 - (iy / (float) gridY));
            }
        }

        // set element array buffer (indices)
        for (int iy = 0; iy < gridY; iy++) {
            for (int ix = 0; ix < gridX; ix++) {
				int a = ix + gridX1 * iy;
                int b = ix + gridX1 * (iy + 1);
                int c = (ix + 1) + gridX1 * (iy + 1);
                int d = (ix + 1) + gridX1 * iy;
                indices.add(a, b, d);
                indices.add(b, c, d);
            }
        }

        float radius = (float) Math.sqrt(width_half * width_half + height_half * height_half);
        // TODO: remove biTangents.
        return new ModelMesh(positions.items, uvs.items, null, normals.items, tangents.items, biTangents.items, indices.items, radius);
    }

    /* CREATE A 3D CUBE */

    // hard-coded : totally fine.
    public static ModelMesh createCube(float sizeX, float sizeY, float sizeZ) {
        float xh = sizeX / 2;
        float yh = sizeY / 2;
        float zh = sizeZ / 2;

        float radius = (float) Math.sqrt(xh * xh + yh * yh + zh * zh);

        float[] positions = {-xh, -yh, zh, -xh, yh, -zh, -xh, -yh, -zh, -xh, yh, zh, xh, yh, -zh, -xh, yh, -zh, xh, yh, zh, xh, -yh, -zh, xh, yh, -zh, xh, -yh, zh, -xh, -yh, -zh, xh, -yh, -zh, xh, yh, -zh, -xh, -yh, -zh, -xh, yh, -zh, -xh, yh, zh, xh, -yh, zh, xh, yh, zh, -xh, -yh, zh, -xh, yh, zh, -xh, yh, -zh, -xh, yh, zh, xh, yh, zh, xh, yh, -zh, xh, yh, zh, xh, -yh, zh, xh, -yh, -zh, xh, -yh, zh, -xh, -yh, zh, -xh, -yh, -zh, xh, yh, -zh, xh, -yh, -zh, -xh, -yh, -zh, -xh, yh, zh, -xh, -yh, zh, xh, -yh, zh};
        float[] uvs = {0.625f, 1.0f, 0.375f, 0.75f, 0.375f, 1.0f, 0.625f, 0.75f, 0.375f, 0.5f, 0.375f, 0.75f, 0.625f, 0.5f, 0.375f, 0.25f, 0.375f, 0.5f, 0.625f, 0.25f, 0.375f, 0.0f, 0.375f, 0.25f, 0.375f, 0.5f, 0.125f, 0.25f, 0.125f, 0.5f, 0.875f, 0.5f, 0.625f, 0.25f, 0.625f, 0.5f, 0.625f, 1.0f, 0.625f, 0.75f, 0.375f, 0.75f, 0.625f, 0.75f, 0.625f, 0.5f, 0.375f, 0.5f, 0.625f, 0.5f, 0.625f, 0.25f, 0.375f, 0.25f, 0.625f, 0.25f, 0.625f, 0.0f, 0.375f, 0.0f, 0.375f, 0.5f, 0.375f, 0.25f, 0.125f, 0.25f, 0.875f, 0.5f, 0.875f, 0.25f, 0.625f, 0.25f};
        float[] normals = {-1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f};
        float[] tangents = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f};
        float[] biTangents = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f};
        int[] indices = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};

        return new ModelMesh(positions, uvs, null, normals, tangents, biTangents, indices, radius);
    }

    public static ModelMesh createCubeInverted(float sizeX, float sizeY, float sizeZ) {
        float xh = sizeX / 2;
        float yh = sizeY / 2;
        float zh = sizeZ / 2;

        float radius = (float) Math.sqrt(xh * xh + yh * yh + zh * zh);

        float[] positions = {-xh, yh, -zh, -xh, -yh, zh, -xh, -yh, -zh, xh, yh, -zh, -xh, yh, zh, -xh, yh, -zh, xh, -yh, -zh, xh, yh, zh, xh, yh, -zh, -xh, -yh, -zh, xh, -yh, zh, xh, -yh, -zh, -xh, -yh, -zh, xh, yh, -zh, -xh, yh, -zh, xh, -yh, zh, -xh, yh, zh, xh, yh, zh, -xh, yh, -zh, -xh, yh, zh, -xh, -yh, zh, xh, yh, -zh, xh, yh, zh, -xh, yh, zh, xh, -yh, -zh, xh, -yh, zh, xh, yh, zh, -xh, -yh, -zh, -xh, -yh, zh, xh, -yh, zh, -xh, -yh, -zh, xh, -yh, -zh, xh, yh, -zh, xh, -yh, zh, -xh, -yh, zh, -xh, yh, zh};
        float[] uvs = {0.375f, 0.75f, 0.625f, 1.0f, 0.375f, 1.0f, 0.375f, 0.5f, 0.625f, 0.75f, 0.375f, 0.75f, 0.375f, 0.25f, 0.625f, 0.5f, 0.375f, 0.5f, 0.375f, 0.0f, 0.625f, 0.25f, 0.375f, 0.25f, 0.125f, 0.25f, 0.375f, 0.5f, 0.125f, 0.5f, 0.625f, 0.25f, 0.875f, 0.5f, 0.625f, 0.5f, 0.375f, 0.75f, 0.625f, 0.75f, 0.625f, 1.0f, 0.375f, 0.5f, 0.625f, 0.5f, 0.625f, 0.75f, 0.375f, 0.25f, 0.625f, 0.25f, 0.625f, 0.5f, 0.375f, 0.0f, 0.625f, 0.0f, 0.625f, 0.25f, 0.125f, 0.25f, 0.375f, 0.25f, 0.375f, 0.5f, 0.625f, 0.25f, 0.875f, 0.25f, 0.875f, 0.5f};
        float[] normals = {1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f};
        float[] tangents = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f};
        float[] biTangents = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f};
        int[] indices = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};

        return new ModelMesh(positions, uvs, null, normals, tangents, biTangents, indices, radius);
    }

}