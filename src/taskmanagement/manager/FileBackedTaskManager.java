package taskmanagement.manager;

import taskmanagement.exceptions.ManagerSaveException;
import taskmanagement.status.TaskStatus;
import taskmanagement.task.BaseTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.Subtask;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
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
                writer.write(task.toString());
                writer.newLine();
            }
            for (EpicTask epic : getAllEpics()) {
                writer.write(epic.toString());
                writer.newLine();
                for (Subtask subtask : getSubtasksByEpicId(epic.getId())) {
                    writer.write(subtask.toString());
                    writer.newLine();
                }
            }
            writer.newLine(); // Добавляем пустую строку перед историей
            writer.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения менеджера задач", e);
        }
    }

    private String historyToString(HistoryManager manager) {
        List<BaseTask> history = manager.getHistory();
        return history.stream()
                .map(task -> String.valueOf(task.getId()))
                .collect(Collectors.joining(","));
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
        if (fields.length < 5) {
            throw new IllegalArgumentException("Неверный формат строки задачи: " + value);
        }
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        return switch (type) {
            case "TASK" -> {
                SimpleTask task = new SimpleTask(title, description);
                task.setId(id);
                task.setStatus(status);
                yield task;
            }
            case "EPIC" -> {
                EpicTask task = new EpicTask(title, description);
                task.setId(id);
                task.setStatus(status);
                yield task;
            }
            case "SUBTASK" -> {
                if (fields.length < 6) {
                    throw new IllegalArgumentException("Неверный формат строки подзадачи: " + value);
                }
                int epicId = Integer.parseInt(fields[5]);
                Subtask task = new Subtask(title, description, epicId);
                task.setId(id);
                task.setStatus(status);
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
                switch (task) {
                    case SimpleTask simpleTask -> manager.addTask(simpleTask);
                    case EpicTask epicTask -> manager.addEpic(epicTask);
                    case Subtask subtask -> manager.addSubtask(subtask);
                    default -> throw new IllegalStateException("Неизвестное значение: " + task);
                }
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
        SimpleTask task2 = new SimpleTask("Задача 2", "Описание задачи 2");
        manager.addTask(task1);
        manager.addTask(task2);

        EpicTask epic1 = new EpicTask("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
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
    }
}