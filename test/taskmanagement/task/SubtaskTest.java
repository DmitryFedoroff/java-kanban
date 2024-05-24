package taskmanagement.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void testCannotSetSelfAsEpic() {
        EpicTask epic = new EpicTask("Epic", "Description");
        epic.setId(1);
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());
        subtask.setId(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Subtask("Subtask", "Description", subtask.getId());
        }, "Подзадача не может быть своим же эпиком");
        assertEquals("Подзадача не может быть своим же эпиком", exception.getMessage());
    }
}
