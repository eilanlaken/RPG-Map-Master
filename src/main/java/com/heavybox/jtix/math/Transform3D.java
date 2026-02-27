package com.heavybox.jtix.math;

import org.jetbrains.annotations.NotNull;

// TODO: test
public class Transform3D {

    public Vector3    translation = new Vector3(0,0,0);
    public Quaternion rotation    = new Quaternion();
    public Vector3    scale       = new Vector3(1,1,1);

    public Transform3D() {}

    public Transform3D(final Transform3D other) {
        this.translation = new Vector3(other.translation);
        this.rotation = new Quaternion(other.rotation);
        this.scale = new Vector3(other.scale);
    }

    /** Sets the quaternion to an identity Quaternion
     * @return this quaternion for chaining */
    public Transform3D idt() {
        this.translation.set(0,0,0);
        this.rotation.idt();
        this.scale.set(1,1,1);
        return this;
    }

    public Vector3 transform(Vector3 v) {
        v.scl(scale);
        v.rot(rotation);
        v.add(translation);
        return v;
    }

    // TODO: toMatrix, fromMatrix etc.
    // set to look at
    // translate global
    // translate local

    // TODO: test
    public Matrix4x4 toMatrix(@NotNull Matrix4x4 mat) {
        mat.setToTranslationRotationScaling(translation, rotation, scale);
        return mat;
    }

    @Override
    public String toString() {
        return "position: " + translation + "\n" + "rotation: " + rotation + "\n" + "scale: " + scale;
    }
}
