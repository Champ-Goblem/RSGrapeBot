package stumpy3toes.api.task;

import stumpy3toes.api.script.ClientAccessor;
import stumpy3toes.api.script.ClientContext;
import stumpy3toes.api.util.RuntimeTracker;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Task extends ClientAccessor {
    private final AtomicReference<String> status = new AtomicReference<String>("");
    private final AtomicInteger priority = new AtomicInteger(0);
    private final AtomicBoolean paused = new AtomicBoolean(false);

    private final CopyOnWriteArrayList<TaskListener> listeners = new CopyOnWriteArrayList<TaskListener>();

    public final String name;
    public final RuntimeTracker pollTracker = new RuntimeTracker();

    public Task(ClientContext ctx, String name) {
        super(ctx);
        this.name = name;
    }

    public Task(ClientContext ctx, String name, int priority) {
        this(ctx, name);
        setPriority(priority);
    }

    public final boolean activate() {
        return !isPaused() && checks();
    }

    public final void execute() {
        poll();
    }

    public final Integer priority() {
        return priority.get();
    }

    public final String status() {
        return status.get();
    }

    public final boolean addListener(TaskListener listener) {
        return listeners.add(listener);
    }

    public final boolean removeListener(TaskListener listener) {
        return listeners.remove(listener);
    }

    public final boolean isPaused() {
        return paused.get();
    }

    public final void pause(boolean pause) {
        paused.set(pause);
    }

    public final void setPriority(int priority) {
        if (this.priority.get() != priority) {
            for (TaskListener listener : listeners) {
                listener.priorityChanging(this, priority());
            }
            this.priority.set(priority);
            for (TaskListener listener : listeners) {
                listener.priorityChanged(this, priority());
            }
        }
    }

    protected final void setStatus(String status) {
        if (!status.equals(this.status.get())) {
            this.status.set(status);
            for (TaskListener listener : listeners) {
                listener.statusChanged(this, status());
            }
        }
    }

    protected final void log(String log) {
        for (TaskListener listener : listeners) {
            listener.log(this, log);
        }
    }

    protected abstract boolean checks();
    protected abstract void poll();
}
