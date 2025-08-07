package com.heavybox.jtix.ecs;

public abstract class ComponentLogicsScript implements ComponentLogics {

    protected Entity  entity = null;
    protected boolean active = true;

    final void setEntity(final Entity entity) {
        if (this.entity != null) throw new ECSException(Entity.class.getSimpleName() + " " + this.entity + " is already set for this" + ComponentLogicsScript.class.getSimpleName());
        else this.entity = entity;
    }

    @Override
    public void handleSignal(Object signal) { }
}
