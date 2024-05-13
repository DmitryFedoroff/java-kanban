package taskmanagement.manager;

import taskmanagement.task.BaseTask;
import taskmanagement.task.Subtask;
import taskmanagement.task.EpicTask;
import taskmanagement.status.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int nextId = 1;
    private final HashMap<Integer, BaseTask> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epics = new HashMap<>();

    public List<BaseTask> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public List<EpicTask> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public BaseTask getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public EpicTask getEpicById(int id) {
        return epics.get(id);
    }

    public void addTask(BaseTask task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    public void addSubtask(Subtask subtask) {
        EpicTask epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Ошибка: Эпик с ID " + subtask.getEpicId() + " не найден. Подзадача не добавлена.");
            return;
        }
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic);
    }

    public void addEpic(EpicTask epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    public void updateTask(BaseTask task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            EpicTask epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            } else {
                System.out.println("Ошибка: Эпик для подзадачи с ID " + subtask.getId() + " не найден.");
            }
        } else {
            System.out.println("Ошибка: Подзадача с ID " + subtask.getId() + " не найдена для обновления.");
        }
    }

    public void updateEpic(EpicTask epic) {
        epics.put(epic.getId(), epic);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            EpicTask epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic);
            }
        }
    }

    public void deleteEpic(int id) {
        EpicTask epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (EpicTask epic : epics.values()) {
            epic.getSubtaskIds().clear();
        }
    }

    public void deleteAllEpics() {
        List<Integer> epicIds = new ArrayList<>(epics.keySet());
        for (Integer epicId : epicIds) {
            deleteEpic(epicId);
        }
    }

    private void updateEpicStatus(EpicTask epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                anyInProgress = true;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (anyInProgress) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }
}
