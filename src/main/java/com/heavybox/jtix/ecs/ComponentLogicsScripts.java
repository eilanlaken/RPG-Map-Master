package com.heavybox.jtix.ecs;

import com.heavybox.jtix.collections.Array;

public class ComponentLogicsScripts implements ComponentLogics {

    public Array<ComponentLogicsScript> scripts;

    public ComponentLogicsScripts(ComponentLogicsScript... scripts) {
        this.scripts = new Array<>(scripts);
    }

    final void setEntity(final Entity entity) {
        for (ComponentLogicsScript script : scripts) {
            if (script.entity != null)
                throw new ECSException(Entity.class.getSimpleName() + " " + script.entity + " is already set for this" + ComponentLogicsScript.class.getSimpleName());
            else script.entity = entity;
        }
    }

    @Override
    public void start() {
        for (ComponentLogicsScript script : scripts) {
            script.start();
        }
    }

    @Override
    public void frameUpdate(float delta) {
        for (ComponentLogicsScript script : scripts) {
            script.frameUpdate(delta);
        }
    }

    @Override
    public void fixedUpdate(float delta) {
        for (ComponentLogicsScript script : scripts) {
            script.fixedUpdate(delta);
        }
    }

    @Override
    public void onDestroy() {
        for (ComponentLogicsScript script : scripts) {
            script.onDestroy();
        }
    }

    @Override
    public void handleSignal(Object signal) {
        for (ComponentLogicsScript script : scripts) {
            script.handleSignal(signal);
        }
    }

}
