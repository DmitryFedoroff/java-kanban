package taskmanagement.manager;

import taskmanagement.exceptions.NotFoundException;
import taskmanagement.task.BaseTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.Subtask;

import java.util.List;

public interface TaskManager {
    List<BaseTask> getAllTasks();

    List<Subtask> getAllSubtasks();

    List<EpicTask> getAllEpics();

    BaseTask getTaskById(int id) throws NotFoundException;

    Subtask getSubtaskById(int id) throws NotFoundException;

    EpicTask getEpicById(int id) throws NotFoundException;

    void addTask(BaseTask task);

    void addSubtask(Subtask subtask);

    void addEpic(EpicTask epic);

    void updateTask(BaseTask task) throws NotFoundException;

    void updateSubtask(Subtask subtask) throws NotFoundException;

    void updateEpic(EpicTask epic) throws NotFoundException;

    void deleteTask(int id) throws NotFoundException;

    void deleteSubtask(int id) throws NotFoundException;

    void deleteEpic(int id) throws NotFoundException;

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    List<Subtask> getSubtasksByEpicId(int epicId) throws NotFoundException;

    List<BaseTask> getPrioritizedTasks();
}
