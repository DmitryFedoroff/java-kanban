package taskmanagement.manager;

import org.junit.jupiter.api.Test;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.BaseTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.Subtask;
import taskmanagement.status.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void testAddAndFindTaskById() {
        TaskManager taskManager = Managers.getDefault();

        SimpleTask task = new SimpleTask("Task", "Description");
        taskManager.addTask(task);
        BaseTask foundTask = taskManager.getTaskById(task.getId());

        assertNotNull(foundTask, "Задача должна быть найдена по ID");
        assertEquals(task, foundTask, "Найденная задача должна соответствовать добавленной");
    }

    @Test
    void testTaskIdConflict() {
        TaskManager taskManager = Managers.getDefault();

        SimpleTask task1 = new SimpleTask("Task1", "Description1");
        SimpleTask task2 = new SimpleTask("Task2", "Description2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertNotEquals(task1.getId(), task2.getId(), "ID задач не должны конфликтовать");
    }

    @Test
    void testTaskImmutabilityOnAdd() {
        TaskManager taskManager = Managers.getDefault();

        SimpleTask task = new SimpleTask("Task", "Description");
        taskManager.addTask(task);
        SimpleTask retrievedTask = (SimpleTask) taskManager.getTaskById(task.getId());

        assertEquals(task.getId(), retrievedTask.getId(), "ID задачи должно совпадать");
        assertEquals(task.getTitle(), retrievedTask.getTitle(), "Название задачи должно совпадать");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Описание задачи должно совпадать");
        assertEquals(task.getStatus(), retrievedTask.getStatus(), "Статус задачи должен совпадать");
    }

    @Test
    void testAddDifferentTaskTypes() {
        TaskManager taskManager = Managers.getDefault();

        SimpleTask simpleTask = new SimpleTask("Simple Task", "Simple Description");
        taskManager.addTask(simpleTask);
        BaseTask retrievedSimpleTask = taskManager.getTaskById(simpleTask.getId());
        assertNotNull(retrievedSimpleTask, "Простая задача должна быть найдена");

        EpicTask epicTask = new EpicTask("Epic Task", "Epic Description");
        taskManager.addEpic(epicTask);
        BaseTask retrievedEpicTask = taskManager.getEpicById(epicTask.getId());
        assertNotNull(retrievedEpicTask, "Эпик должен быть найден");

        Subtask subtask = new Subtask("Subtask", "Subtask Description", epicTask.getId());
        taskManager.addSubtask(subtask);
        BaseTask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask, "Подзадача должна быть найдена");
    }

    @Test
    void testDeleteTask() {
        TaskManager taskManager = Managers.getDefault();

        SimpleTask task = new SimpleTask("Task", "Description");
        taskManager.addTask(task);
        int taskId = task.getId();

        taskManager.deleteTask(taskId);
        BaseTask deletedTask = taskManager.getTaskById(taskId);

        assertNull(deletedTask, "Задача не должна быть найдена после удаления");
    }

    @Test
    void testDeleteEpicWithSubtasks() {
        TaskManager taskManager = Managers.getDefault();

        EpicTask epic = new EpicTask("Epic", "Epic Description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask 1 Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2 Description", epic.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.deleteEpic(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()), "Эпик не должен быть найден после удаления");
        assertNull(taskManager.getSubtaskById(subtask1.getId()), "Подзадача не должна быть найдена после удаления эпика");
        assertNull(taskManager.getSubtaskById(subtask2.getId()), "Подзадача не должна быть найдена после удаления эпика");
    }

    @Test
    void testSubtaskIdsIntegrityInEpic() {
        TaskManager taskManager = Managers.getDefault();

        EpicTask epic = new EpicTask("Epic", "Epic Description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Subtask Description", epic.getId());
        taskManager.addSubtask(subtask);

        taskManager.deleteSubtask(subtask.getId());

        assertFalse(epic.getSubtaskIds().contains(subtask.getId()), "ID подзадачи не должно оставаться в эпике после удаления");
    }

    @Test
    void testTaskFieldUpdates() {
        TaskManager taskManager = Managers.getDefault();

        SimpleTask task = new SimpleTask("Task", "Description");
        taskManager.addTask(task);
        int taskId = task.getId();

        task.setTitle("Updated Task");
        task.setDescription("Updated Description");
        task.setStatus(TaskStatus.DONE);

        taskManager.updateTask(task);
        SimpleTask updatedTask = (SimpleTask) taskManager.getTaskById(taskId);

        assertEquals("Updated Task", updatedTask.getTitle(), "Название задачи должно быть обновлено");
        assertEquals("Updated Description", updatedTask.getDescription(), "Описание задачи должно быть обновлено");
        assertEquals(TaskStatus.DONE, updatedTask.getStatus(), "Статус задачи должен быть обновлен");
    }

    @Test
    void testSubtaskRemovalIntegrity() {
        TaskManager taskManager = Managers.getDefault();

        EpicTask epic = new EpicTask("Epic", "Epic Description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        taskManager.deleteSubtask(subtask1.getId());
        taskManager.deleteSubtask(subtask2.getId());

        assertFalse(epic.getSubtaskIds().contains(subtask1.getId()), "ID подзадачи 1 не должно оставаться в эпике после удаления");
        assertFalse(epic.getSubtaskIds().contains(subtask2.getId()), "ID подзадачи 2 не должно оставаться в эпике после удаления");
    }

    @Test
    void testFieldUpdatesImpactOnManager() {
        TaskManager taskManager = Managers.getDefault();

        SimpleTask task = new SimpleTask("Task", "Description");
        taskManager.addTask(task);
        int taskId = task.getId();

        task.setTitle("Updated Task");
        task.setDescription("Updated Description");
        task.setStatus(TaskStatus.DONE);

        SimpleTask updatedTask = (SimpleTask) taskManager.getTaskById(taskId);
        assertEquals("Updated Task", updatedTask.getTitle(), "Название задачи должно быть обновлено в менеджере");
        assertEquals("Updated Description", updatedTask.getDescription(), "Описание задачи должно быть обновлено в менеджере");
        assertEquals(TaskStatus.DONE, updatedTask.getStatus(), "Статус задачи должен быть обновлен в менеджере");

        SimpleTask modifiedTask = new SimpleTask("Modified Task", "Modified Description");
        modifiedTask.setId(taskId);
        modifiedTask.setStatus(TaskStatus.NEW);

        updatedTask = (SimpleTask) taskManager.getTaskById(taskId);
        assertNotEquals(modifiedTask.getTitle(), updatedTask.getTitle(), "Название задачи не должно быть изменено через новый экземпляр");
        assertNotEquals(modifiedTask.getDescription(), updatedTask.getDescription(), "Описание задачи не должно быть изменено через новый экземпляр");
        assertNotEquals(modifiedTask.getStatus(), updatedTask.getStatus(), "Статус задачи не должен быть изменен через новый экземпляр");
    }
}
