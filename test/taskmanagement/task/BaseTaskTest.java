package taskmanagement.task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BaseTaskTest {
    @Test
    void testEquals() {
        SimpleTask task1 = new SimpleTask("Task1", "Description1");
        task1.setId(1);
        SimpleTask task2 = new SimpleTask("Task1", "Description1");
        task2.setId(1);

        assertEquals(task1, task2, "Задачи должны быть равны при одинаковом ID");
    }
}
