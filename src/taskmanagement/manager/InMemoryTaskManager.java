package taskmanagement.manager;

import taskmanagement.task.BaseTask;
import taskmanagement.task.Subtask;
import taskmanagement.task.EpicTask;
import taskmanagement.status.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected final HashMap<Integer, BaseTask> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, EpicTask> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

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
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public EpicTask getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public void addTask(BaseTask task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        addToHistory(task);
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
        addToHistory(subtask);
    }

    @Override
    public void addEpic(EpicTask epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        addToHistory(epic);
    }

    @Override
    public void updateTask(BaseTask task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            addToHistory(task);
        }
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
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        }
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
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteAllEpics() {
        for (EpicTask epic : epics.values()) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(epic.getId());
        }
        epics.clear();
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

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                anyInProgress = true;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
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

    private void addToHistory(BaseTask task) {
        if (task != null) {
            historyManager.add(task);
        }
    }
}