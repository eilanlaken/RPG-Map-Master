package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.physics2d.RayCasting2DRay;

/*
The system responsible for updating the transforms (2d and 3d).
Updating is done according to: physics and parent-child relationships.
Note that a transform may also be manipulated from a ComponentLogics.
 */
public class SystemDynamics implements System {

    private final EntityContainer container;
    private final Array<Entity2D> entities_2d = new Array<>(false, 10);
    private final Array<Entity3D> entities_3d = new Array<>(false, 10);

    SystemDynamics(final EntityContainer container) {
        this.container = container;
    }

    @Override
    public boolean shouldProcess(Entity entity) {
        return true;
    }

    @Override
    public void add(Entity entity) {
        if (entity instanceof Entity2D) {
            Entity2D entity2D = (Entity2D) entity;
            entities_2d.add(entity2D); // <- later consider what to do with static transforms.
            ComponentPhysics2D physics2D = (ComponentPhysics2D) entity2D.getComponentPhysics();
            // ... TODO
            return;
        }
        if (entity instanceof Entity3D) {

            return;
        }
    }

    @Override
    public void remove(Entity entity) {

    }

    @Override
    public void frameUpdate(float delta) {
        // no-frame update: dynamics is a game logic related stuff only.
    }

    @Override
    public void fixedUpdate(float delta) {

    }

    @Override
    public String toString() {
        return "System Dynamics Entities : " + (entities_2d.size + entities_3d.size);
    }

    /* System API */

    public void raycast(RayCasting2DRay ray) {
        // TODO
    }

}
