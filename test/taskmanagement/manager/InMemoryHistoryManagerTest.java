package taskmanagement.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.task.BaseTask;
import taskmanagement.task.SimpleTask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private SimpleTask task1;
    private SimpleTask task2;
    private SimpleTask task3;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new SimpleTask("Task 1", "Description 1");
        task1.setId(1);
        task2 = new SimpleTask("Task 2", "Description 2");
        task2.setId(2);
        task3 = new SimpleTask("Task 3", "Description 3");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
    }

    @Test
    void testAddTaskToHistory() {
        List<BaseTask> history = historyManager.getHistory();

        assertNotNull(history, "История не должна быть пустой");
        assertEquals(3, history.size(), "История должна содержать три задачи");
        assertEquals(task1, history.get(0), "Первая задача должна быть task1");
        assertEquals(task2, history.get(1), "Вторая задача должна быть task2");
        assertEquals(task3, history.get(2), "Третья задача должна быть task3");
    }

    @Test
    void testDuplicateHistory() {
        historyManager.add(task1);
        List<BaseTask> history = historyManager.getHistory();

        assertEquals(3, history.size(), "История не должна содержать дубликаты задач");
    }

    @Test
    void testRemoveTaskFromHistory() {
        // Удаление задач из начала истории
        historyManager.remove(task1.getId());
        List<BaseTask> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи после удаления из начала");
        assertEquals(task2, history.get(0), "Первая оставшаяся задача должна соответствовать второй добавленной");
        assertEquals(task3, history.get(1), "Вторая оставшаяся задача должна соответствовать третьей добавленной");

        // Удаление задач из середины истории
        historyManager.remove(task2.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления из середины");
        assertEquals(task3, history.getFirst(), "Единственная оставшаяся задача должна соответствовать третьей добавленной");

        // Удаление задач из конца истории
        historyManager.add(task2); // Восстанавливаем task2 для теста удаления из конца
        historyManager.remove(task3.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления из конца");
        assertEquals(task2, history.getFirst(), "Единственная оставшаяся задача должна соответствовать второй добавленной");
    }

    @Test
    void testClearHistory() {
        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        historyManager.remove(task3.getId());

        List<BaseTask> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления всех задач");
    }
}
