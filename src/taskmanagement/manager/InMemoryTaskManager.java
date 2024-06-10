package taskmanagement.manager;

import taskmanagement.task.BaseTask;
import taskmanagement.task.Subtask;
import taskmanagement.task.EpicTask;
import taskmanagement.status.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private final HashMap<Integer, BaseTask> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, EpicTask> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<BaseTask> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<EpicTask> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public BaseTask getTaskById(int id) {
        BaseTask task = tasks.get(id);
        if (task != null) {
            addToHistory(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            addToHistory(subtask);
        }
        return subtask;
    }

    @Override
    public EpicTask getEpicById(int id) {
        EpicTask epic = epics.get(id);
        if (epic != null) {
            addToHistory(epic);
        }
        return epic;
    }

    @Override
    public void addTask(BaseTask task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
    }

    @Override
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

    @Override
    public void addEpic(EpicTask epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(BaseTask task) {
        tasks.put(task.getId(), task);
        addToHistory(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            EpicTask epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            } else {
                System.out.println("Ошибка: Эпик для подзадачи с ID " + subtask.getId() + " не найден.");
            }
            addToHistory(subtask);
        } else {
            System.out.println("Ошибка: Подзадача с ID " + subtask.getId() + " не найдена для обновления.");
        }
    }

    @Override
    public void updateEpic(EpicTask epic) {
        epics.put(epic.getId(), epic);
        addToHistory(epic);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            EpicTask epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        EpicTask epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        for (EpicTask epic : epics.values()) {
            epic.getSubtaskIds().clear();
        }
    }

    @Override
    public void deleteAllEpics() {
        List<Integer> epicIds = new ArrayList<>(epics.keySet());
        for (Integer epicId : epicIds) {
            deleteEpic(epicId);
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (int subtaskId : epics.get(epicId).getSubtaskIds()) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    private void updateEpicStatus(EpicTask epic) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;
        boolean anyNotDone = false;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                anyInProgress = true;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == TaskStatus.NEW) {
                anyNotDone = true;
            }
        }

        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (anyInProgress || anyNotDone) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    private void addToHistory(BaseTask task) {
        historyManager.add(task);
    }
}
