package com.heavybox.jtix.ecs;

public final class ComponentTransform2D implements ComponentTransform {

    // TODO
    ComponentTransform2D parent;
    ComponentTransform2D world;

    public final boolean isStatic;

    public float x       = 0;
    public float y       = 0;
    public float degrees = 0;
    public float sclX    = 1;
    public float sclY    = 1;

    public ComponentTransform2D() {
        this.isStatic = false;
    }

    public ComponentTransform2D(boolean isStatic, float x, float y, float degrees, float sclX, float sclY) {
        this.isStatic = isStatic;
        this.x = x;
        this.y = y;
        this.degrees = degrees;
        this.sclX = sclX;
        this.sclY = sclY;
    }

    public ComponentTransform2D(boolean isStatic, float x, float y, float degrees) {
        this(isStatic, x, y, degrees,1,1);
    }

    public void set(float x, float y, float degrees, float sclX, float sclY) {
        this.x = x;
        this.y = y;
        this.degrees = degrees;
        this.sclX = sclX;
        this.sclY = sclY;
    }

    public void set(final ComponentTransform2D transform2D) {
        this.x = transform2D.x;
        this.y = transform2D.y;
        this.degrees = transform2D.degrees;
        this.sclX = transform2D.sclX;
        this.sclY = transform2D.sclY;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public float getPositionX() {
        return x;
    }

    @Override
    public float getPositionY() {
        return y;
    }

    @Override
    public float getPositionZ() {
        return 0;
    }

    @Override
    public ComponentTransform2D getWorld() {
        return world == null ? this : world;
    }

}
