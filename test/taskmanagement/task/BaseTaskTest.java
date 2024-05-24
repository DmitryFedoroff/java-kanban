package taskmanagement.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseTaskTest {

    @Test
    void testEquals() {
        SimpleTask task1 = new SimpleTask("Task1", "Description1");
        SimpleTask task2 = new SimpleTask("Task2", "Description2");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Задачи должны быть равны при одинаковом ID");

        EpicTask epic1 = new EpicTask("Epic1", "Description1");
        EpicTask epic2 = new EpicTask("Epic2", "Description2");
        epic1.setId(2);
        epic2.setId(2);
        assertEquals(epic1, epic2, "Эпики должны быть равны при одинаковом ID");

        Subtask subtask1 = new Subtask("Subtask1", "Description1", 1);
        Subtask subtask2 = new Subtask("Subtask2", "Description2", 1);
        subtask1.setId(3);
        subtask2.setId(3);
        assertEquals(subtask1, subtask2, "Подзадачи должны быть равны при одинаковом ID");
    }
}
