package com.heavybox.jtix.math;

public class Transform3D {

    public Vector3    position;
    public Quaternion rotation;
    public Vector3    scale;

    public Transform3D() {
        this.position = new Vector3(0,0,0);
        this.rotation = new Quaternion();
        this.scale    = new Vector3(1,1,1);
    }

    /** Sets the quaternion to an identity Quaternion
     * @return this quaternion for chaining */
    public Transform3D idt() {
        this.position.set(0,0,0);
        this.rotation.idt();
        this.scale.set(1,1,1);
        return this;
    }

}
