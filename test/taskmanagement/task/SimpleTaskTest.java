package taskmanagement.task;

import org.junit.jupiter.api.Test;
import taskmanagement.status.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SimpleTaskTest {

    @Test
    void testTaskCreation() {
        SimpleTask task = new SimpleTask("Test Task", "Test Description");

        assertNotNull(task, "Задача не должна быть null");
        assertEquals("Test Task", task.getTitle(), "Название задачи должно совпадать");
        assertEquals("Test Description", task.getDescription(), "Описание задачи должно совпадать");
        assertEquals(TaskStatus.NEW, task.getStatus(), "Статус задачи должен быть NEW по умолчанию");
    }

    @Test
    void testTaskSetters() {
        SimpleTask task = new SimpleTask("Test Task", "Test Description");
        task.setTitle("Updated Task");
        task.setDescription("Updated Description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals("Updated Task", task.getTitle(), "Название задачи должно быть обновлено");
        assertEquals("Updated Description", task.getDescription(), "Описание задачи должно быть обновлено");
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus(), "Статус задачи должен быть обновлен");
    }

    @Test
    void testTaskEquality() {
        SimpleTask task1 = new SimpleTask("Task", "Description");
        task1.setId(1);
        SimpleTask task2 = new SimpleTask("Task", "Description");
        task2.setId(1);

        assertEquals(task1, task2, "Задачи должны быть равны при одинаковом ID");
    }

    @Test
    void testTaskInequality() {
        SimpleTask task1 = new SimpleTask("Task", "Description");
        task1.setId(1);
        SimpleTask task2 = new SimpleTask("Task", "Description");
        task2.setId(2);

        assertNotEquals(task1, task2, "Задачи должны быть не равны при разных ID");
    }

    @Test
    void testTaskToString() {
        SimpleTask task = new SimpleTask("Task", "Description");
        task.setId(1);
        String expectedString = "1,TASK,Task,NEW,Description,null,0";
        assertEquals(expectedString, task.toString(), "Метод toString должен возвращать корректное строковое представление задачи");
    }
}
