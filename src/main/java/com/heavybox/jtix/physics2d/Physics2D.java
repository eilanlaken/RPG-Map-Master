package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public final class Physics2D {

    private Physics2D() {}

    public static float calculateMomentOfInertia(Body2DCollider collider) {

        if (collider instanceof Body2DColliderCircle) {
            float mass = collider.density * collider.area();
            Body2DColliderCircle circle = (Body2DColliderCircle) collider;
            return 0.5f * mass * circle.r * circle.r;
        }

        if (collider instanceof Body2DColliderRectangle) {
            Body2DColliderRectangle rectangle = (Body2DColliderRectangle) collider;
            float mass = collider.density * collider.area();
            float w = rectangle.width;
            float h = rectangle.height;
            return (1f / 12.0f) * mass * (w * w + h * h);
        }

        if (collider instanceof Body2DColliderPolygon) {
            Body2DColliderPolygon polygon = (Body2DColliderPolygon) collider;

            float[] vertices = polygon.vertices.items;
            int[] indices = polygon.indices.items;
            int triangles = indices.length / 3;

            ArrayFloat masses = new ArrayFloat(true, triangles);
            ArrayFloat Is = new ArrayFloat(true, triangles);
            Array<Vector2> centroids = new Array<>(true, triangles);

            Vector2 combinedCentroid = new Vector2();
            float totalMass = 0;

            for (int i = 0; i < indices.length; i += 3) {
                // Get the indices of the current triangle's vertices
                int indexA = indices[i] * 2;
                int indexB = indices[i + 1] * 2;
                int indexC = indices[i + 2] * 2;
                // Extract the vertices of the current triangle
                float ax = Collections.getCyclic(vertices, indexA);
                float ay = Collections.getCyclic(vertices,indexA + 1);
                float bx = Collections.getCyclic(vertices, indexB);
                float by = Collections.getCyclic(vertices,indexB + 1);
                float cx = Collections.getCyclic(vertices, indexC);
                float cy = Collections.getCyclic(vertices,indexC + 1);
                // calculate mass of the triangle
                float tri_mass = MathUtils.getAreaTriangle(ax, ay, bx, by, cx, cy) * collider.density;
                totalMass += tri_mass;
                masses.add(tri_mass);
                // calculate local centroid of the triangle
                float centerX = (ax + bx + cx) / 3.0f;
                float centerY = (ay + by + cy) / 3.0f;
                centroids.add(new Vector2(centerX, centerY));
                combinedCentroid.x += tri_mass * centerX;
                combinedCentroid.y += tri_mass * centerY;
                // calculate moment of inertia of the triangle with respect to its centroid
                Vector2 a = new Vector2(ax - centerX, ay - centerY);
                Vector2 b = new Vector2(bx - centerX, by - centerY);
                Vector2 c = new Vector2(cx - centerX, cy - centerY);
                float aa = Vector2.dot(a, a);
                float bb = Vector2.dot(b, b);
                float cc = Vector2.dot(c, c);
                float ab = Vector2.dot(a, b);
                float bc = Vector2.dot(b, c);
                float ca = Vector2.dot(c, a);
                float tri_I = (aa + bb + cc + ab + bc + ca) * tri_mass / 6f;
                Is.add(tri_I);
            }

            combinedCentroid.scl(1.0f / totalMass);

            float I = 0;
            for (int i = 0; i < triangles; ++i) {
                I += Is.get(i) + masses.get(i) * (Vector2.dst2(centroids.get(i), combinedCentroid));
            }

            return I;
        }

        return 0;
    }

    /**
     * Returns the relative angle between the two bodies given the reference angle.
     * @return double
     */
    public static float getRelativeRotationRad(Body2D body_1, Body2D body_2, float referenceAngleRad) {
        float rr = (body_1.radians - body_2.radians) - referenceAngleRad;
        if (rr < -MathUtils.PI) rr += MathUtils.PI_TWO;
        if (rr >  MathUtils.PI) rr -= MathUtils.PI_TWO;
        return rr;
    }

}
