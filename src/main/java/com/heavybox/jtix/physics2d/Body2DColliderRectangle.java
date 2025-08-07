package com.heavybox.jtix.physics2d;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Vector2;

/**
Represent a rectangular body collider:

       c0 ---------------c3
       |                 |
       |                 |
       |                 |
       c1 --------------c2

 **/
public final class Body2DColliderRectangle extends Body2DCollider {

    public final float width;
    public final float widthHalf;
    public final float height;
    public final float heightHalf;

    // world corners:
    public final Vector2 c0 = new Vector2();
    public final Vector2 c1 = new Vector2();
    public final Vector2 c2 = new Vector2();
    public final Vector2 c3 = new Vector2();

    public final Array<Vector2> worldVertices = new Array<>(true, 4);

    public Body2DColliderRectangle(Data data, float width, float height, float offsetX, float offsetY, float offsetAngleRad) {
        this(data.density, data.staticFriction, data.dynamicFriction, data.restitution, data.ghost, data.bitmask, width, height, offsetX, offsetY, offsetAngleRad);
    }

    public Body2DColliderRectangle(Data data, float width, float height) {
        this(data.density, data.staticFriction, data.dynamicFriction, data.restitution, data.ghost, data.bitmask, width, height, 0, 0, 0);
    }

    public Body2DColliderRectangle(float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask,
                                   float width, float height) {
        this(density, staticFriction, dynamicFriction, restitution, ghost, bitmask, width, height, 0, 0, 0);
    }

    public Body2DColliderRectangle(float density, float staticFriction, float dynamicFriction, float restitution, boolean ghost, int bitmask,
                                   float width, float height, float offsetX, float offsetY, float offsetAngleRad) {
        super(density, staticFriction, dynamicFriction, restitution, ghost, bitmask, offsetX, offsetY, offsetAngleRad);
        this.width = width;
        this.widthHalf = width * 0.5f;
        this.height = height;
        this.heightHalf = height * 0.5f;
        worldVertices.add(c0, c1, c2, c3);
    }

    @Override
    protected boolean containsPoint(float x, float y) {
        Vector2 tmp1 = new Vector2();
        Vector2 tmp2 = new Vector2();

        tmp1.set(c3).sub(c0);
        tmp2.set(x,y).sub(c0);
        float projection1 = tmp1.dot(tmp2);
        if (projection1 < 0 || projection1 > tmp1.dot(tmp1)) return false;

        tmp1.set(c1).sub(c0);
        tmp2.set(x,y).sub(c0);
        float projection2 = tmp1.dot(tmp2);
        if (projection2 < 0 || projection2 > tmp1.dot(tmp1)) return false;

        return true;
    }

    @Override
    protected float calculateBoundingRadius() {
        return (float) Math.sqrt(widthHalf * widthHalf + heightHalf * heightHalf);
    }

    @Override
    protected float calculateArea() {
        return width * height;
    }

    @Override
    protected void update() {
        c0.set(-widthHalf, +heightHalf).rotateRad(offsetAngleRad).add(offset);
        c1.set(-widthHalf, -heightHalf).rotateRad(offsetAngleRad).add(offset);
        c2.set(+widthHalf, -heightHalf).rotateRad(offsetAngleRad).add(offset);
        c3.set(+widthHalf, +heightHalf).rotateRad(offsetAngleRad).add(offset);

        c0.rotateAroundRad(body.local_cmX, body.local_cmY, body.radians);
        c1.rotateAroundRad(body.local_cmX, body.local_cmY, body.radians);
        c2.rotateAroundRad(body.local_cmX, body.local_cmY, body.radians);
        c3.rotateAroundRad(body.local_cmX, body.local_cmY, body.radians);

        // translate
        c0.add(body.x, body.y);
        c1.add(body.x, body.y);
        c2.add(body.x, body.y);
        c3.add(body.x, body.y);
    }

    @Override
    Vector2 calculateLocalCenter() {
        return offset;
    }

    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + "| c0: " + c0 + ", c1: " + c1 + ", c2: " + c2 + ", c3: " + c3 + ">";
    }

}