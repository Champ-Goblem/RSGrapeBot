package stumpy3toes.api.task;

public interface TaskListener {
    void log(Task task, String log);
    void priorityChanging(Task task, int priority);
    void priorityChanged(Task task, int priority);
    void statusChanged(Task task, String status);
}
