package com.heavybox.jtix.math;

/*
<pre>
    A column major matrix:

    M00 M01 M02 M03
    M10 M11 M12 M13
    M20 M21 M22 M23
    M30 M31 M32 M33

</pre>
*/

import com.heavybox.jtix.memory.MemoryPool;

public class Matrix4x4 implements MemoryPool.Reset {

    /** XX: Typically the unrotated X component for scaling, also the cosine of the angle when rotated on the Y and/or Z axis. On
     * Vector3 multiplication this value is multiplied with the source X component and added to the target X component. */
    public static final int M00 = 0;
    /** XY: Typically the negative sine of the angle when rotated on the Z axis. On Vector3 multiplication this value is multiplied
     * with the source Y component and added to the target X component. */
    public static final int M01 = 4;
    /** XZ: Typically the sine of the angle when rotated on the Y axis. On Vector3 multiplication this value is multiplied with the
     * source Z component and added to the target X component. */
    public static final int M02 = 8;
    /** XW: Typically the translation of the X component. On Vector3 multiplication this value is added to the target X
     * component. */
    public static final int M03 = 12;
    /** YX: Typically the sine of the angle when rotated on the Z axis. On Vector3 multiplication this value is multiplied with the
     * source X component and added to the target Y component. */
    public static final int M10 = 1;
    /** YY: Typically the unrotated Y component for scaling, also the cosine of the angle when rotated on the X and/or Z axis. On
     * Vector3 multiplication this value is multiplied with the source Y component and added to the target Y component. */
    public static final int M11 = 5;
    /** YZ: Typically the negative sine of the angle when rotated on the X axis. On Vector3 multiplication this value is multiplied
     * with the source Z component and added to the target Y component. */
    public static final int M12 = 9;
    /** YW: Typically the translation of the Y component. On Vector3 multiplication this value is added to the target Y
     * component. */
    public static final int M13 = 13;
    /** ZX: Typically the negative sine of the angle when rotated on the Y axis. On Vector3 multiplication this value is multiplied
     * with the source X component and added to the target Z component. */
    public static final int M20 = 2;
    /** ZY: Typical the sine of the angle when rotated on the X axis. On Vector3 multiplication this value is multiplied with the
     * source Y component and added to the target Z component. */
    public static final int M21 = 6;
    /** ZZ: Typically the unrotated Z component for scaling, also the cosine of the angle when rotated on the X and/or Y axis. On
     * Vector3 multiplication this value is multiplied with the source Z component and added to the target Z component. */
    public static final int M22 = 10;
    /** ZW: Typically the translation of the Z component. On Vector3 multiplication this value is added to the target Z
     * component. */
    public static final int M23 = 14;
    /** WX: Typically the value zero. On Vector3 multiplication this value is ignored. */
    public static final int M30 = 3;
    /** WY: Typically the value zero. On Vector3 multiplication this value is ignored. */
    public static final int M31 = 7;
    /** WZ: Typically the value zero. On Vector3 multiplication this value is ignored. */
    public static final int M32 = 11;
    /** WW: Typically the value one. On Vector3 multiplication this value is ignored. */
    public static final int M33 = 15;

    protected static final Matrix4x4 rotation = new Matrix4x4();

    private static final Quaternion quaternion  = new Quaternion();
    private static final Quaternion quaternion2 = new Quaternion();
    private static final Vector3    l_vez       = new Vector3();
    private static final Vector3    l_vex       = new Vector3();
    private static final Vector3    l_vey       = new Vector3();
    private static final Vector3    tmpVec      = new Vector3();
    private static final Matrix4x4  tmpMat      = new Matrix4x4();
    private static final Vector3    right       = new Vector3();
    private static final Vector3    tmpForward  = new Vector3();
    private static final Vector3    tmpUp       = new Vector3();
    private static final Vector3    currentX    = new Vector3();
    private static final Vector3    currentY    = new Vector3();
    private static final Vector3    newX        = new Vector3();
    private static final Vector3    newY        = new Vector3();

    public final float[] val = new float[16];

    /** Constructs an identity matrix */
    public Matrix4x4() {
        val[M00] = 1f;
        val[M11] = 1f;
        val[M22] = 1f;
        val[M33] = 1f;
    }

    /** Constructs a matrix from the given matrix.
     * @param matrix The matrix to copy. (This matrix is not modified) */
    public Matrix4x4(Matrix4x4 matrix) {
        set(matrix);
    }

    /** Constructs a matrix from the given float array. The array must have at least 16 elements; the first 16 will be copied.
     * @param values The float array to copy. Remember that this matrix is in
     *           <a href="http://en.wikipedia.org/wiki/Row-major_order">column major</a> order. (The float array is not
     *           modified) */
    public Matrix4x4(float[] values) {
        set(values);
    }

    /** Constructs a rotation matrix from the given {@link Quaternion}.
     * @param quaternion The quaternion to be copied. (The quaternion is not modified) */
    public Matrix4x4(Quaternion quaternion) {
        setToTranslationRotationScaling(quaternion);
    }

    /** Construct a matrix from the given translation, rotation and scale.
     * @param translation The translation
     * @param rotation The rotation, must be normalized
     * @param scale The scale */
    public Matrix4x4(Vector3 translation, Quaternion rotation, Vector3 scale) {
        setToTranslationRotationScaling(translation, rotation, scale);
    }

    /** Sets the matrix to the given matrix.
     * @param matrix The matrix that is to be copied. (The given matrix is not modified)
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 set(Matrix4x4 matrix) {
        return set(matrix.val);
    }

    /** Sets the matrix to the given matrix as a float array. The float array must have at least 16 elements; the first 16 will be
     * copied.
     *
     * @param values The matrix, in float form, that is to be copied. Remember that this matrix is in
     *           <a href="http://en.wikipedia.org/wiki/Row-major_order">column major</a> order.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 set(float[] values) {
        System.arraycopy(values, 0, val, 0, val.length);
        return this;
    }

    /** Sets the matrix to a rotation matrix representing the quaternion.
     * @param quaternion The quaternion that is to be used to set this matrix.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToTranslationRotationScaling(Quaternion quaternion) {
        return set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }

    /** Sets the matrix to a rotation matrix representing the quaternion.
     *
     * @param quaternionX The X component of the quaternion that is to be used to set this matrix.
     * @param quaternionY The Y component of the quaternion that is to be used to set this matrix.
     * @param quaternionZ The Z component of the quaternion that is to be used to set this matrix.
     * @param quaternionW The W component of the quaternion that is to be used to set this matrix.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 set(float quaternionX, float quaternionY, float quaternionZ, float quaternionW) {
        return set(0f, 0f, 0f, quaternionX, quaternionY, quaternionZ, quaternionW);
    }

    /** Set this matrix to the specified translation and rotation.
     * @param translation The translation
     * @param orientation The rotation, must be normalized
     * @return This matrix for chaining */
    public Matrix4x4 set(Vector3 translation, Quaternion orientation) {
        return set(translation.x, translation.y, translation.z, orientation.x, orientation.y, orientation.z, orientation.w);
    }

    /** Sets the matrix to a rotation matrix representing the translation and quaternion.
     * @param translationX The X component of the translation that is to be used to set this matrix.
     * @param translationY The Y component of the translation that is to be used to set this matrix.
     * @param translationZ The Z component of the translation that is to be used to set this matrix.
     * @param quaternionX The X component of the quaternion that is to be used to set this matrix.
     * @param quaternionY The Y component of the quaternion that is to be used to set this matrix.
     * @param quaternionZ The Z component of the quaternion that is to be used to set this matrix.
     * @param quaternionW The W component of the quaternion that is to be used to set this matrix.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 set(float translationX, float translationY, float translationZ, float quaternionX, float quaternionY,
                         float quaternionZ, float quaternionW) {
        final float xs = quaternionX * 2f, ys = quaternionY * 2f, zs = quaternionZ * 2f;
        final float wx = quaternionW * xs, wy = quaternionW * ys, wz = quaternionW * zs;
        final float xx = quaternionX * xs, xy = quaternionX * ys, xz = quaternionX * zs;
        final float yy = quaternionY * ys, yz = quaternionY * zs, zz = quaternionZ * zs;

        val[M00] = 1f - (yy + zz);
        val[M01] = xy - wz;
        val[M02] = xz + wy;
        val[M03] = translationX;

        val[M10] = xy + wz;
        val[M11] = 1f - (xx + zz);
        val[M12] = yz - wx;
        val[M13] = translationY;

        val[M20] = xz - wy;
        val[M21] = yz + wx;
        val[M22] = 1f - (xx + yy);
        val[M23] = translationZ;

        val[M30] = 0f;
        val[M31] = 0f;
        val[M32] = 0f;
        val[M33] = 1f;
        return this;
    }

    /** Set this matrix to the specified translation, rotation and scale.
     * @param translation The translation
     * @param orientation The rotation, must be normalized
     * @param scale The scale
     * @return This matrix for chaining */
    public Matrix4x4 setToTranslationRotationScaling(Vector3 translation, Quaternion orientation, Vector3 scale) {
        return setToTranslationRotationScaling(translation.x, translation.y, translation.z, orientation.x, orientation.y, orientation.z, orientation.w, scale.x, scale.y,
                scale.z);
    }

