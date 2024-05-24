package taskmanagement.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {

    @Test
    void testCannotAddEpicAsSubtask() {
        EpicTask epic = new EpicTask("Epic", "Description");
        epic.setId(1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(epic.getId());
        }, "Эпик не может быть добавлен в виде подзадачи к самому себе");
        assertEquals("Эпик не может быть добавлен в виде подзадачи к самому себе", exception.getMessage());
    }
}
