package com.heavybox.jtix.async;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.memory.MemoryPool;

public abstract class AsyncTask {

    protected Array<AsyncTask> prerequisites = new Array<>();
    protected boolean          inProgress    = false;
    protected boolean          complete      = false;

    public AsyncTask(AsyncTask... prerequisites) {
        for (final AsyncTask task : prerequisites) {
            addPrerequisite(task);
        }
    }

    public final void addPrerequisite(AsyncTask task) {
        if (task == null) return;
        if (inProgress)   throw new IllegalStateException("Cannot add prerequisite tasks to " + this.getClass().getSimpleName() + " while in progress.");
        if (complete)     throw new IllegalStateException("Cannot add prerequisite tasks to " + this.getClass().getSimpleName() + " after completed.");
        if (task == this) throw new IllegalArgumentException("A " + AsyncTask.class.getSimpleName() + " cannot be its own prerequisite.");
        if (task.isPrerequisite(this)) throw new IllegalArgumentException("Cyclic dependency: this " + this.getClass().getSimpleName() + " is a prerequisite of task: " + task + ". Cannot set task: " + task + " to be a prerequisite of " + this + ".");
        this.prerequisites.add(task);
    }

    public boolean isPrerequisite(AsyncTask task) {
        if (prerequisites.isEmpty()) return false;
        boolean found = false;
        for (AsyncTask prerequisite : prerequisites) {
            found |= (task == prerequisite) || prerequisite.isPrerequisite(task);
        }
        return found;
    }

    protected synchronized final void run() {
        if (complete) return;
        inProgress = true;
        onStart();
        task();
        onComplete();
        inProgress = false;
        complete = true;
    }

    public void onStart() {}
    public abstract void task();
    public void onComplete() {}

    public final boolean isComplete() {
        return complete;
    }
    public final boolean inProgress() { return inProgress; }

}
