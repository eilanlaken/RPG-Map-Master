package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;
import org.jetbrains.annotations.NotNull;

public abstract class Entity2D extends Entity {

    public final ComponentTransform2D transform;

    protected Entity2D        parent   = null;
    protected Array<Entity2D> children = null;

    protected Entity2D() {
        this(false,0,0,0,1,1);
    }

    protected Entity2D(boolean isStatic, float x, float y, float deg) {
        this(isStatic, x, y, deg, 1, 1);
    }

    protected Entity2D(boolean isStatic, float x, float y, float deg, float sclX, float sclY) {
        this.transform = new ComponentTransform2D(isStatic, x, y, deg, sclX, sclY);
    }

    // TODO
    public final void setParent(Entity2D newParent, boolean keepTransform) {
        if (newParent == null) {
            clearParent(keepTransform);
            return;
        }
        if (newParent == this) throw new ECSException("Cannot parent an " + Entity2D.class.getSimpleName() + " to itself.");
        if (ECS.isDescendant(this, newParent)) throw new ECSException("Trying to create circular dependency between entities.");
        if (this.parent == newParent) return;

        if (this.parent != null) clearParent(keepTransform); // clear old parent first
        this.parent = newParent;
        if (newParent.children == null) newParent.children = new Array<>(false, 1);
        newParent.children.add(this);
        this.transform.parent = newParent.transform;
        if (transform.world == null) {
            transform.world = new ComponentTransform2D();
        }

        if (keepTransform) { // TODO

        }
    }

    public final void clearParent(boolean keepTransform) {
        if (this.parent == null) return;

        this.parent.children.removeValue(this, true);
        this.transform.parent = null;
        if (keepTransform) {
            this.transform.set(this.transform.world);
        }
        this.transform.world = null;
        this.parent = null;
    }

    public final void addChild(Entity2D child, boolean keepTransform) {
        if (child == null) return;
        child.setParent(this, keepTransform);
    }

    public final void removeChild(Entity2D child, boolean keepTransform) {
        if (child == null) return;
        if (children == null) return;
        if (!children.contains(child, true)) return;
        child.clearParent(keepTransform);
    }

    @Deprecated protected void attachChild(Entity2D child) {
        if (child == null) throw new ECSException("Child entity cannot be null");
        if (child == this) throw new ECSException("Cannot add an " + Entity2D.class.getSimpleName() + " as a child to itself.");
        if (child.parent != null) throw new ECSException("child " + Entity2D.class.getSimpleName() + " already has a parent. Must un-parent first.");
        if (this.children != null && this.children.contains(child,true)) throw new ECSException(Entity2D.class.getSimpleName() + " child is already a child of this " + Entity2D.class.getSimpleName());
        if (this.children == null) this.children = new Array<>(false, 1);
        this.children.add(child);
        child.parent = this;
    }

    @Override protected          ComponentTransform2D createComponentTransform() { return transform; }
    @Override protected abstract ComponentAudio       createComponentAudio();
    @Override protected abstract ComponentRender2D    createComponentRender();
    @Override protected abstract ComponentCamera2D    createComponentCamera();
    @Override protected abstract ComponentPhysics2D   createComponentPhysics();
    @Override protected abstract ComponentLogics      createComponentLogics();
    @Override protected abstract ComponentRegion      createComponentRegion();

    @Override public final ComponentTransform2D getComponentTransform() {
        return transform;
    }

    public @NotNull EntityLayer2D getLayer() {
        return EntityLayer2D.DEFAULT;
    }

}
