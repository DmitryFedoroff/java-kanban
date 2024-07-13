package taskmanagement.manager;

import taskmanagement.status.TaskStatus;
import taskmanagement.task.BaseTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.Subtask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, BaseTask> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, EpicTask> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<BaseTask> prioritizedTasks = new TreeSet<>(Comparator.comparing(BaseTask::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    protected int nextId = 1;

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
        if (isTaskOverlapping(task)) {
            throw new IllegalArgumentException("Ошибка: задача пересекается с уже существующей задачей.");
        }
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        addToHistory(task);
        addToPrioritizedTasks(task);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        EpicTask epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Ошибка: Эпик с ID " + subtask.getEpicId() + " не найден. Подзадача не добавлена.");
        }
        if (isTaskOverlapping(subtask)) {
            throw new IllegalArgumentException("Ошибка: подзадача пересекается с уже существующей задачей.");
        }
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic);
        addToHistory(subtask);
        addToPrioritizedTasks(subtask);
    }

    @Override
    public void addEpic(EpicTask epic) {
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        addToHistory(epic);
        addToPrioritizedTasks(epic);
    }

    @Override
    public void updateTask(BaseTask task) {
        if (tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            if (isTaskOverlapping(task)) {
                prioritizedTasks.add(tasks.get(task.getId()));
                throw new IllegalArgumentException("Ошибка: обновленная задача пересекается с уже существующей задачей.");
            }
            tasks.put(task.getId(), task);
            addToHistory(task);
            addToPrioritizedTasks(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            if (isTaskOverlapping(subtask)) {
                prioritizedTasks.add(subtasks.get(subtask.getId()));
                throw new IllegalArgumentException("Ошибка: обновленная подзадача пересекается с уже существующей задачей.");
            }
            subtasks.put(subtask.getId(), subtask);
            EpicTask epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
            } else {
                throw new IllegalArgumentException("Ошибка: Эпик для подзадачи с ID " + subtask.getId() + " не найден.");
            }
            addToHistory(subtask);
            addToPrioritizedTasks(subtask);
        } else {
            throw new IllegalArgumentException("Ошибка: Подзадача с ID " + subtask.getId() + " не найдена для обновления.");
        }
    }

    @Override
    public void updateEpic(EpicTask epic) {
        epics.put(epic.getId(), epic);
        addToHistory(epic);
        updatePrioritizedTasks(epic);
    }

    @Override
    public void deleteTask(int id) {
        BaseTask task = tasks.remove(id);
        if (task != null) {
            historyManager.remove(id);
            prioritizedTasks.remove(task);
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
            prioritizedTasks.remove(subtask);
        }
    }

    @Override
    public void deleteEpic(int id) {
        EpicTask epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().stream()
                    .map(subtasks::remove)
                    .filter(Objects::nonNull)
                    .forEach(subtask -> {
                        historyManager.remove(subtask.getId());
                        prioritizedTasks.remove(subtask);
                    });
            historyManager.remove(id);
            prioritizedTasks.remove(epic);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        });
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().stream()
                    .map(subtasks::remove)
                    .filter(Objects::nonNull)
                    .forEach(subtask -> {
                        historyManager.remove(subtask.getId());
                        prioritizedTasks.remove(subtask);
                    });
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        });
        epics.clear();
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        return epics.get(epicId).getSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    private void updateEpicStatus(EpicTask epic) {
        List<Subtask> subtasksList = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();

        if (subtasksList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subtasksList.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);
        boolean allDone = subtasksList.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);
        boolean anyInProgress = subtasksList.stream().anyMatch(subtask -> subtask.getStatus() == TaskStatus.IN_PROGRESS);
        boolean anyDone = subtasksList.stream().anyMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (anyInProgress || anyDone) {
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

    private void addToPrioritizedTasks(BaseTask task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void updatePrioritizedTasks(BaseTask task) {
        prioritizedTasks.remove(task);
        addToPrioritizedTasks(task);
    }

    public List<BaseTask> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isTaskOverlapping(BaseTask task) {
        return prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getId() != task.getId())
                .anyMatch(existingTask -> existingTask.isOverlapping(task));
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}
