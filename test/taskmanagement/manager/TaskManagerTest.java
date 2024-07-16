package taskmanagement.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.status.TaskStatus;
import taskmanagement.task.BaseTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.Subtask;
import taskmanagement.util.TimeUtils;

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
        SimpleTask task = createSimpleTask("Task", "Description", 10, 60);
        taskManager.addTask(task);
        BaseTask foundTask = taskManager.getTaskById(task.getId());

        assertNotNull(foundTask, "Задача должна быть найдена по ID");
        assertEquals(task, foundTask, "Найденная задача должна соответствовать добавленной");
    }

    @Test
    void testUpdateTask() {
        SimpleTask task = createSimpleTask("Task", "Description", 10, 60);
        taskManager.addTask(task);

        task.setTitle("Updated Task");
        taskManager.updateTask(task);
        BaseTask updatedTask = taskManager.getTaskById(task.getId());

        assertEquals("Updated Task", updatedTask.getTitle(), "Название задачи должно быть обновлено");
    }

    @Test
    void testDeleteTask() {
        SimpleTask task = createSimpleTask("Task", "Description", 10, 60);
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

        addSubtaskToEpic(epic, "Subtask 1", TaskStatus.NEW);
        addSubtaskToEpic(epic, "Subtask 2", TaskStatus.DONE);
        addSubtaskToEpic(epic, "Subtask 3", TaskStatus.IN_PROGRESS);

        EpicTask foundEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, foundEpic.getStatus(), "Статус эпика должен быть IN_PROGRESS при наличии подзадачи в IN_PROGRESS");
    }

    @Test
    void testAddAndRetrieveSubtasks() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        addSubtaskToEpic(epic, "Subtask 1", TaskStatus.NEW);
        addSubtaskToEpic(epic, "Subtask 2", TaskStatus.NEW);

        assertEquals(2, taskManager.getSubtasksByEpicId(epic.getId()).size(), "Должно быть две подзадачи в эпике");
    }

    @Test
    void testTaskOverlappingValidation() {
        SimpleTask task1 = createSimpleTask("Task 1", "Description", 10, 60);
        taskManager.addTask(task1);

        SimpleTask task2 = createSimpleTask("Task 2", "Description", 30, 60);

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2), "Должно быть исключение при пересечении задач");
    }

    @Test
    void testEpicStatusWithNewSubtasks() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        addSubtaskToEpic(epic, "Subtask 1", TaskStatus.NEW);
        addSubtaskToEpic(epic, "Subtask 2", TaskStatus.NEW);

        EpicTask foundEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.NEW, foundEpic.getStatus(), "Статус эпика должен быть NEW при всех подзадачах со статусом NEW");
    }

    @Test
    void testEpicStatusWithDoneSubtasks() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        addSubtaskToEpic(epic, "Subtask 1", TaskStatus.DONE);
        addSubtaskToEpic(epic, "Subtask 2", TaskStatus.DONE);

        EpicTask foundEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.DONE, foundEpic.getStatus(), "Статус эпика должен быть DONE при всех подзадачах со статусом DONE");
    }

    @Test
    void testEpicStatusWithNewAndDoneSubtasks() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        addSubtaskToEpic(epic, "Subtask 1", TaskStatus.NEW);
        addSubtaskToEpic(epic, "Subtask 2", TaskStatus.DONE);

        EpicTask foundEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, foundEpic.getStatus(), "Статус эпика должен быть IN_PROGRESS при подзадачах со статусами NEW и DONE");
    }

    @Test
    void testEpicStatusWithInProgressSubtasks() {
        EpicTask epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        addSubtaskToEpic(epic, "Subtask 1", TaskStatus.IN_PROGRESS);
        addSubtaskToEpic(epic, "Subtask 2", TaskStatus.IN_PROGRESS);

        EpicTask foundEpic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, foundEpic.getStatus(), "Статус эпика должен быть IN_PROGRESS при всех подзадачах со статусом IN_PROGRESS");
    }

    @Test
    void testEmptyHistory() {
        List<BaseTask> history = taskManager.getPrioritizedTasks();
        assertTrue(history.isEmpty(), "История задач должна быть пустой");
    }

    @Test
    void testDuplicateHistory() {
        SimpleTask task = createSimpleTask("Task", "Description", 10, 60);
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());

        List<BaseTask> history = taskManager.getPrioritizedTasks();
        assertEquals(1, history.size(), "История задач не должна содержать дубликатов");
    }

    @Test
    void testRemoveFromHistory() {
        SimpleTask task1 = createSimpleTask("Task 1", "Description 1", 10, 60);
        SimpleTask task2 = createSimpleTask("Task 2", "Description 2", 70, 60);
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.deleteTask(task1.getId());

        List<BaseTask> history = taskManager.getPrioritizedTasks();
        assertEquals(1, history.size(), "История задач должна содержать одну задачу после удаления");
        assertEquals(task2, history.getFirst(), "Оставшаяся задача должна быть task2 после удаления task1");

        taskManager.deleteTask(task2.getId());
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "История задач должна быть пустой после удаления всех задач");
    }

    @Test
    void testTimeUtils() {
        LocalDateTime time = LocalDateTime.now();
        String formattedTime = TimeUtils.timeToString(time);

        assertEquals(time.format(BaseTask.DATE_TIME_FORMATTER), formattedTime, "Время должно быть отформатировано корректно");
    }

    private SimpleTask createSimpleTask(String title, String description, int startMinutesFromNow, int durationMinutes) {
        SimpleTask task = new SimpleTask(title, description);
        task.setStartTime(LocalDateTime.now().plusMinutes(startMinutesFromNow));
        task.setDuration(Duration.ofMinutes(durationMinutes));
        return task;
    }

    private void addSubtaskToEpic(EpicTask epic, String title, TaskStatus status) {
        Subtask subtask = new Subtask(title, epic.getDescription(), epic.getId());
        subtask.setStatus(status);
        taskManager.addSubtask(subtask);
    }
}
