package com.heavybox.jtix.ecs;

import org.jetbrains.annotations.NotNull;

abstract class Entity {

    public static final int PHASE_BEFORE_CREATED  = -1;
    public static final int PHASE_AFTER_DESTROYED = -2;

    EntityContainer container = null;
    int             handle    = PHASE_BEFORE_CREATED;
    int             bitmask   = 0;

    Entity() {} // default package-private constructor to prevent directly extending Entity.

    public EntityContainer getContainer() { return container; }
    void setContainer(final EntityContainer container) { this.container = container; }

    protected abstract ComponentTransform createComponentTransform();
    protected abstract ComponentAudio     createComponentAudio();
    protected abstract ComponentRender    createComponentRender();
    protected abstract ComponentCamera    createComponentCamera();
    protected abstract ComponentPhysics   createComponentPhysics();
    protected abstract ComponentLogics    createComponentLogics();
    protected abstract ComponentRegion    createComponentRegion();

    protected abstract ComponentTransform getComponentTransform();

    public final ComponentAudio getComponentAudio() {
        if (handle == PHASE_BEFORE_CREATED) return null;
        return container.componentAudios.get(handle);
    }

    public final ComponentRender getComponentRender() {
        if (handle == -1) return null;
        return container.componentRenders.get(handle);
    }

    public final ComponentCamera getComponentCamera() {
        if (handle == -1) return null;
        return container.componentCameras.get(handle);
    }

    public final ComponentPhysics getComponentPhysics() {
        if (handle == -1) return null;
        return container.componentPhysics.get(handle);
    }

    public final ComponentLogics getComponentLogics() {
        if (handle == -1) return null;
        return container.componentScripts.get(handle);
    }

    public final ComponentRegion getComponentRegion() {
        if (handle == -1) return null;
        return container.componentRegions.get(handle);
    }

    public final Component getComponent(@NotNull Component.Type type) {
        if (handle == -1) return null;

        return switch (type) {
            case AUDIO     -> container.componentAudios.get(handle);
            case RENDER    -> container.componentRenders.get(handle);
            case CAMERA    -> container.componentCameras.get(handle);
            case LOGICS    -> container.componentScripts.get(handle);
            case PHYSICS   -> container.componentPhysics.get(handle);
            case REGION    -> container.componentRegions.get(handle);
            case TRANSFORM -> container.componentTransforms.get(handle);
        };
    }

}
