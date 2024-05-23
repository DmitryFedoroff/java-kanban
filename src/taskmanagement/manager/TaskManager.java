package taskmanagement.manager;

import taskmanagement.task.BaseTask;
import taskmanagement.task.Subtask;
import taskmanagement.task.EpicTask;

import java.util.List;

public interface TaskManager {
    List<BaseTask> getAllTasks();
    List<Subtask> getAllSubtasks();
    List<EpicTask> getAllEpics();
    BaseTask getTaskById(int id);
    Subtask getSubtaskById(int id);
    EpicTask getEpicById(int id);
    void addTask(BaseTask task);
    void addSubtask(Subtask subtask);
    void addEpic(EpicTask epic);
    void updateTask(BaseTask task);
    void updateSubtask(Subtask subtask);
    void updateEpic(EpicTask epic);
    void deleteTask(int id);
    void deleteSubtask(int id);
    void deleteEpic(int id);
    void deleteAllTasks();
    void deleteAllSubtasks();
    void deleteAllEpics();
}
