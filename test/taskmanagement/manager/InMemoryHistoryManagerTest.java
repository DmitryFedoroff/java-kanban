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
        task.setId(1);

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
        task.setId(1);

        historyManager.add(task);
        task.setTitle("Modified Task");
        List<BaseTask> history = historyManager.getHistory();

        assertEquals("Task", history.get(0).getTitle(), "Название задачи в истории не должно изменяться");
    }

    @Test
    void testRemoveTaskFromHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        SimpleTask task1 = new SimpleTask("Task 1", "Description 1");
        SimpleTask task2 = new SimpleTask("Task 2", "Description 2");
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        List<BaseTask> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления");
        assertEquals(task2, history.get(0), "Оставшаяся задача должна соответствовать второй добавленной");
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
        assertEquals(12, history.size(), "История должна содержать все 12 задач");

        for (int i = 1; i <= 12; i++) {
            assertEquals("Task " + i, history.get(i - 1).getTitle(), "Задача в истории должна соответствовать добавленной");
        }
    }
}
