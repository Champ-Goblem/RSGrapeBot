package stumpy3toes.api.script;

import stumpy3toes.api.task.Task;
import stumpy3toes.api.task.TaskSet;

import java.util.concurrent.atomic.AtomicReference;

public abstract class Script extends org.powerbot.script.PollingScript<ClientContext> {
    private final AtomicReference<Task> activeTask = new AtomicReference<Task>();
    private final AtomicReference<TaskSet> tasks = new AtomicReference<TaskSet>(new TaskSet());

    @Override
    public void start() {
        super.start();
        initTaskSet(tasks.get());
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void poll() {
        for (Task task : tasks.get().descendingSet()) {
            if (task.activate()) {
                if (activeTask() != task) {
                    setActiveTask(task);
                    task.pollTracker.start();
                }
                task.execute();
                return;
            }
        }
        setActiveTask(null);
    }

    private void setActiveTask(Task task) {
        if (activeTask() != null) {
            activeTask().pollTracker.stop();
        }
        activeTask.set(task);
    }

    protected Task activeTask() {
        return activeTask.get();
    }

    protected abstract void initTaskSet(TaskSet tasks);
}
