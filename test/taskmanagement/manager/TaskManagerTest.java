package taskmanagement.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.status.TaskStatus;
import taskmanagement.task.BaseTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void testAddAndFindTaskById() {
        SimpleTask task = new SimpleTask("Task", "Description");
        task.setStartTime(LocalDateTime.now().plusMinutes(10));
        task.setDuration(Duration.ofMinutes(60));
        taskManager.addTask(task);
        BaseTask foundTask = taskManager.getTaskById(task.getId());

        assertNotNull(foundTask, "Задача должна быть найдена по ID");
        assertEquals(task, foundTask, "Найденная задача должна соответствовать добавленной");
    }

    @Test
    void testUpdateTask() {
        SimpleTask task = new SimpleTask("Task", "Description");
        task.setStartTime(LocalDateTime.now().plusMinutes(10));
        task.setDuration(Duration.ofMinutes(60));
        taskManager.addTask(task);

        task.setTitle("Updated Task");
        taskManager.updateTask(task);
        BaseTask updatedTask = taskManager.getTaskById(task.getId());

        assertEquals("Updated Task", updatedTask.getTitle(), "Название задачи должно быть обновлено");
    }

    @Test
    void testDeleteTask() {
        SimpleTask task = new SimpleTask("Task", "Description");
        task.setStartTime(LocalDateTime.now().plusMinutes(10));
        task.setDuration(Duration.ofMinutes(60));
        taskManager.addTask(task);
        int taskId = task.getId();

        taskManager.deleteTask(taskId);
        BaseTask deletedTask = taskManager.getTaskById(taskId);

        assertNull(deletedTask, "Задача не должна быть найдена после удаления");
    }

    @Test
    void testEpicStatusCalculation() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        subtask1.setStatus(TaskStatus.NEW);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask 3", "Description", epic.getId());
        subtask3.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask3);

        EpicTask foundEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, foundEpic.getStatus(), "Статус эпика должен быть IN_PROGRESS при наличии подзадачи в IN_PROGRESS");
    }

    @Test
    void testAddAndRetrieveSubtasks() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());
        taskManager.addSubtask(subtask2);

        assertEquals(2, taskManager.getSubtasksByEpicId(epic.getId()).size(), "Должно быть две подзадачи в эпике");
    }

    @Test
    void testTaskOverlappingValidation() {
        SimpleTask task1 = new SimpleTask("Task 1", "Description");
        task1.setStartTime(LocalDateTime.now().plusMinutes(10));
        task1.setDuration(Duration.ofMinutes(60));
        taskManager.addTask(task1);

        SimpleTask task2 = new SimpleTask("Task 2", "Description");
        task2.setStartTime(LocalDateTime.now().plusMinutes(30));
        task2.setDuration(Duration.ofMinutes(60));

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2), "Должно быть исключение при пересечении задач");
    }

    @Test
    void testEpicStatusWithNewSubtasks() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        subtask1.setStatus(TaskStatus.NEW);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());
        subtask2.setStatus(TaskStatus.NEW);
        taskManager.addSubtask(subtask2);

        EpicTask foundEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.NEW, foundEpic.getStatus(), "Статус эпика должен быть NEW при всех подзадачах со статусом NEW");
    }

    @Test
    void testEpicStatusWithDoneSubtasks() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        EpicTask foundEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.DONE, foundEpic.getStatus(), "Статус эпика должен быть DONE при всех подзадачах со статусом DONE");
    }

    @Test
    void testEpicStatusWithNewAndDoneSubtasks() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        subtask1.setStatus(TaskStatus.NEW);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.addSubtask(subtask2);

        EpicTask foundEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, foundEpic.getStatus(), "Статус эпика должен быть IN_PROGRESS при подзадачах со статусами NEW и DONE");
    }

    @Test
    void testEpicStatusWithInProgressSubtasks() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.addSubtask(subtask2);

        EpicTask foundEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, foundEpic.getStatus(), "Статус эпика должен быть IN_PROGRESS при всех подзадачах со статусом IN_PROGRESS");
    }

    @Test
    void testEmptyHistory() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        assertTrue(historyManager.getHistory().isEmpty(), "История задач должна быть пустой");
    }

    @Test
    void testDuplicateHistory() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        SimpleTask task = new SimpleTask("Task", "Description");
        task.setStartTime(LocalDateTime.now().plusMinutes(10));
        task.setDuration(Duration.ofMinutes(60));
        taskManager.addTask(task);
        historyManager.add(task);
        historyManager.add(task);

        List<BaseTask> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История задач не должна содержать дубликатов");
    }

    @Test
    void testRemoveFromHistory() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        SimpleTask task1 = new SimpleTask("Task 1", "Description 1");
        task1.setStartTime(LocalDateTime.now().plusMinutes(10));
        task1.setDuration(Duration.ofMinutes(60));
        SimpleTask task2 = new SimpleTask("Task 2", "Description 2");
        task2.setStartTime(LocalDateTime.now().plusMinutes(70));
        task2.setDuration(Duration.ofMinutes(60));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<BaseTask> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История задач должна содержать одну задачу после удаления");
        assertEquals(task2, history.getFirst(), "Оставшаяся задача должна быть task2 после удаления task1");

        historyManager.remove(task2.getId());
        assertTrue(historyManager.getHistory().isEmpty(), "История задач должна быть пустой после удаления всех задач");
    }
}
