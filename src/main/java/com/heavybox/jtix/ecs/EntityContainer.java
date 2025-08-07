package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.memory.MemoryResourceHolder;

public abstract class EntityContainer implements MemoryResourceHolder {

    /* Entities */
    protected Array<Entity> entities = new Array<>(false, 200);
    protected Array<Entity> toAdd    = new Array<>(false, 50);
    protected Array<Entity> toRemove = new Array<>(false, 50);

    /* State */
    private float secondsPerUpdate = 1 / 60.0f;
    private float lag              = 0;

    /* Systems */
    protected final Array<System>   systems        = new Array<>(true, 5);
    protected final SystemDynamics  systemDynamics = new SystemDynamics(this);
    protected final SystemGUI       systemGUI      = new SystemGUI(this);
    protected final SystemAudio     systemAudio    = new SystemAudio(this);
    protected final SystemGraphics  systemGraphics = new SystemGraphics(this);
    protected final SystemLogics    systemLogics   = new SystemLogics(this);

    /* Component Store */
    protected Array<ComponentTransform> componentTransforms = new Array<>(false, 200);
    protected Array<ComponentRender>    componentRenders    = new Array<>(false, 200);
    protected Array<ComponentCamera>    componentCameras    = new Array<>(false, 200);
    protected Array<ComponentAudio>     componentAudios     = new Array<>(false, 200);
    protected Array<ComponentPhysics>   componentPhysics    = new Array<>(false, 200);
    protected Array<ComponentLogics>    componentScripts    = new Array<>(false, 200);
    protected Array<ComponentRegion>    componentRegions    = new Array<>(false, 200);

    /* global container services */
    protected final Renderer2D renderer2D = new Renderer2D();

    public EntityContainer() {
        systems.add(systemDynamics);
        systems.add(systemAudio);
        systems.add(systemGUI);
        systems.add(systemGraphics);
        systems.add(systemLogics);
    }

    public abstract void prepare();
    public abstract void start();

    public void update() {
        float elapsedTime = Graphics.getDeltaTime();
        this.lag += elapsedTime;

        /* call the fixedUpdate() of every system */
        while (this.lag >= this.secondsPerUpdate) {
            removeEntities();
            addEntities();
            for (System system : systems) {
                system.fixedUpdate(this.secondsPerUpdate);
            }
            this.lag -= this.secondsPerUpdate;
        }

        /* call the frameUpdate() of every system */
        for (System system : systems) {
            system.frameUpdate(elapsedTime);
        }
    }

    private void removeEntities() {
        for (Entity entity : toRemove) {
            if (entity.handle == Entity.PHASE_AFTER_DESTROYED) continue;
            if (entity.handle == Entity.PHASE_BEFORE_CREATED)  continue;

            //throw new ECSException("Trying to remove an " + Entity.class.getSimpleName() + " that was not inserted");
            for (System system : systems) {
                if (system.shouldProcess(entity)) system.remove(entity);
            }
            int handle = entity.handle;
            /* remove from components store */
            componentTransforms.removeIndex(handle);
            componentRenders.removeIndex(handle);
            componentCameras.removeIndex(handle);
            componentAudios.removeIndex(handle);
            componentPhysics.removeIndex(handle);
            componentScripts.removeIndex(handle);
            componentRegions.removeIndex(handle);
            /* remove from entities array and update the handles of the affected Entities */
            entities.removeIndex(handle);
            entity.handle = Entity.PHASE_AFTER_DESTROYED;
            entity.setContainer(null);
            if (entities.isEmpty()) continue;
            entities.get(handle).handle = handle;
        }

        toRemove.clear();
    }

