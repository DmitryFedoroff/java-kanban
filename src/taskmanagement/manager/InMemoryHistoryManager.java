package taskmanagement.manager;

import taskmanagement.task.BaseTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<BaseTask> history = new ArrayList<>();
    private final HashMap<Integer, Integer> viewCounts = new HashMap<>();

    @Override
    public void add(BaseTask task) {
        if (history.size() == 10) {
            history.remove(0);
        }
        history.add(task);
        viewCounts.put(task.getId(), viewCounts.getOrDefault(task.getId(), 0) + 1);
    }

    @Override
    public List<BaseTask> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public int getViewCount(int taskId) {
        return viewCounts.getOrDefault(taskId, 0);
    }
}
