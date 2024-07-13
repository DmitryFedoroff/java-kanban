package taskmanagement.manager;

import taskmanagement.exceptions.ManagerSaveException;
import taskmanagement.status.TaskStatus;
import taskmanagement.task.BaseTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.Subtask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String HEADER = "id,type,name,status,description,epic,startTime,duration";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> taskIds = new ArrayList<>();
        if (value != null && !value.isEmpty()) {
            String[] ids = value.split(",");
            for (String id : ids) {
                taskIds.add(Integer.parseInt(id));
            }
        }
        return taskIds;
    }

    private static BaseTask taskFromString(String value) {
        String[] fields = value.split(",");
        if (fields.length < 7) {
            throw new IllegalArgumentException("Неверный формат строки задачи: " + value);
        }
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        LocalDateTime startTime = fields[5].equals("null") ? null : LocalDateTime.parse(fields[5], BaseTask.DATE_TIME_FORMATTER);
        Duration duration = Duration.ofMinutes(Long.parseLong(fields[6]));

        return switch (type) {
            case "TASK" -> {
                SimpleTask task = new SimpleTask(title, description);
                task.setId(id);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                yield task;
            }
            case "EPIC" -> {
                EpicTask task = new EpicTask(title, description);
                task.setId(id);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                yield task;
            }
            case "SUBTASK" -> {
                if (fields.length < 8) {
                    throw new IllegalArgumentException("Неверный формат строки подзадачи: " + value);
                }
                int epicId = Integer.parseInt(fields[7]);
                Subtask task = new Subtask(title, description, epicId);
                task.setId(id);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                yield task;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        };
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break; // Достигли пустой строки перед историей
                }
                BaseTask task = taskFromString(line);
                manager.addTaskToCollection(task); // Добавляем задачи с проверкой пересечений
            }
            String historyLine = reader.readLine();
            if (historyLine != null && !historyLine.isEmpty()) {
                List<Integer> history = historyFromString(historyLine);
                for (Integer taskId : history) {
                    manager.getTaskById(taskId);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки менеджера задач", e);
        }
        return manager;
    }

    public static void main(String[] args) {
        // Создаем директорию data, если она не существует
        File directory = new File("data");
        if (!directory.exists() && !directory.mkdir()) {
            System.err.println("Не удалось создать директорию: " + directory.getAbsolutePath());
            return;
        }

        File file = new File(directory, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        SimpleTask task1 = new SimpleTask("Задача 1", "Описание задачи 1");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(60));
        SimpleTask task2 = new SimpleTask("Задача 2", "Описание задачи 2");
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(120));
        manager.addTask(task1);
        manager.addTask(task2);

        EpicTask epic1 = new EpicTask("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        subtask1.setStartTime(LocalDateTime.now().plusDays(1));
        subtask1.setDuration(Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        subtask2.setStartTime(LocalDateTime.now().plusDays(1).plusHours(1));
        subtask2.setDuration(Duration.ofMinutes(45));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1.getId());

        System.out.println("Первоначальный менеджер:");
        System.out.println("Задачи: " + manager.getAllTasks());
        System.out.println("Эпики: " + manager.getAllEpics());
        System.out.println("Подзадачи: " + manager.getAllSubtasks());
        System.out.println("История: " + manager.historyManager.getHistory());

        System.out.println("\nСодержимое файла:");
        try {
            System.out.println(Files.readString(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("\nЗагруженный менеджер:");
        System.out.println("Задачи: " + loadedManager.getAllTasks());
        System.out.println("Эпики: " + loadedManager.getAllEpics());
        System.out.println("Подзадачи: " + loadedManager.getAllSubtasks());
        System.out.println("История: " + loadedManager.historyManager.getHistory());

        // Вывод задач по приоритету
        printPrioritizedTasks(manager);
    }

    private static void printPrioritizedTasks(TaskManager manager) {
        System.out.println("\nЗадачи по приоритету:");
        List<BaseTask> prioritizedTasks = manager.getPrioritizedTasks();
        prioritizedTasks.forEach(System.out::println);
    }

    @Override
    public void addTask(BaseTask task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(EpicTask epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateTask(BaseTask task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(EpicTask epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public BaseTask getTaskById(int id) {
        BaseTask task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public EpicTask getEpicById(int id) {
        EpicTask epic = super.getEpicById(id);
        save();
        return epic;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();
            for (BaseTask task : getAllTasks()) {
                writer.write(taskToString(task));
                writer.newLine();
            }
            for (EpicTask epic : getAllEpics()) {
                writer.write(taskToString(epic));
                writer.newLine();
                for (Subtask subtask : getSubtasksByEpicId(epic.getId())) {
                    writer.write(taskToString(subtask));
                    writer.newLine();
                }
            }
            writer.newLine(); // Добавляем пустую строку перед историей
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения менеджера задач", e);
        }
    }

    private String taskToString(BaseTask task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(task instanceof SimpleTask ? "TASK" :
                task instanceof EpicTask ? "EPIC" : "SUBTASK").append(",");
        sb.append(task.getTitle()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task instanceof Subtask subtask) {
            sb.append(subtask.getEpicId()).append(",");
        }
        sb.append(task.getStartTime() != null ? task.getStartTime().format(BaseTask.DATE_TIME_FORMATTER) : "null").append(",");
        sb.append(task.getDuration().toMinutes());
        return sb.toString();
    }

    private String historyToString(HistoryManager manager) {
        List<BaseTask> history = manager.getHistory();
        return history.stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.joining(","));
    }

    private void addTaskToCollection(BaseTask task) {
        if (isTaskOverlapping(task)) {
            throw new IllegalArgumentException("Ошибка: задача пересекается с уже существующей задачей.");
        }
        if (task instanceof SimpleTask) {
            tasks.put(task.getId(), task);
        } else if (task instanceof EpicTask) {
            epics.put(task.getId(), (EpicTask) task);
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            subtasks.put(task.getId(), subtask);
            EpicTask epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtask(subtask.getId());
            }
        }
        prioritizedTasks.add(task);
    }

    private boolean isTaskOverlapping(BaseTask task) {
        return prioritizedTasks.stream().anyMatch(existingTask -> task.isOverlapping(existingTask));
    }
}