    private void addEntities() {
        for (Entity entity : toAdd) {
            if (entity.handle == Entity.PHASE_AFTER_DESTROYED) throw new ECSException("Cannot create " + Entity.class.getSimpleName() + " after it was destroyed.");
            if (entity.handle != Entity.PHASE_BEFORE_CREATED)  continue;

            entity.handle = entities.size;
            entity.setContainer(this);
            entities.add(entity);
            ComponentTransform cTransform = entity.createComponentTransform();
            ComponentAudio cAudio = entity.createComponentAudio();
            ComponentRender cRender = entity.createComponentRender();
            ComponentCamera cCamera = entity.createComponentCamera();
            ComponentPhysics cPhysics = entity.createComponentPhysics();
            ComponentLogics cLogics = entity.createComponentLogics();
            ComponentRegion cRegion = entity.createComponentRegion();
            componentTransforms.add(cTransform);
            componentAudios.add(cAudio);
            componentRenders.add(cRender);
            componentCameras.add(cCamera);
            componentPhysics.add(cPhysics);
            componentScripts.add(cLogics);
            componentRegions.add(cRegion);

            entity.bitmask = ECS.getComponentsBitmask(entity);
        }
        for (Entity entity : toAdd) {
            for (System system : systems) {
                if (system.shouldProcess(entity)) system.add(entity);
            }
        }
        toAdd.clear();
    }

    public void createEntity(Entity2D entity) {
        this.toAdd.add(entity);
        if (entity.children == null) return;
        for (Entity2D child : entity.children) {
            createEntity(child);
        }
    }

    public void destroyEntity(Entity2D entity) {
        entity.clearParent(false);
        this.toRemove.add(entity);
        ECS.getDescendants(entity, this.toRemove);
    }

//    public void createEntity(Entity3D entity) {
//        this.toAdd.add(entity);
//        if (entity.children == null) return;
//        for (Entity2D child : entity.children) {
//            createEntity(child);
//        }
//    }
//
//    public void destroyEntity(Entity3D entity) {
//        entity.clearParent(false);
//        this.toRemove.add(entity);
//        entity.getDescendants(this.toRemove);
//    }

    public void setSecondsPerUpdate(float secondsPerUpdate) {
        if (secondsPerUpdate <= 0) return;
        this.secondsPerUpdate = secondsPerUpdate;
    }

    public float getSecondsPerUpdate() {
        return secondsPerUpdate;
    }

    public void registerSystem(System system) {
        if (systems.contains(system, true)) return;
        systems.add(system);
    }

    @Override
    public void deleteAll() {
        renderer2D.deleteAll();
        // TODO: delete all. Renderer2D, any other resources.
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Print the size of the entities array
        sb.append("Entities Size  : ").append(entities.size).append("\n\n");
        sb.append("--Component Store--").append("\n");
        sb.append("            ");
        for (int i = 0; i < entities.size; i++) {
            String index = String.format("%6d", i);
            sb.append(index);
        }
        sb.append("\n");
        // Print the transforms as 0s and 1s
        sb.append("Transforms: ");
        for (ComponentTransform c : componentTransforms) {
            sb.append(c == null ? String.format("%6d", 0) : String.format("%6d", 1));
        }
        sb.append("\n");
        // Print the renders as 0s and 1s
        sb.append("Renders   : ");
        for (ComponentRender c : componentRenders) {
            sb.append(c == null ? String.format("%6d", 0) : String.format("%6d", 1));
        }
        sb.append("\n");
        // Print the cameras as 0s and 1s
        sb.append("Cameras   : ");
        for (ComponentCamera c : componentCameras) {
            sb.append(c == null ? String.format("%6d", 0) : String.format("%6d", 1));
        }
        sb.append("\n");
        // Print the audios as 0s and 1s
        sb.append("Audios    : ");
        for (ComponentAudio c : componentAudios) {
            sb.append(c == null ? String.format("%6d", 0) : String.format("%6d", 1));
        }
        sb.append("\n");
        // Print the physics as 0s and 1s
        sb.append("Physics   : ");
        for (ComponentPhysics c : componentPhysics) {
            sb.append(c == null ? String.format("%6d", 0) : String.format("%6d", 1));
        }
        sb.append("\n");
        // Print the logics as 0s and 1s
        sb.append("Scripts   : ");
        for (ComponentLogics c : componentScripts) {
            sb.append(c == null ? String.format("%6d", 0) : String.format("%6d", 1));
        }
        sb.append("\n");
        // Print the regions as 0s and 1s
        sb.append("Regions   : ");
        for (ComponentRegion c : componentRegions) {
            sb.append(c == null ? String.format("%6d", 0) : String.format("%6d", 1));
        }
        sb.append("\n\n");
        sb.append("--Systems--").append("\n");
        for (System system : systems) {
            sb.append(system.toString()).append("\n");
        }
        return sb.toString();
    }

}
