package com.heavybox.jtix.math;

// TODO: test
public class Transform3D {

    public Vector3    position = new Vector3(0,0,0);
    public Quaternion rotation = new Quaternion();
    public Vector3    scale    = new Vector3(1,1,1);

    /** Sets the quaternion to an identity Quaternion
     * @return this quaternion for chaining */
    public Transform3D idt() {
        this.position.set(0,0,0);
        this.rotation.idt();
        this.scale.set(1,1,1);
        return this;
    }

    // TODO: toMatrix, fromMatrix etc.
    // set to look at
    // translate global
    // translate local

}
