package taskmanagement.manager;

import taskmanagement.task.BaseTask;
import java.util.List;

public interface HistoryManager {
    void add(BaseTask task);
    List<BaseTask> getHistory();
    int getViewCount(int taskId);
}
