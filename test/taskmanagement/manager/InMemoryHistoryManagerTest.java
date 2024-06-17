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
        assertEquals(task, history.getFirst(), "Задача в истории должна соответствовать добавленной");
    }

    @Test
    void testTaskHistoryMutability() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        SimpleTask task = new SimpleTask("Task", "Description");
        task.setId(1);

        historyManager.add(task);
        String modifiedTitle = "Modified Task";
        task.setTitle(modifiedTitle);
        List<BaseTask> history = historyManager.getHistory();

        assertEquals(modifiedTitle, history.getFirst().getTitle(), "Название задачи в истории должно изменяться");
    }

    @Test
    void testRemoveTaskFromHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        SimpleTask task1 = new SimpleTask("Task 1", "Description 1");
        SimpleTask task2 = new SimpleTask("Task 2", "Description 2");
        SimpleTask task3 = new SimpleTask("Task 3", "Description 3");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Удаление задач из начала истории
        historyManager.remove(task1.getId());
        List<BaseTask> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи после удаления из начала");
        assertEquals(task2, history.get(0), "Первая оставшаяся задача должна соответствовать второй добавленной");
        assertEquals(task3, history.get(1), "Вторая оставшаяся задача должна соответствовать третьей добавленной");

        // Восстанавливаем исходную историю задач для следующих тестов
        historyManager.add(task1);
        historyManager.add(task3);  // Добавляем task3 обратно

        // Удаление задач из середины истории
        historyManager.remove(task2.getId());
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи после удаления из середины");
        assertEquals(task1, history.get(0), "Первая оставшаяся задача должна соответствовать первой добавленной");
        assertEquals(task3, history.get(1), "Вторая оставшаяся задача должна соответствовать третьей добавленной");

        // Восстанавливаем исходную историю задач для следующих тестов
        historyManager.add(task2);
        historyManager.add(task3);  // Добавляем task3 обратно

        // Удаление задач из конца истории
        historyManager.remove(task3.getId());
        history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи после удаления из конца");
        assertEquals(task1, history.get(0), "Первая оставшаяся задача должна соответствовать первой добавленной");
        assertEquals(task2, history.get(1), "Вторая оставшаяся задача должна соответствовать второй добавленной");
    }

    @Test
    void testLinkedListIntegrity() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        SimpleTask task1 = new SimpleTask("Task 1", "Description 1");
        SimpleTask task2 = new SimpleTask("Task 2", "Description 2");
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<BaseTask> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать две задачи");
        assertEquals(task1, history.get(0), "Первая задача должна соответствовать первой добавленной");
        assertEquals(task2, history.get(1), "Вторая задача должна соответствовать второй добавленной");

        // Проверка ссылок на связный список
        historyManager.remove(task1.getId());
        history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления");
        assertEquals(task2, history.getFirst(), "Оставшаяся задача должна соответствовать второй добавленной");
    }

    @Test
    void testClearHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        SimpleTask task1 = new SimpleTask("Task 1", "Description 1");
        SimpleTask task2 = new SimpleTask("Task 2", "Description 2");
        SimpleTask task3 = new SimpleTask("Task 3", "Description 3");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<BaseTask> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать три задачи");

        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        historyManager.remove(task3.getId());

        history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления всех задач");
    }

    @Test
    void testUpdateTaskHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        SimpleTask task = new SimpleTask("Task", "Initial Description");
        task.setId(1);

        historyManager.add(task);

        task.setDescription("Updated Description");
        historyManager.add(task);

        List<BaseTask> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну запись задачи после обновления");
        assertEquals("Updated Description", history.getFirst().getDescription(), "Описание задачи в истории должно быть обновлено");
    }
}