    public Matrix4x4 setToTranslationEulerScaling(float x, float y, float z, float degX, float degY, float degZ, float sclX, float sclY, float sclZ) {
        quaternion.setEulerAnglesDeg(degX, degY, degZ);
        return setToTranslationRotationScaling(x, y, z, quaternion.x, quaternion.y, quaternion.z, quaternion.w, sclX, sclY, sclZ);
    }

    /** Sets the matrix to a rotation matrix representing the translation and quaternion.
     * @param translationX The X component of the translation that is to be used to set this matrix.
     * @param translationY The Y component of the translation that is to be used to set this matrix.
     * @param translationZ The Z component of the translation that is to be used to set this matrix.
     * @param quaternionX The X component of the quaternion that is to be used to set this matrix.
     * @param quaternionY The Y component of the quaternion that is to be used to set this matrix.
     * @param quaternionZ The Z component of the quaternion that is to be used to set this matrix.
     * @param quaternionW The W component of the quaternion that is to be used to set this matrix.
     * @param scaleX The X component of the scaling that is to be used to set this matrix.
     * @param scaleY The Y component of the scaling that is to be used to set this matrix.
     * @param scaleZ The Z component of the scaling that is to be used to set this matrix.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToTranslationRotationScaling(float translationX, float translationY, float translationZ, float quaternionX, float quaternionY,
                                                     float quaternionZ, float quaternionW, float scaleX, float scaleY, float scaleZ) {
        final float xs = quaternionX * 2f, ys = quaternionY * 2f, zs = quaternionZ * 2f;
        final float wx = quaternionW * xs, wy = quaternionW * ys, wz = quaternionW * zs;
        final float xx = quaternionX * xs, xy = quaternionX * ys, xz = quaternionX * zs;
        final float yy = quaternionY * ys, yz = quaternionY * zs, zz = quaternionZ * zs;

        val[M00] = scaleX * (1.0f - (yy + zz));
        val[M01] = scaleY * (xy - wz);
        val[M02] = scaleZ * (xz + wy);
        val[M03] = translationX;

        val[M10] = scaleX * (xy + wz);
        val[M11] = scaleY * (1.0f - (xx + zz));
        val[M12] = scaleZ * (yz - wx);
        val[M13] = translationY;

        val[M20] = scaleX * (xz - wy);
        val[M21] = scaleY * (yz + wx);
        val[M22] = scaleZ * (1.0f - (xx + yy));
        val[M23] = translationZ;

        val[M30] = 0f;
        val[M31] = 0f;
        val[M32] = 0f;
        val[M33] = 1f;
        return this;
    }

    /** Sets the four columns of the matrix which correspond to the x-, y- and z-axis of the vector space this matrix creates as
     * well as the 4th column representing the translation of any point that is multiplied by this matrix.
     * @param xAxis The x-axis.
     * @param yAxis The y-axis.
     * @param zAxis The z-axis.
     * @param pos The translation vector. */
    public Matrix4x4 setToTranslationRotationScaling(Vector3 xAxis, Vector3 yAxis, Vector3 zAxis, Vector3 pos) {
        val[M00] = xAxis.x;
        val[M01] = xAxis.y;
        val[M02] = xAxis.z;
        val[M10] = yAxis.x;
        val[M11] = yAxis.y;
        val[M12] = yAxis.z;
        val[M20] = zAxis.x;
        val[M21] = zAxis.y;
        val[M22] = zAxis.z;
        val[M03] = pos.x;
        val[M13] = pos.y;
        val[M23] = pos.z;
        val[M30] = 0f;
        val[M31] = 0f;
        val[M32] = 0f;
        val[M33] = 1f;
        return this;
    }

    /** @return a copy of this matrix */
    public Matrix4x4 cpy() {
        return new Matrix4x4(this);
    }

    /** Adds a translational component to the matrix in the 4th column. The other columns are untouched.
     * @param vector The translation vector to add to the current matrix. (This vector is not modified)
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 trn(Vector3 vector) {
        val[M03] += vector.x;
        val[M13] += vector.y;
        val[M23] += vector.z;
        return this;
    }

    /** Adds a translational component to the matrix in the 4th column. The other columns are untouched.
     * @param x The x-component of the translation vector.
     * @param y The y-component of the translation vector.
     * @param z The z-component of the translation vector.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 trn(float x, float y, float z) {
        val[M03] += x;
        val[M13] += y;
        val[M23] += z;
        return this;
    }

    /** @return the backing float array */
    public float[] getValues() {
        return val;
    }

    /** Postmultiplies this matrix with the given matrix, storing the result in this matrix. For example:
     *
     * <pre>
     * A.mul(B) results in A := AB.
     * </pre>
     *
     * @param matrix The other matrix to multiply by.
     * @return This matrix for the purpose of chaining operations together. */
    public Matrix4x4 mul(Matrix4x4 matrix) {
        mul(val, matrix.val);
        return this;
    }

    /** Premultiplies this matrix with the given matrix, storing the result in this matrix. For example:
     *
     * <pre>
     * A.mulLeft(B) results in A := BA.
     * </pre>
     *
     * @param matrix The other matrix to multiply by.
     * @return This matrix for the purpose of chaining operations together. */
    public Matrix4x4 mulLeft(Matrix4x4 matrix) {
        tmpMat.set(matrix);
        mul(tmpMat.val, val);
        return set(tmpMat);
    }

