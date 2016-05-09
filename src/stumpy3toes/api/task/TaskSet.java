package stumpy3toes.api.task;

import java.util.Comparator;
import java.util.TreeSet;

public class TaskSet extends TreeSet<Task> implements TaskListener {
    public TaskSet() {
        super(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.priority().compareTo(o2.priority());
            }
        });
    }

    @Override
    public boolean add(Task task) {
        for (Task t : descendingSet()) {
            if (t.priority() == task.priority()) {
                task.setPriority(task.priority() - 1);
            }
        }
        if (super.add(task)) {
            task.addListener(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (super.remove(o)) {
            if (o instanceof Task) {
                ((Task) o).removeListener(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public void priorityChanging(Task task, int priority) {
        super.remove(task);
    }

    @Override
    public void priorityChanged(Task task, int priority) {
        super.add(task);
    }

    @Override
    public void statusChanged(Task task, String status) {}
    @Override
    public void log(Task task, String log) {}
}
