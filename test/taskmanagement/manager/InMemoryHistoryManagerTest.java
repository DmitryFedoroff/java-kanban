package taskmanagement.manager;

import org.junit.jupiter.api.Test;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.BaseTask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void testAddTaskToHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        SimpleTask task = new SimpleTask("Task", "Description");

        historyManager.add(task);
        List<BaseTask> history = historyManager.getHistory();

        assertNotNull(history, "История не должна быть пустой");
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task, history.get(0), "Задача в истории должна соответствовать добавленной");
    }

    @Test
    void testTaskHistoryImmutability() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        SimpleTask task = new SimpleTask("Task", "Description");

        historyManager.add(task);
        task.setTitle("Modified Task");
        List<BaseTask> history = historyManager.getHistory();

        assertEquals("Task", history.get(0).getTitle(), "Title задачи в истории не должен изменяться");
    }

    @Test
    void testHistorySizeLimit() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        for (int i = 1; i <= 12; i++) {
            SimpleTask task = new SimpleTask("Task " + i, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }

        List<BaseTask> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История должна содержать не более 10 задач");

        for (int i = 3; i <= 12; i++) {
            assertEquals("Task " + i, history.get(i - 3).getTitle(), "Задача в истории должна соответствовать добавленной");
        }
    }
}
