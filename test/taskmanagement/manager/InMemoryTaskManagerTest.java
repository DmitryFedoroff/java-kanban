package taskmanagement.manager;

import org.junit.jupiter.api.Test;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.BaseTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.Subtask;

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
        assertEquals(task.getTitle(), retrievedTask.getTitle(), "Title задачи должно совпадать");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Description задачи должно совпадать");
        assertEquals(task.getStatus(), retrievedTask.getStatus(), "Status задачи должно совпадать");
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
}
