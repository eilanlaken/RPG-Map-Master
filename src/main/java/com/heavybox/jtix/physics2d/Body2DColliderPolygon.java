package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;

public final class Body2DColliderPolygon extends Body2DCollider {

    public final int            vertexCount;
    public final ArrayFloat     vertices;
    public final ArrayInt       indices;
    public final Array<Vector2> worldVertices;


    public Body2DColliderPolygon(Data data, float[] polygon) {
        this(data.density, data.staticFriction, data.dynamicFriction, data.restitution, data.ghost, data.bitmask, polygon);
    }

    public Body2DColliderPolygon(float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask,
                                 float[] polygon) throws RuntimeException {
        super(density, staticFriction, dynamicFriction, restitution, ghost, bitmask, 0, 0, 0);
        this.vertices = new ArrayFloat(true, polygon.length);
        this.indices = new ArrayInt(true, (polygon.length - 2) * 3);
        MathUtils.polygonTriangulate(polygon, vertices, indices);
        vertices.pack();
        indices.pack();
        vertexCount = vertices.size / 2;
        this.worldVertices = new Array<>(true, vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            this.worldVertices.add(new Vector2());
        }
    }

    @Override
    protected float calculateBoundingRadius() {
        float max = 0;
        for (int i = 0; i < vertices.size - 1; i += 2) {
            float l2 = vertices.get(i) * vertices.get(i) + vertices.get(i+1) * vertices.get(i+1);
            if (l2 > max) max = l2;
        }
        return (float) Math.sqrt(max);
    }

    @Override
    protected float calculateArea() {
        float sum = 0;
        for (int i = 0; i < indices.size - 2; i += 3) {
            int   v1 = indices.get(i);
            float x1 = vertices.get(v1);
            float y1 = vertices.get(v1 + 1);

            int   v2 = indices.get(i + 1);
            float x2 = vertices.get(v2);
            float y2 = vertices.get(v2 + 1);

            int   v3 = indices.get(i + 2);
            float x3 = vertices.get(v3);
            float y3 = vertices.get(v3 + 1);

            sum += MathUtils.areaTriangle(x1, y1, x2, y2, x3, y3);
        }
        return sum;
    }

    @Override
    protected void update() {
        for (int i = 0; i < vertexCount; i++) {
            worldVertices.get(i)
                    .set(vertices.get(i * 2), vertices.get(i * 2 + 1))
                    .rotateAroundRad(body.local_cmX, body.local_cmY, body.radians)
                    .add(body.x, body.y);
        }
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        boolean inside = false;
        Vector2 tail = new Vector2();
        Vector2 head = new Vector2();
        for (int i = 0; i < worldVertices.size; i++) {
            getWorldEdge(i, tail, head);
            float x1 = tail.x;
            float y1 = tail.y;
            float x2 = head.x;
            float y2 = head.y;
            if ( ((y1 > y) != (y2 > y)) )
                if (x < (x2 - x1) * (y - y1) / (y2 - y1) + x1) inside = !inside;
        }
        return inside;
    }

    public void getWorldEdge(int index, @NotNull Vector2 tail, @NotNull Vector2 head) {
        int next = (index + 1) % vertexCount;
        tail.set(worldVertices.getCyclic(index));
        head.set(worldVertices.getCyclic(next));
    }

    public Array<Vector2> worldVertices() {
        return worldVertices;
    }

    @Override
    Vector2 calculateLocalCenter() {
        Vector2 local_center = new Vector2();
        for (int i = 0; i < vertices.size - 1; i += 2) {
            local_center.x += vertices.get(i);
            local_center.y += vertices.get(i+1);
        }
        return local_center.scl(1.0f / vertexCount);
    }

}