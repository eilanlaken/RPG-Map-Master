package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.MathUtils;

public final class ECS {

    private ECS() {}

    static int getComponentsBitmask(final Entity entity) {
        int mask = 0;
        Component component;
        for (Component.Type type : Component.Type.values()) {
            component = entity.getComponent(type);
            if (component != null) mask |= component.getBitmask();
        }
        return mask;
    }

    public static int getLayersBitmask(EntityLayer2D... layers) {
        if (layers == null) return EntityLayer2D.DEFAULT.bitmask;
        int layerBitmask = 0;
        for (EntityLayer2D layer : layers) {
            layerBitmask |= layer.bitmask;
        }
        return layerBitmask;
    }

    public static int getLayersBitmask(EntityLayer3D... layers) {
        if (layers == null) return EntityLayer3D.DEFAULT.bitmask;
        int layerBitmask = 0;
        for (EntityLayer3D layer : layers) {
            layerBitmask |= layer.bitmask;
        }
        return layerBitmask;
    }

    public static void getDescendants(Entity2D entity, Array<Entity> out) {
        if (entity.children == null) return;
        for (Entity2D child : entity.children) {
            out.add(child);
            getDescendants(child, out);
        }
    }

    public static boolean isDescendant(Entity2D ancestor, final Entity2D descendant) {
        if (ancestor.children == null) return false;
        for (Entity2D child : ancestor.children) {
            if (child == descendant) return true;
            if (isDescendant(child, descendant)) return true;
        }
        return false;
    }

    @Deprecated // TODO: remove, for debug only.
    public static Entity2D createDebugEntity() {
        return new Entity2D() {
            @Override
            protected ComponentAudio createComponentAudio() {
                int rand = MathUtils.randomUniformInt(0, 2);
                if (rand == 0) return null;
                return new ComponentAudioPlayer();
            }

            @Override
            protected ComponentRender2D createComponentRender() {
                int rand = MathUtils.randomUniformInt(0, 2);
                if (rand == 0) return null;
                return new ComponentRender2DSprite();
            }

            @Override
            protected ComponentCamera2D createComponentCamera() {
                int rand = MathUtils.randomUniformInt(0, 2);
                if (rand == 0) return null;
                return new ComponentCamera2D(10);
            }

            @Override
            protected ComponentPhysics2D createComponentPhysics() {
                int rand = MathUtils.randomUniformInt(0, 2);
                if (rand == 0) return null;
                return new ComponentPhysics2DBodyRigid();
            }

            @Override
            protected ComponentLogics createComponentLogics() {
                int rand = MathUtils.randomUniformInt(0, 2);
                if (rand == 0) return null;
                return new ComponentLogicsScripts();
            }

            @Override
            protected ComponentRegion createComponentRegion() {
                int rand = MathUtils.randomUniformInt(0, 2);
                if (rand == 0) return null;
                return new ComponentRegionTexture();
            }

        };
    }

}
