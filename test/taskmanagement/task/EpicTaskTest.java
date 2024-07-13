package taskmanagement.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.manager.Managers;
import taskmanagement.manager.TaskManager;
import taskmanagement.status.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTaskTest {
    private TaskManager taskManager;
    private EpicTask epic;
    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        epic = new EpicTask("Epic", "Description");
        taskManager.addEpic(epic);

        subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        subtask2 = new Subtask("Subtask 2", "Description", epic.getId());
        subtask3 = new Subtask("Subtask 3", "Description", epic.getId());
    }

    @Test
    void testEpicStatusWithNewSubtasks() {
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен быть NEW, когда все подзадачи NEW");
    }

    @Test
    void testEpicStatusWithDoneSubtasks() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE, когда все подзадачи DONE");
    }

    @Test
    void testEpicStatusWithNewAndDoneSubtasks() {
        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.NEW);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, когда подзадачи NEW и DONE");
    }

    @Test
    void testEpicStatusWithInProgressSubtasks() {
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        subtask3.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, когда все подзадачи IN_PROGRESS");
    }
}
