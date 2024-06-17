package taskmanagement.task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubtaskTest {
    @Test
    void testCannotSetSelfAsEpic() {
        Subtask subtask = new Subtask("Subtask", "Description", 1);
        assertThrows(IllegalArgumentException.class, () -> {
            subtask.setId(1);
        }, "Подзадача не может быть своим же эпиком");
    }
}