    /** Transposes the matrix.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 tra() {
        float m01 = val[M01];
        float m02 = val[M02];
        float m03 = val[M03];
        float m12 = val[M12];
        float m13 = val[M13];
        float m23 = val[M23];
        val[M01] = val[M10];
        val[M02] = val[M20];
        val[M03] = val[M30];
        val[M10] = m01;
        val[M12] = val[M21];
        val[M13] = val[M31];
        val[M20] = m02;
        val[M21] = m12;
        val[M23] = val[M32];
        val[M30] = m03;
        val[M31] = m13;
        val[M32] = m23;
        return this;
    }

    /** Sets the matrix to an identity matrix.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 idt() {
        val[M00] = 1f;
        val[M01] = 0f;
        val[M02] = 0f;
        val[M03] = 0f;
        val[M10] = 0f;
        val[M11] = 1f;
        val[M12] = 0f;
        val[M13] = 0f;
        val[M20] = 0f;
        val[M21] = 0f;
        val[M22] = 1f;
        val[M23] = 0f;
        val[M30] = 0f;
        val[M31] = 0f;
        val[M32] = 0f;
        val[M33] = 1f;
        return this;
    }

    /** Inverts the matrix. Stores the result in this matrix.
     * @return This matrix for the purpose of chaining methods together.
     * @throws RuntimeException if the matrix is singular (not invertible) */
    public Matrix4x4 inv() {
        float l_det = val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31] * val[M12] * val[M03]
                - val[M30] * val[M11] * val[M22] * val[M03] + val[M10] * val[M31] * val[M22] * val[M03]
                + val[M20] * val[M11] * val[M32] * val[M03] - val[M10] * val[M21] * val[M32] * val[M03]
                - val[M30] * val[M21] * val[M02] * val[M13] + val[M20] * val[M31] * val[M02] * val[M13]
                + val[M30] * val[M01] * val[M22] * val[M13] - val[M00] * val[M31] * val[M22] * val[M13]
                - val[M20] * val[M01] * val[M32] * val[M13] + val[M00] * val[M21] * val[M32] * val[M13]
                + val[M30] * val[M11] * val[M02] * val[M23] - val[M10] * val[M31] * val[M02] * val[M23]
                - val[M30] * val[M01] * val[M12] * val[M23] + val[M00] * val[M31] * val[M12] * val[M23]
                + val[M10] * val[M01] * val[M32] * val[M23] - val[M00] * val[M11] * val[M32] * val[M23]
                - val[M20] * val[M11] * val[M02] * val[M33] + val[M10] * val[M21] * val[M02] * val[M33]
                + val[M20] * val[M01] * val[M12] * val[M33] - val[M00] * val[M21] * val[M12] * val[M33]
                - val[M10] * val[M01] * val[M22] * val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
        if (l_det == 0f) throw new RuntimeException("non-invertible matrix");
        float m00 = val[M12] * val[M23] * val[M31] - val[M13] * val[M22] * val[M31] + val[M13] * val[M21] * val[M32]
                - val[M11] * val[M23] * val[M32] - val[M12] * val[M21] * val[M33] + val[M11] * val[M22] * val[M33];
        float m01 = val[M03] * val[M22] * val[M31] - val[M02] * val[M23] * val[M31] - val[M03] * val[M21] * val[M32]
                + val[M01] * val[M23] * val[M32] + val[M02] * val[M21] * val[M33] - val[M01] * val[M22] * val[M33];
        float m02 = val[M02] * val[M13] * val[M31] - val[M03] * val[M12] * val[M31] + val[M03] * val[M11] * val[M32]
                - val[M01] * val[M13] * val[M32] - val[M02] * val[M11] * val[M33] + val[M01] * val[M12] * val[M33];
        float m03 = val[M03] * val[M12] * val[M21] - val[M02] * val[M13] * val[M21] - val[M03] * val[M11] * val[M22]
                + val[M01] * val[M13] * val[M22] + val[M02] * val[M11] * val[M23] - val[M01] * val[M12] * val[M23];
        float m10 = val[M13] * val[M22] * val[M30] - val[M12] * val[M23] * val[M30] - val[M13] * val[M20] * val[M32]
                + val[M10] * val[M23] * val[M32] + val[M12] * val[M20] * val[M33] - val[M10] * val[M22] * val[M33];
        float m11 = val[M02] * val[M23] * val[M30] - val[M03] * val[M22] * val[M30] + val[M03] * val[M20] * val[M32]
                - val[M00] * val[M23] * val[M32] - val[M02] * val[M20] * val[M33] + val[M00] * val[M22] * val[M33];
        float m12 = val[M03] * val[M12] * val[M30] - val[M02] * val[M13] * val[M30] - val[M03] * val[M10] * val[M32]
                + val[M00] * val[M13] * val[M32] + val[M02] * val[M10] * val[M33] - val[M00] * val[M12] * val[M33];
        float m13 = val[M02] * val[M13] * val[M20] - val[M03] * val[M12] * val[M20] + val[M03] * val[M10] * val[M22]
                - val[M00] * val[M13] * val[M22] - val[M02] * val[M10] * val[M23] + val[M00] * val[M12] * val[M23];
        float m20 = val[M11] * val[M23] * val[M30] - val[M13] * val[M21] * val[M30] + val[M13] * val[M20] * val[M31]
                - val[M10] * val[M23] * val[M31] - val[M11] * val[M20] * val[M33] + val[M10] * val[M21] * val[M33];
        float m21 = val[M03] * val[M21] * val[M30] - val[M01] * val[M23] * val[M30] - val[M03] * val[M20] * val[M31]
                + val[M00] * val[M23] * val[M31] + val[M01] * val[M20] * val[M33] - val[M00] * val[M21] * val[M33];
        float m22 = val[M01] * val[M13] * val[M30] - val[M03] * val[M11] * val[M30] + val[M03] * val[M10] * val[M31]
                - val[M00] * val[M13] * val[M31] - val[M01] * val[M10] * val[M33] + val[M00] * val[M11] * val[M33];
        float m23 = val[M03] * val[M11] * val[M20] - val[M01] * val[M13] * val[M20] - val[M03] * val[M10] * val[M21]
                + val[M00] * val[M13] * val[M21] + val[M01] * val[M10] * val[M23] - val[M00] * val[M11] * val[M23];
        float m30 = val[M12] * val[M21] * val[M30] - val[M11] * val[M22] * val[M30] - val[M12] * val[M20] * val[M31]
                + val[M10] * val[M22] * val[M31] + val[M11] * val[M20] * val[M32] - val[M10] * val[M21] * val[M32];
        float m31 = val[M01] * val[M22] * val[M30] - val[M02] * val[M21] * val[M30] + val[M02] * val[M20] * val[M31]
                - val[M00] * val[M22] * val[M31] - val[M01] * val[M20] * val[M32] + val[M00] * val[M21] * val[M32];
        float m32 = val[M02] * val[M11] * val[M30] - val[M01] * val[M12] * val[M30] - val[M02] * val[M10] * val[M31]
                + val[M00] * val[M12] * val[M31] + val[M01] * val[M10] * val[M32] - val[M00] * val[M11] * val[M32];
        float m33 = val[M01] * val[M12] * val[M20] - val[M02] * val[M11] * val[M20] + val[M02] * val[M10] * val[M21]
                - val[M00] * val[M12] * val[M21] - val[M01] * val[M10] * val[M22] + val[M00] * val[M11] * val[M22];
        float inv_det = 1.0f / l_det;
        val[M00] = m00 * inv_det;
        val[M10] = m10 * inv_det;
        val[M20] = m20 * inv_det;
        val[M30] = m30 * inv_det;
        val[M01] = m01 * inv_det;
        val[M11] = m11 * inv_det;
        val[M21] = m21 * inv_det;
        val[M31] = m31 * inv_det;
        val[M02] = m02 * inv_det;
        val[M12] = m12 * inv_det;
        val[M22] = m22 * inv_det;
        val[M32] = m32 * inv_det;
        val[M03] = m03 * inv_det;
        val[M13] = m13 * inv_det;
        val[M23] = m23 * inv_det;
        val[M33] = m33 * inv_det;
        return this;
    }

    /** @return The determinant of this matrix */
    public float det() {
        return val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31] * val[M12] * val[M03]
                - val[M30] * val[M11] * val[M22] * val[M03] + val[M10] * val[M31] * val[M22] * val[M03]
                + val[M20] * val[M11] * val[M32] * val[M03] - val[M10] * val[M21] * val[M32] * val[M03]
                - val[M30] * val[M21] * val[M02] * val[M13] + val[M20] * val[M31] * val[M02] * val[M13]
                + val[M30] * val[M01] * val[M22] * val[M13] - val[M00] * val[M31] * val[M22] * val[M13]
                - val[M20] * val[M01] * val[M32] * val[M13] + val[M00] * val[M21] * val[M32] * val[M13]
                + val[M30] * val[M11] * val[M02] * val[M23] - val[M10] * val[M31] * val[M02] * val[M23]
                - val[M30] * val[M01] * val[M12] * val[M23] + val[M00] * val[M31] * val[M12] * val[M23]
                + val[M10] * val[M01] * val[M32] * val[M23] - val[M00] * val[M11] * val[M32] * val[M23]
                - val[M20] * val[M11] * val[M02] * val[M33] + val[M10] * val[M21] * val[M02] * val[M33]
                + val[M20] * val[M01] * val[M12] * val[M33] - val[M00] * val[M21] * val[M12] * val[M33]
                - val[M10] * val[M01] * val[M22] * val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
    }

    /** @return The determinant of the 3x3 upper left matrix */
    public float det3x3() {
        return val[M00] * val[M11] * val[M22] + val[M01] * val[M12] * val[M20] + val[M02] * val[M10] * val[M21]
                - val[M00] * val[M12] * val[M21] - val[M01] * val[M10] * val[M22] - val[M02] * val[M11] * val[M20];
    }

    /** Sets the matrix to a projection matrix with a near- and far plane, a field of view in degrees and an aspect ratio. Note
     * that the field of view specified is the angle in degrees for the height, the field of view for the width will be calculated
     * according to the aspect ratio.
     * @param near The near plane
     * @param far The far plane
     * @param verticalFov The field of view of the height in degrees
     * @param aspectRatio The "width over height" aspect ratio
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToPerspectiveProjection(float near, float far, float verticalFov, float aspectRatio) {
        idt();
        float l_fd = (float)(1.0 / Math.tan((verticalFov * (Math.PI / 180)) / 2.0));
        float l_a1 = (far + near) / (near - far);
        float l_a2 = (2 * far * near) / (near - far);
        val[M00] = l_fd / aspectRatio;
        val[M10] = 0;
        val[M20] = 0;
        val[M30] = 0;
        val[M01] = 0;
        val[M11] = l_fd;
        val[M21] = 0;
        val[M31] = 0;
        val[M02] = 0;
        val[M12] = 0;
        val[M22] = l_a1;
        val[M32] = -1;
        val[M03] = 0;
        val[M13] = 0;
        val[M23] = l_a2;
        val[M33] = 0;
        return this;
    }

    /** Sets the matrix to a projection matrix with a near/far plane, and left, bottom, right and top specifying the points on the
     * near plane that are mapped to the lower left and upper right corners of the viewport. This allows to create projection
     * matrix with off-center vanishing point.
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near The near plane
     * @param far The far plane
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToPerspectiveProjection(float left, float right, float bottom, float top, float near, float far) {
        float x = 2.0f * near / (right - left);
        float y = 2.0f * near / (top - bottom);
        float a = (right + left) / (right - left);
        float b = (top + bottom) / (top - bottom);
        float l_a1 = (far + near) / (near - far);
        float l_a2 = (2 * far * near) / (near - far);
        val[M00] = x;
        val[M10] = 0;
        val[M20] = 0;
        val[M30] = 0;
        val[M01] = 0;
        val[M11] = y;
        val[M21] = 0;
        val[M31] = 0;
        val[M02] = a;
        val[M12] = b;
        val[M22] = l_a1;
        val[M32] = -1;
        val[M03] = 0;
        val[M13] = 0;
        val[M23] = l_a2;
        val[M33] = 0;
        return this;
    }

    /** Sets this matrix to an orthographic projection matrix with the origin at (x,y) extending by width and height. The near
     * plane is set to 0, the far plane is set to 1.
     * @param x The x-coordinate of the origin
     * @param y The y-coordinate of the origin
     * @param width The width
     * @param height The height
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToOrtho2D(float x, float y, float width, float height) {
        setToOrthographicProjection(x, x + width, y, y + height, 0, 1);
        return this;
    }

    /** Sets this matrix to an orthographic projection matrix with the origin at (x,y) extending by width and height, having a near
     * and far plane.
     * @param x The x-coordinate of the origin
     * @param y The y-coordinate of the origin
     * @param width The width
     * @param height The height
     * @param near The near plane
     * @param far The far plane
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToOrtho2D(float x, float y, float width, float height, float near, float far) {
        setToOrthographicProjection(x, x + width, y, y + height, near, far);
        return this;
    }

    /** Sets the matrix to an orthographic projection like glOrtho (http://www.opengl.org/sdk/docs/man/xhtml/glOrtho.xml) following
     * the OpenGL equivalent
     * @param left The left clipping plane
     * @param right The right clipping plane
     * @param bottom The bottom clipping plane
     * @param top The top clipping plane
     * @param near The near clipping plane
     * @param far The far clipping plane
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToOrthographicProjection(float left, float right, float bottom, float top, float near, float far) {
        float x_orth = 2 / (right - left);
        float y_orth = 2 / (top - bottom);
        float z_orth = -2 / (far - near);

        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        val[M00] = x_orth;
        val[M10] = 0;
        val[M20] = 0;
        val[M30] = 0;
        val[M01] = 0;
        val[M11] = y_orth;
        val[M21] = 0;
        val[M31] = 0;
        val[M02] = 0;
        val[M12] = 0;
        val[M22] = z_orth;
        val[M32] = 0;
        val[M03] = tx;
        val[M13] = ty;
        val[M23] = tz;
        val[M33] = 1;
        return this;
    }

    /** Sets the 4th column to the translation vector.
     * @param vector The translation vector
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setTranslation(Vector3 vector) {
        val[M03] = vector.x;
        val[M13] = vector.y;
        val[M23] = vector.z;
        return this;
    }

    /** Sets the 4th column to the translation vector.
     * @param x The X coordinate of the translation vector
     * @param y The Y coordinate of the translation vector
     * @param z The Z coordinate of the translation vector
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setTranslation(float x, float y, float z) {
        val[M03] = x;
        val[M13] = y;
        val[M23] = z;
        return this;
    }

    /** Sets this matrix to a translation matrix, overwriting it first by an identity matrix and then setting the 4th column to the
     * translation vector.
     * @param vector The translation vector
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToTranslation(Vector3 vector) {
        idt();
        val[M03] = vector.x;
        val[M13] = vector.y;
        val[M23] = vector.z;
        return this;
    }

    /** Sets this matrix to a translation matrix, overwriting it first by an identity matrix and then setting the 4th column to the
     * translation vector.
     * @param x The x-component of the translation vector.
     * @param y The y-component of the translation vector.
     * @param z The z-component of the translation vector.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToTranslation(float x, float y, float z) {
        idt();
        val[M03] = x;
        val[M13] = y;
        val[M23] = z;
        return this;
    }

    /** Sets this matrix to a translation and scaling matrix by first overwriting it with an identity and then setting the
     * translation vector in the 4th column and the scaling vector in the diagonal.
     * @param translation The translation vector
     * @param scaling The scaling vector
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToTranslationAndScaling(Vector3 translation, Vector3 scaling) {
        idt();
        val[M03] = translation.x;
        val[M13] = translation.y;
        val[M23] = translation.z;
        val[M00] = scaling.x;
        val[M11] = scaling.y;
        val[M22] = scaling.z;
        return this;
    }

    /** Sets this matrix to a translation and scaling matrix by first overwriting it with an identity and then setting the
     * translation vector in the 4th column and the scaling vector in the diagonal.
     * @param translationX The x-component of the translation vector
     * @param translationY The y-component of the translation vector
     * @param translationZ The z-component of the translation vector
     * @param scalingX The x-component of the scaling vector
     * @param scalingY The x-component of the scaling vector
     * @param scalingZ The x-component of the scaling vector
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToTranslationAndScaling(float translationX, float translationY, float translationZ, float scalingX,
                                                float scalingY, float scalingZ) {
        idt();
        val[M03] = translationX;
        val[M13] = translationY;
        val[M23] = translationZ;
        val[M00] = scalingX;
        val[M11] = scalingY;
        val[M22] = scalingZ;
        return this;
    }

    /** Sets the matrix to a rotation matrix around the given axis.
     * @param axis The axis
     * @param degrees The angle in degrees
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToRotation(Vector3 axis, float degrees) {
        if (degrees == 0) {
            idt();
            return this;
        }
        return setToTranslationRotationScaling(quaternion.setDeg(axis, degrees));
    }

    /** Sets the matrix to a rotation matrix around the given axis.
     * @param axis The axis
     * @param radians The angle in radians
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToRotationRad(Vector3 axis, float radians) {
        if (radians == 0) {
            idt();
            return this;
        }
        return setToTranslationRotationScaling(quaternion.setFromAxisRad(axis, radians));
    }

    /** Sets the matrix to a rotation matrix around the given axis.
     * @param axisX The x-component of the axis
     * @param axisY The y-component of the axis
     * @param axisZ The z-component of the axis
     * @param degrees The angle in degrees
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToRotation(float axisX, float axisY, float axisZ, float degrees) {
        if (degrees == 0) {
            idt();
            return this;
        }
        return setToTranslationRotationScaling(quaternion.setFromAxisDeg(axisX, axisY, axisZ, degrees));
    }

    /** Sets the matrix to a rotation matrix around the given axis.
     * @param axisX The x-component of the axis
     * @param axisY The y-component of the axis
     * @param axisZ The z-component of the axis
     * @param radians The angle in radians
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToRotationRad(float axisX, float axisY, float axisZ, float radians) {
        if (radians == 0) {
            idt();
            return this;
        }
        return setToTranslationRotationScaling(quaternion.setFromAxisRad(axisX, axisY, axisZ, radians));
    }

    /** Set the matrix to a rotation matrix between two vectors.
     * @param v1 The base vector
     * @param v2 The target vector
     * @return This matrix for the purpose of chaining methods together */
    public Matrix4x4 setToRotation(final Vector3 v1, final Vector3 v2) {
        return setToTranslationRotationScaling(quaternion.setFromSourceToTarget(v1, v2));
    }

    /** Set the matrix to a rotation matrix between two vectors.
     * @param x1 The base vectors x value
     * @param y1 The base vectors y value
     * @param z1 The base vectors z value
     * @param x2 The target vector x value
     * @param y2 The target vector y value
     * @param z2 The target vector z value
     * @return This matrix for the purpose of chaining methods together */
    public Matrix4x4 setToRotation(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        return setToTranslationRotationScaling(quaternion.setFromSourceToTarget(x1, y1, z1, x2, y2, z2));
    }

    /** Sets this matrix to a rotation matrix from the given euler angles.
     * @param yaw the yaw in degrees
     * @param pitch the pitch in degrees
     * @param roll the roll in degrees
     * @return This matrix */
    public Matrix4x4 setFromEulerAngles(float yaw, float pitch, float roll) {
        quaternion.setEulerAnglesDeg(yaw, pitch, roll);
        return setToTranslationRotationScaling(quaternion);
    }

    /** Sets this matrix to a rotation matrix from the given euler angles.
     * @param yaw the yaw in radians
     * @param pitch the pitch in radians
     * @param roll the roll in radians
     * @return This matrix */
    public Matrix4x4 setFromEulerAnglesRad(float yaw, float pitch, float roll) {
        quaternion.setEulerAnglesRad(yaw, pitch, roll);
        return setToTranslationRotationScaling(quaternion);
    }

    /** Sets this matrix to a scaling matrix
     * @param vector The scaling vector
     * @return This matrix for chaining. */
    public Matrix4x4 setToScaling(Vector3 vector) {
        idt();
        val[M00] = vector.x;
        val[M11] = vector.y;
        val[M22] = vector.z;
        return this;
    }

    /** Sets this matrix to a scaling matrix
     * @param x The x-component of the scaling vector
     * @param y The y-component of the scaling vector
     * @param z The z-component of the scaling vector
     * @return This matrix for chaining. */
    public Matrix4x4 setToScaling(float x, float y, float z) {
        idt();
        val[M00] = x;
        val[M11] = y;
        val[M22] = z;
        return this;
    }

    /** Sets the matrix to a look at matrix with a direction and an up vector. Multiply with a translation matrix to get a camera
     * model view matrix.
     * @param direction The direction vector
     * @param up The up vector
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 setToLookAt(Vector3 direction, Vector3 up) {
        l_vez.set(direction).nor();
        l_vex.set(direction).crs(up).nor();
        l_vey.set(l_vex).crs(l_vez).nor();
        idt();
        val[M00] = l_vex.x;
        val[M01] = l_vex.y;
        val[M02] = l_vex.z;
        val[M10] = l_vey.x;
        val[M11] = l_vey.y;
        val[M12] = l_vey.z;
        val[M20] = -l_vez.x;
        val[M21] = -l_vez.y;
        val[M22] = -l_vez.z;
        return this;
    }

    /** Sets this matrix to a look at matrix with the given position, target and up vector.
     * @param position the position
     * @param target the target
     * @param up the up vector
     * @return This matrix */
    public Matrix4x4 setToLookAt(Vector3 position, Vector3 target, Vector3 up) {
        tmpVec.set(target).sub(position);
        setToLookAt(tmpVec, up);
        mul(tmpMat.setToTranslation(-position.x, -position.y, -position.z));
        return this;
    }

    public Matrix4x4 setToWorld(Vector3 position, Vector3 forward, Vector3 up) {
        tmpForward.set(forward).nor();
        right.set(tmpForward).crs(up).nor();
        tmpUp.set(right).crs(tmpForward).nor();
        setToTranslationRotationScaling(right, tmpUp, tmpForward.scl(-1), position);
        return this;
    }

    /** Linearly interpolates between this matrix and the given matrix mixing by alpha
     * @param matrix the matrix
     * @param alpha the alpha value in the range [0,1]
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 lerp(Matrix4x4 matrix, float alpha) {
        for (int i = 0; i < 16; i++)
            val[i] = val[i] * (1 - alpha) + matrix.val[i] * alpha;
        return this;
    }

    public Matrix4x4 sclXYZ(Vector3 scale) {
        val[M00] *= scale.x;
        val[M11] *= scale.y;
        val[M22] *= scale.z;
        return this;
    }

    public Matrix4x4 sclXYZ(float x, float y, float z) {
        val[M00] *= x;
        val[M11] *= y;
        val[M22] *= z;
        return this;
    }

    public Matrix4x4 sclXYZ(float scale) {
        val[M00] *= scale;
        val[M11] *= scale;
        val[M22] *= scale;
        return this;
    }

    // TODO: understand or delete.
    public Matrix4x4 interpolate(Matrix4x4 target, float factor) {
        getScale(tmpVec);
        target.getScale(tmpForward);
        getRotation(quaternion);
        target.getRotation(quaternion2);
        getTranslation(tmpUp);
        target.getTranslation(right);
        setToScaling(tmpVec.scl(1-factor).add(tmpForward.scl(factor)));
        rotateLocalAxis(quaternion.slerp(quaternion2, factor));
        setTranslation(tmpUp.scl(1-factor).add(right.scl(factor)));
        return this;
    }

    public Matrix4x4 setToInterpolation(Matrix4x4 source, Matrix4x4 target, float factor) {
        source.getScale(tmpVec);
        target.getScale(tmpForward);
        source.getRotation(quaternion);
        target.getRotation(quaternion2);
        source.getTranslation(tmpUp);
        target.getTranslation(right);

        setToScaling(tmpVec.scl(1-factor).add(tmpForward.scl(factor)));
        rotateLocalAxis(quaternion.slerp(quaternion2, factor));
        setTranslation(tmpUp.scl(1-factor).add(right.scl(factor)));
        return this;
    }

    public static Matrix4x4 interpolation(final Matrix4x4 source, final Matrix4x4 target, final float factor, Matrix4x4 out) {
        source.getScale(tmpVec);
        target.getScale(tmpForward);
        source.getRotation(quaternion);
        target.getRotation(quaternion2);
        source.getTranslation(tmpUp);
        target.getTranslation(right);

        out.setToScaling(tmpVec.scl(1-factor).add(tmpForward.scl(factor)));
        out.rotateLocalAxis(quaternion.slerp(quaternion2, factor));
        out.setTranslation(tmpUp.scl(1-factor).add(right.scl(factor)));
        return out;
    }

    public static Matrix4x4 interpolationPositionRotation(final Matrix4x4 source, final Matrix4x4 target, final float factor, Matrix4x4 out) {
        source.getRotation(quaternion);
        target.getRotation(quaternion2);
        source.getTranslation(tmpUp);
        target.getTranslation(right);

        out.setToScaling(out.getScaleX(), out.getScaleY(), out.getScaleZ());
        out.rotateLocalAxis(quaternion.slerp(quaternion2, factor));
        out.setTranslation(tmpUp.scl(1-factor).add(right.scl(factor)));
        return out;
    }

    public static Matrix4x4 interpolationRotation(final Matrix4x4 source, final Matrix4x4 target, final float factor, Matrix4x4 out) {
        source.getRotation(quaternion);
        target.getRotation(quaternion2);

        out.getScale(tmpUp); // <- tmpUp stores original scale
        out.getTranslation(right); // <- right stores original translation

        out.setToScaling(tmpUp);
        out.rotateLocalAxis(quaternion.slerp(quaternion2, factor));
        out.setTranslation(right);
        return out;
    }



    // TODO: careful
    public Matrix4x4 stretch(float scale) {
        val[M00] *= scale;
        val[M10] *= scale;
        val[M20] *= scale;
        return this;
    }

    public Matrix4x4 elongate(float scale) {
        val[M01] *= scale;
        val[M11] *= scale;
        val[M21] *= scale;
        return this;
    }

    public Matrix4x4 lengthen(float scale) {
        val[M02] *= scale;
        val[M12] *= scale;
        val[M22] *= scale;
        return this;
    }

    public Vector3 getTranslation(Vector3 translation) {
        translation.x = val[M03];
        translation.y = val[M13];
        translation.z = val[M23];
        return translation;
    }

    public float getTranslationX() {
        return val[M03];
    }

    public float getTranslationY() {
        return val[M13];
    }

    public float getTranslationZ() {
        return val[M23];
    }

    /** Gets the rotation of this matrix.
     * @param rotation The {@link Quaternion} to receive the rotation
     * @param normalizeAxes True to normalize the axes, necessary when the matrix might also include scaling.
     * @return The provided {@link Quaternion} for chaining. */
    public Quaternion getRotation(Quaternion rotation, boolean normalizeAxes) {
        return rotation.setFromMatrix(normalizeAxes, this);
    }

    /** Gets the rotation of this matrix.
     * @param rotation The {@link Quaternion} to receive the rotation
     * @return The provided {@link Quaternion} for chaining. */
    public Quaternion getRotation(Quaternion rotation) {
        return rotation.setFromMatrix(this);
    }

    /** @return the squared scale factor on the X axis */
    public float getScaleXSquared () {
        return val[M00] * val[M00] + val[M01] * val[M01] + val[M02] * val[M02];
    }

    /** @return the squared scale factor on the Y axis */
    public float getScaleYSquared () {
        return val[M10] * val[M10] + val[M11] * val[M11] + val[M12] * val[M12];
    }

    /** @return the squared scale factor on the Z axis */
    public float getScaleZSquared () {
        return val[M20] * val[M20] + val[M21] * val[M21] + val[M22] * val[M22];
    }

    /** @return the scale factor on the X axis (non-negative) */
    public float getScaleX() {
        return (val[M01] == 0 && val[M02] == 0) ? Math.abs(val[M00])
                : (float)Math.sqrt(getScaleXSquared());
    }

    /** @return the scale factor on the Y axis (non-negative) */
    public float getScaleY() {
        return (val[M10] == 0 && val[M12] == 0) ? Math.abs(val[M11])
                : (float)Math.sqrt(getScaleYSquared());
    }

    /** @return the scale factor on the X axis (non-negative) */
    public float getScaleZ() {
        return (val[M20] == 0 && val[M21] == 0) ? Math.abs(val[M22])
                : (float)Math.sqrt(getScaleZSquared());
    }

    /** @param scale The vector which will receive the (non-negative) scale components on each axis.
     * @return The provided vector for chaining. */
    public Vector3 getScale(Vector3 scale) {
        return scale.set(getScaleX(), getScaleY(), getScaleZ());
    }

    public Vector3 getScaleSquared(Vector3 scale) {
        return scale.set(getScaleXSquared(), getScaleYSquared(), getScaleZSquared());
    }

    /** removes the translational part and transposes the matrix. */
    public Matrix4x4 toNormalMatrix() {
        val[M03] = 0;
        val[M13] = 0;
        val[M23] = 0;
        return inv().tra();
    }

    /** Multiplies the vectors with the given matrix. The matrix array is assumed to hold a 4x4 column major matrix as you can get
     * from {@link Matrix4x4#val}. The vectors array is assumed to hold 3-component vectors. Offset specifies the offset into the
     * array where the x-component of the first vector is located. The numVecs parameter specifies the number of vectors stored in
     * the vectors array. The stride parameter specifies the number of floats between subsequent vectors and must be >= 3. This is
     * the same as {@link Vector3#mul(Matrix4x4)} applied to multiple vectors.
     * @param m the matrix
     * @param vector3s the vectors
    */
    public static void mulVectors(Matrix4x4 m, Vector3[] vector3s) {
        for (int i = 0; i < vector3s.length; i++) {
            vector3s[i].mul(m);
        }
    }

    /** Multiplies the vectors with the given matrix, , performing a division by w. The matrix array is assumed to hold a 4x4 column
     * major matrix as you can get from {@link Matrix4x4#val}. The vectors array is assumed to hold 3-component vectors. Offset
     * specifies the offset into the array where the x-component of the first vector is located. The numVecs parameter specifies
     * the number of vectors stored in the vectors array. The stride parameter specifies the number of floats between subsequent
     * vectors and must be >= 3. This is the same as {@link Vector3#prj(Matrix4x4)} applied to multiple vectors.
     * @param m the matrix
     * @param vector3s the vectors
     * */
    public static void prj(Matrix4x4 m, Vector3[] vector3s) {
        for (int i = 0; i < vector3s.length; i++) {
            vector3s[i].prj(m);
        }
    }

    /** Multiplies the vectors with the top most 3x3 sub-matrix of the given matrix. The matrix array is assumed to hold a 4x4
     * column major matrix as you can get from {@link Matrix4x4#val}. The vectors array is assumed to hold 3-component vectors.
     * Offset specifies the offset into the array where the x-component of the first vector is located. The numVecs parameter
     * specifies the number of vectors stored in the vectors array. The stride parameter specifies the number of floats between
     * subsequent vectors and must be >= 3. This is the same as {@link Vector3#rot(Matrix4x4)} applied to multiple vectors.
     * @param m the matrix
     * @param vector3s the vectors
     * */
    public static void rot(Matrix4x4 m, Vector3[] vector3s) {
        for (int i = 0; i < vector3s.length; i++) {
            vector3s[i].rot(m);
        }
    }

    public static void interpolate(Matrix4x4 source, Matrix4x4 target, float a, Matrix4x4 out) {
        Vector3 s_scale = source.getScale(new Vector3());
        Quaternion s_rotation = source.getRotation(new Quaternion());
        Vector3 s_position = source.getTranslation(new Vector3());

        Vector3 t_scale = target.getScale(new Vector3());
        Quaternion t_rotation = target.getRotation(new Quaternion());
        Vector3 t_position = target.getTranslation(new Vector3());

        out.idt();
        s_scale.lerp(t_scale, a);
        out.setToScaling(s_scale);
        s_rotation.slerp(t_rotation, a);
        out.rotateLocalAxis(s_rotation);
        s_position.lerp(t_position, a);
        out.translateGlobalAxisXYZ(s_position);
    }

    // TODO: implement unit tests.
    /** Multiplies the matrix mata with matrix matb, storing the result in mata. The arrays are assumed to hold 4x4 column major
     * matrices as you can get from {@link Matrix4x4#val}. This is the same as {@link Matrix4x4#mul(Matrix4x4)}.
     *
     * @param mata the first matrix.
     * @param matb the second matrix. */
    public static void mul(float[] mata, float[] matb) {
        float m00 = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02] * matb[M20] + mata[M03] * matb[M30];
        float m01 = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02] * matb[M21] + mata[M03] * matb[M31];
        float m02 = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02] * matb[M22] + mata[M03] * matb[M32];
        float m03 = mata[M00] * matb[M03] + mata[M01] * matb[M13] + mata[M02] * matb[M23] + mata[M03] * matb[M33];
        float m10 = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12] * matb[M20] + mata[M13] * matb[M30];
        float m11 = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12] * matb[M21] + mata[M13] * matb[M31];
        float m12 = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12] * matb[M22] + mata[M13] * matb[M32];
        float m13 = mata[M10] * matb[M03] + mata[M11] * matb[M13] + mata[M12] * matb[M23] + mata[M13] * matb[M33];
        float m20 = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22] * matb[M20] + mata[M23] * matb[M30];
        float m21 = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22] * matb[M21] + mata[M23] * matb[M31];
        float m22 = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22] * matb[M22] + mata[M23] * matb[M32];
        float m23 = mata[M20] * matb[M03] + mata[M21] * matb[M13] + mata[M22] * matb[M23] + mata[M23] * matb[M33];
        float m30 = mata[M30] * matb[M00] + mata[M31] * matb[M10] + mata[M32] * matb[M20] + mata[M33] * matb[M30];
        float m31 = mata[M30] * matb[M01] + mata[M31] * matb[M11] + mata[M32] * matb[M21] + mata[M33] * matb[M31];
        float m32 = mata[M30] * matb[M02] + mata[M31] * matb[M12] + mata[M32] * matb[M22] + mata[M33] * matb[M32];
        float m33 = mata[M30] * matb[M03] + mata[M31] * matb[M13] + mata[M32] * matb[M23] + mata[M33] * matb[M33];
        mata[M00] = m00;
        mata[M10] = m10;
        mata[M20] = m20;
        mata[M30] = m30;
        mata[M01] = m01;
        mata[M11] = m11;
        mata[M21] = m21;
        mata[M31] = m31;
        mata[M02] = m02;
        mata[M12] = m12;
        mata[M22] = m22;
        mata[M32] = m32;
        mata[M03] = m03;
        mata[M13] = m13;
        mata[M23] = m23;
        mata[M33] = m33;
    }

    /** Multiplies the vector with the given matrix. The matrix array is assumed to hold a 4x4 column major matrix as you can get
     * from {@link Matrix4x4#val}. The vector array is assumed to hold a 3-component vector, with x being the first element, y being
     * the second and z being the last component. The result is stored in the vector array. This is the same as
     * {@link Vector3#mul(Matrix4x4)}.
     * @param mat the matrix
     * @param vec the vector. */
    public static void mulVec(float[] mat, float[] vec) {
        float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03];
        float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13];
        float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23];
        vec[0] = x;
        vec[1] = y;
        vec[2] = z;
    }

    /** Multiplies the vector with the given matrix, performing a division by w. The matrix array is assumed to hold a 4x4 column
     * major matrix as you can get from {@link Matrix4x4#val}. The vector array is assumed to hold a 3-component vector, with x being
     * the first element, y being the second and z being the last component. The result is stored in the vector array. This is the
     * same as {@link Vector3#prj(Matrix4x4)}.
     * @param mat the matrix
     * @param vec the vector. */
    public static void prj(float[] mat, float[] vec) {
        float inv_w = 1.0f / (vec[0] * mat[M30] + vec[1] * mat[M31] + vec[2] * mat[M32] + mat[M33]);
        float x = (vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02] + mat[M03]) * inv_w;
        float y = (vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12] + mat[M13]) * inv_w;
        float z = (vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22] + mat[M23]) * inv_w;
        vec[0] = x;
        vec[1] = y;
        vec[2] = z;
    }

    /** Multiplies the vector with the top most 3x3 sub-matrix of the given matrix. The matrix array is assumed to hold a 4x4
     * column major matrix as you can get from {@link Matrix4x4#val}. The vector array is assumed to hold a 3-component vector, with
     * x being the first element, y being the second and z being the last component. The result is stored in the vector array. This
     * is the same as {@link Vector3#rot(Matrix4x4)}.
     * @param mat the matrix
     * @param vec the vector. */
    public static void rot(float[] mat, float[] vec) {
        float x = vec[0] * mat[M00] + vec[1] * mat[M01] + vec[2] * mat[M02];
        float y = vec[0] * mat[M10] + vec[1] * mat[M11] + vec[2] * mat[M12];
        float z = vec[0] * mat[M20] + vec[1] * mat[M21] + vec[2] * mat[M22];
        vec[0] = x;
        vec[1] = y;
        vec[2] = z;
    }

    /** Computes the inverse of the given matrix. The matrix array is assumed to hold a 4x4 column major matrix as you can get from
     * {@link Matrix4x4#val}.
     * @param values the matrix values.
     * @return false in case the inverse could not be calculated, true otherwise. */
    public static boolean inv (float[] values) {
        float l_det = det(values);
        if (l_det == 0) return false;
        float m00 = values[M12] * values[M23] * values[M31] - values[M13] * values[M22] * values[M31]
                + values[M13] * values[M21] * values[M32] - values[M11] * values[M23] * values[M32]
                - values[M12] * values[M21] * values[M33] + values[M11] * values[M22] * values[M33];
        float m01 = values[M03] * values[M22] * values[M31] - values[M02] * values[M23] * values[M31]
                - values[M03] * values[M21] * values[M32] + values[M01] * values[M23] * values[M32]
                + values[M02] * values[M21] * values[M33] - values[M01] * values[M22] * values[M33];
        float m02 = values[M02] * values[M13] * values[M31] - values[M03] * values[M12] * values[M31]
                + values[M03] * values[M11] * values[M32] - values[M01] * values[M13] * values[M32]
                - values[M02] * values[M11] * values[M33] + values[M01] * values[M12] * values[M33];
        float m03 = values[M03] * values[M12] * values[M21] - values[M02] * values[M13] * values[M21]
                - values[M03] * values[M11] * values[M22] + values[M01] * values[M13] * values[M22]
                + values[M02] * values[M11] * values[M23] - values[M01] * values[M12] * values[M23];
        float m10 = values[M13] * values[M22] * values[M30] - values[M12] * values[M23] * values[M30]
                - values[M13] * values[M20] * values[M32] + values[M10] * values[M23] * values[M32]
                + values[M12] * values[M20] * values[M33] - values[M10] * values[M22] * values[M33];
        float m11 = values[M02] * values[M23] * values[M30] - values[M03] * values[M22] * values[M30]
                + values[M03] * values[M20] * values[M32] - values[M00] * values[M23] * values[M32]
                - values[M02] * values[M20] * values[M33] + values[M00] * values[M22] * values[M33];
        float m12 = values[M03] * values[M12] * values[M30] - values[M02] * values[M13] * values[M30]
                - values[M03] * values[M10] * values[M32] + values[M00] * values[M13] * values[M32]
                + values[M02] * values[M10] * values[M33] - values[M00] * values[M12] * values[M33];
        float m13 = values[M02] * values[M13] * values[M20] - values[M03] * values[M12] * values[M20]
                + values[M03] * values[M10] * values[M22] - values[M00] * values[M13] * values[M22]
                - values[M02] * values[M10] * values[M23] + values[M00] * values[M12] * values[M23];
        float m20 = values[M11] * values[M23] * values[M30] - values[M13] * values[M21] * values[M30]
                + values[M13] * values[M20] * values[M31] - values[M10] * values[M23] * values[M31]
                - values[M11] * values[M20] * values[M33] + values[M10] * values[M21] * values[M33];
        float m21 = values[M03] * values[M21] * values[M30] - values[M01] * values[M23] * values[M30]
                - values[M03] * values[M20] * values[M31] + values[M00] * values[M23] * values[M31]
                + values[M01] * values[M20] * values[M33] - values[M00] * values[M21] * values[M33];
        float m22 = values[M01] * values[M13] * values[M30] - values[M03] * values[M11] * values[M30]
                + values[M03] * values[M10] * values[M31] - values[M00] * values[M13] * values[M31]
                - values[M01] * values[M10] * values[M33] + values[M00] * values[M11] * values[M33];
        float m23 = values[M03] * values[M11] * values[M20] - values[M01] * values[M13] * values[M20]
                - values[M03] * values[M10] * values[M21] + values[M00] * values[M13] * values[M21]
                + values[M01] * values[M10] * values[M23] - values[M00] * values[M11] * values[M23];
        float m30 = values[M12] * values[M21] * values[M30] - values[M11] * values[M22] * values[M30]
                - values[M12] * values[M20] * values[M31] + values[M10] * values[M22] * values[M31]
                + values[M11] * values[M20] * values[M32] - values[M10] * values[M21] * values[M32];
        float m31 = values[M01] * values[M22] * values[M30] - values[M02] * values[M21] * values[M30]
                + values[M02] * values[M20] * values[M31] - values[M00] * values[M22] * values[M31]
                - values[M01] * values[M20] * values[M32] + values[M00] * values[M21] * values[M32];
        float m32 = values[M02] * values[M11] * values[M30] - values[M01] * values[M12] * values[M30]
                - values[M02] * values[M10] * values[M31] + values[M00] * values[M12] * values[M31]
                + values[M01] * values[M10] * values[M32] - values[M00] * values[M11] * values[M32];
        float m33 = values[M01] * values[M12] * values[M20] - values[M02] * values[M11] * values[M20]
                + values[M02] * values[M10] * values[M21] - values[M00] * values[M12] * values[M21]
                - values[M01] * values[M10] * values[M22] + values[M00] * values[M11] * values[M22];
        float inv_det = 1.0f / l_det;
        values[M00] = m00 * inv_det;
        values[M10] = m10 * inv_det;
        values[M20] = m20 * inv_det;
        values[M30] = m30 * inv_det;
        values[M01] = m01 * inv_det;
        values[M11] = m11 * inv_det;
        values[M21] = m21 * inv_det;
        values[M31] = m31 * inv_det;
        values[M02] = m02 * inv_det;
        values[M12] = m12 * inv_det;
        values[M22] = m22 * inv_det;
        values[M32] = m32 * inv_det;
        values[M03] = m03 * inv_det;
        values[M13] = m13 * inv_det;
        values[M23] = m23 * inv_det;
        values[M33] = m33 * inv_det;
        return true;
    }

    /** Computes the determinante of the given matrix. The matrix array is assumed to hold a 4x4 column major matrix as you can get
     * from {@link Matrix4x4#val}.
     * @param values the matrix values.
     * @return the determinante. */
    public static float det(float[] values) {
        return values[M30] * values[M21] * values[M12] * values[M03] - values[M20] * values[M31] * values[M12] * values[M03]
                - values[M30] * values[M11] * values[M22] * values[M03] + values[M10] * values[M31] * values[M22] * values[M03]
                + values[M20] * values[M11] * values[M32] * values[M03] - values[M10] * values[M21] * values[M32] * values[M03]
                - values[M30] * values[M21] * values[M02] * values[M13] + values[M20] * values[M31] * values[M02] * values[M13]
                + values[M30] * values[M01] * values[M22] * values[M13] - values[M00] * values[M31] * values[M22] * values[M13]
                - values[M20] * values[M01] * values[M32] * values[M13] + values[M00] * values[M21] * values[M32] * values[M13]
                + values[M30] * values[M11] * values[M02] * values[M23] - values[M10] * values[M31] * values[M02] * values[M23]
                - values[M30] * values[M01] * values[M12] * values[M23] + values[M00] * values[M31] * values[M12] * values[M23]
                + values[M10] * values[M01] * values[M32] * values[M23] - values[M00] * values[M11] * values[M32] * values[M23]
                - values[M20] * values[M11] * values[M02] * values[M33] + values[M10] * values[M21] * values[M02] * values[M33]
                + values[M20] * values[M01] * values[M12] * values[M33] - values[M00] * values[M21] * values[M12] * values[M33]
                - values[M10] * values[M01] * values[M22] * values[M33] + values[M00] * values[M11] * values[M22] * values[M33];

    }

    /** Postmultiplies this matrix by a translation matrix. Postmultiplication is also used by OpenGL ES'
     * glTranslate/glRotate/glScale
     * @param translation
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 translateLocalAxis(Vector3 translation) {
        return translateLocalAxis(translation.x, translation.y, translation.z);
    }

    /** Postmultiplies this matrix by a translation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     * @param x Translation in the x-axis.
     * @param y Translation in the y-axis.
     * @param z Translation in the z-axis.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 translateLocalAxis(float x, float y, float z) {
        val[M03] += val[M00] * x + val[M01] * y + val[M02] * z;
        val[M13] += val[M10] * x + val[M11] * y + val[M12] * z;
        val[M23] += val[M20] * x + val[M21] * y + val[M22] * z;
        val[M33] += val[M30] * x + val[M31] * y + val[M32] * z;
        return this;
    }

    public Matrix4x4 translateGlobalAxisXYZ(final Vector3 dr) {
        val[M03] += dr.x;
        val[M13] += dr.y;
        val[M23] += dr.z;
        return this;
    }

    public Matrix4x4 translateGlobalAxisXYZ(float dx, float dy, float dz) {
        val[M03] += dx;
        val[M13] += dy;
        val[M23] += dz;
        return this;
    }

    public Matrix4x4 translateGlobalAxisX(float x) {
        val[M03] += x;
        return this;
    }

    public Matrix4x4 translateGlobalAxisY(float y) {
        val[M13] += y;
        return this;
    }

    public Matrix4x4 translateGlobalAxisZ(float z) {
        val[M23] += z;
        return this;
    }

    public Matrix4x4 rotateGlobalAxisX(float degX) {
        float x = val[M03];
        float y = val[M13];
        float z = val[M23];
        translateGlobalAxisXYZ(-x, -y, -z);
        rotation.setToRotation(1, 0, 0, degX);
        mulLeft(rotation);
        translateGlobalAxisXYZ(x, y, z);
        return this;
    }

    public Matrix4x4 rotateGlobalAxisY(float degY) {
        float x = val[M03];
        float y = val[M13];
        float z = val[M23];
        translateGlobalAxisXYZ(-x, -y, -z);
        rotation.setToRotation(0, 1, 0, degY);
        mulLeft(rotation);
        translateGlobalAxisXYZ(x, y, z);
        return this;
    }

    public Matrix4x4 rotateGlobalAxisZ(float degZ) {
        float x = val[M03];
        float y = val[M13];
        float z = val[M23];
        translateGlobalAxisXYZ(-x, -y, -z);
        rotation.setToRotation(0, 0, 1, degZ);
        mulLeft(rotation);
        translateGlobalAxisXYZ(x, y, z);
        return this;
    }

    public Matrix4x4 rotateLocalAxisX(float degrees) {
        return rotateLocalAxis(Vector3.X_UNIT, degrees);
    }

    public Matrix4x4 rotateLocalAxisY(float degrees) { return rotateLocalAxis(Vector3.Y_UNIT, degrees); }

    public Matrix4x4 rotateLocalAxisZ(float degrees) {
        return rotateLocalAxis(Vector3.Z_UNIT, degrees);
    }

    /** Postmultiplies this matrix with a (counter-clockwise) rotation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     * @param axis The vector axis to rotate around.
     * @param degrees The angle in degrees.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 rotateLocalAxis(Vector3 axis, float degrees) {
        if (degrees == 0) return this;
        quaternion.setDeg(axis, degrees);
        return rotateLocalAxis(quaternion);
    }

    /** Postmultiplies this matrix with a (counter-clockwise) rotation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale
     * @param axisX The x-axis component of the vector to rotate around.
     * @param axisY The y-axis component of the vector to rotate around.
     * @param axisZ The z-axis component of the vector to rotate around.
     * @param degrees The angle in degrees
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 rotateLocalAxis(float axisX, float axisY, float axisZ, float degrees) {
        if (degrees == 0) return this;
        quaternion.setFromAxisDeg(axisX, axisY, axisZ, degrees);
        return rotateLocalAxis(quaternion);
    }

    /** Postmultiplies this matrix with a (counter-clockwise) rotation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     * @param rotation
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 rotateLocalAxis(Quaternion rotation) {
        float x = rotation.x, y = rotation.y, z = rotation.z, w = rotation.w;
        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float xw = x * w;
        float yy = y * y;
        float yz = y * z;
        float yw = y * w;
        float zz = z * z;
        float zw = z * w;
        // Set matrix from quaternion
        float r00 = 1 - 2 * (yy + zz);
        float r01 = 2 * (xy - zw);
        float r02 = 2 * (xz + yw);
        float r10 = 2 * (xy + zw);
        float r11 = 1 - 2 * (xx + zz);
        float r12 = 2 * (yz - xw);
        float r20 = 2 * (xz - yw);
        float r21 = 2 * (yz + xw);
        float r22 = 1 - 2 * (xx + yy);
        float m00 = val[M00] * r00 + val[M01] * r10 + val[M02] * r20;
        float m01 = val[M00] * r01 + val[M01] * r11 + val[M02] * r21;
        float m02 = val[M00] * r02 + val[M01] * r12 + val[M02] * r22;
        float m10 = val[M10] * r00 + val[M11] * r10 + val[M12] * r20;
        float m11 = val[M10] * r01 + val[M11] * r11 + val[M12] * r21;
        float m12 = val[M10] * r02 + val[M11] * r12 + val[M12] * r22;
        float m20 = val[M20] * r00 + val[M21] * r10 + val[M22] * r20;
        float m21 = val[M20] * r01 + val[M21] * r11 + val[M22] * r21;
        float m22 = val[M20] * r02 + val[M21] * r12 + val[M22] * r22;
        float m30 = val[M30] * r00 + val[M31] * r10 + val[M32] * r20;
        float m31 = val[M30] * r01 + val[M31] * r11 + val[M32] * r21;
        float m32 = val[M30] * r02 + val[M31] * r12 + val[M32] * r22;
        val[M00] = m00;
        val[M10] = m10;
        val[M20] = m20;
        val[M30] = m30;
        val[M01] = m01;
        val[M11] = m11;
        val[M21] = m21;
        val[M31] = m31;
        val[M02] = m02;
        val[M12] = m12;
        val[M22] = m22;
        val[M32] = m32;
        return this;
    }

    // TODO: test
    public Vector3 getBasisX(Vector3 out) {
        out.x = val[M00];
        out.y = val[M10];
        out.z = val[M20];
        return out;
    }

    // TODO: test
    public Vector3 getBasisY(Vector3 out) {
        out.x = val[M01];
        out.y = val[M11];
        out.z = val[M21];
        return out;
    }

    // TODO: test
    public Vector3 getBasisZ(Vector3 out) {
        out.x = val[M02];
        out.y = val[M12];
        out.z = val[M22];
        return out;
    }

    // TODO: test
    public Matrix4x4 setFromBasis(Vector3 b1, Vector3 b2, Vector3 b3, Vector3 origin) {
        val[M00] = b1.x;
        val[M10] = b1.y;
        val[M20] = b1.z;

        val[M01] = b2.x;
        val[M11] = b2.y;
        val[M21] = b2.z;

        val[M02] = b3.x;
        val[M12] = b3.y;
        val[M22] = b3.z;

        val[M03] = origin.x;
        val[M13] = origin.y;
        val[M23] = origin.z;

        val[M30] = 0f;
        val[M31] = 0f;
        val[M32] = 0f;
        val[M33] = 1f;

        return this;
    }

    // TODO: remove this. this is questionable.
    @Deprecated public Matrix4x4 setFromBasis(Vector3 b1, Vector3 b2, Vector3 b3, Vector3 position, Vector3 scale) {
        val[M00] = scale.x * b1.x;
        val[M10] = scale.x * b1.y;
        val[M20] = scale.x * b1.z;

        val[M01] = scale.y * b2.x;
        val[M11] = scale.y * b2.y;
        val[M21] = scale.y * b2.z;

        val[M02] = scale.z * b3.x;
        val[M12] = scale.z * b3.y;
        val[M22] = scale.z * b3.z;

        val[M03] = position.x;
        val[M13] = position.y;
        val[M23] = position.z;

        val[M30] = 0f;
        val[M31] = 0f;
        val[M32] = 0f;
        val[M33] = 1f;

        return this;
    }

    /** Postmultiplies this matrix by the rotation between two vectors.
     * @param v1 The base vector
     * @param v2 The target vector
     * @return This matrix for the purpose of chaining methods together */
    public Matrix4x4 rotateLocalAxis(final Vector3 v1, final Vector3 v2) {
        return rotateLocalAxis(quaternion.setFromSourceToTarget(v1, v2));
    }

    /** Post-multiplies this matrix by a rotation toward a direction.
     * @param direction direction to rotate toward
     * @param up up vector
     * @return This matrix for chaining */
    public Matrix4x4 rotateTowardDirection(final Vector3 direction, final Vector3 up) {
        l_vez.set(direction).nor();
        l_vex.set(direction).crs(up).nor();
        l_vey.set(l_vex).crs(l_vez).nor();
        float m00 = val[M00] * l_vex.x + val[M01] * l_vex.y + val[M02] * l_vex.z;
        float m01 = val[M00] * l_vey.x + val[M01] * l_vey.y + val[M02] * l_vey.z;
        float m02 = val[M00] * -l_vez.x + val[M01] * -l_vez.y + val[M02] * -l_vez.z;
        float m10 = val[M10] * l_vex.x + val[M11] * l_vex.y + val[M12] * l_vex.z;
        float m11 = val[M10] * l_vey.x + val[M11] * l_vey.y + val[M12] * l_vey.z;
        float m12 = val[M10] * -l_vez.x + val[M11] * -l_vez.y + val[M12] * -l_vez.z;
        float m20 = val[M20] * l_vex.x + val[M21] * l_vex.y + val[M22] * l_vex.z;
        float m21 = val[M20] * l_vey.x + val[M21] * l_vey.y + val[M22] * l_vey.z;
        float m22 = val[M20] * -l_vez.x + val[M21] * -l_vez.y + val[M22] * -l_vez.z;
        float m30 = val[M30] * l_vex.x + val[M31] * l_vex.y + val[M32] * l_vex.z;
        float m31 = val[M30] * l_vey.x + val[M31] * l_vey.y + val[M32] * l_vey.z;
        float m32 = val[M30] * -l_vez.x + val[M31] * -l_vez.y + val[M32] * -l_vez.z;
        val[M00] = m00;
        val[M10] = m10;
        val[M20] = m20;
        val[M30] = m30;
        val[M01] = m01;
        val[M11] = m11;
        val[M21] = m21;
        val[M31] = m31;
        val[M02] = m02;
        val[M12] = m12;
        val[M22] = m22;
        val[M32] = m32;
        return this;
    }

    /** Post-multiplies this matrix by a rotation toward a target.
     * @param target the target to rotate to
     * @param up the up vector
     * @return This matrix for chaining */
    public Matrix4x4 rotateTowardTarget(final Vector3 target, final Vector3 up) {
        tmpVec.set(target.x - val[M03], target.y - val[M13], target.z - val[M23]);
        return rotateTowardDirection(tmpVec, up);
    }

    public Matrix4x4 scale(final Vector3 scale) {
        return this.scale(scale.x, scale.y, scale.z);
    }

    /** Postmultiplies this matrix with a scale matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     * @param scaleX The scale in the x-axis.
     * @param scaleY The scale in the y-axis.
     * @param scaleZ The scale in the z-axis.
     * @return This matrix for the purpose of chaining methods together. */
    public Matrix4x4 scale(float scaleX, float scaleY, float scaleZ) {
        val[M00] *= scaleX;
        val[M01] *= scaleY;
        val[M02] *= scaleZ;
        val[M10] *= scaleX;
        val[M11] *= scaleY;
        val[M12] *= scaleZ;
        val[M20] *= scaleX;
        val[M21] *= scaleY;
        val[M22] *= scaleZ;
        val[M30] *= scaleX;
        val[M31] *= scaleY;
        val[M32] *= scaleZ;
        return this;
    }

    /** Copies the 4x3 upper-left sub-matrix into float array. The destination array is supposed to be a column major matrix.
     * @param dst the destination matrix */
    public void extract4x3Matrix(float[] dst) {
        dst[0] = val[M00];
        dst[1] = val[M10];
        dst[2] = val[M20];
        dst[3] = val[M01];
        dst[4] = val[M11];
        dst[5] = val[M21];
        dst[6] = val[M02];
        dst[7] = val[M12];
        dst[8] = val[M22];
        dst[9] = val[M03];
        dst[10] = val[M13];
        dst[11] = val[M23];
    }

    // TODO
//    /** @return True if this matrix has any rotation or scaling, false otherwise */
//    public boolean hasRotationOrScaling () {
//        return !(MathUtils.isEqual(val[M00], 1) && MathUtils.isEqual(val[M11], 1) && MathUtils.isEqual(val[M22], 1)
//                && MathUtils.isZero(val[M01]) && MathUtils.isZero(val[M02]) && MathUtils.isZero(val[M10]) && MathUtils.isZero(val[M12])
//                && MathUtils.isZero(val[M20]) && MathUtils.isZero(val[M21]));
//    }


    @Override
    public void reset() {
        this.idt();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Matrix4x4 other = (Matrix4x4)obj;
        for (int i = 0; i < val.length; i++) {
            if (val[i] != other.val[i]) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return    "[" + val[M00] + "|" + val[M01] + "|" + val[M02] + "|" + val[M03] + "]\n" //
                + "[" + val[M10] + "|" + val[M11] + "|" + val[M12] + "|" + val[M13] + "]\n" //
                + "[" + val[M20] + "|" + val[M21] + "|" + val[M22] + "|" + val[M23] + "]\n" //
                + "[" + val[M30] + "|" + val[M31] + "|" + val[M32] + "|" + val[M33] + "]\n";
    }

}
