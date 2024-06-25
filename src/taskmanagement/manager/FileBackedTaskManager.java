package taskmanagement.manager;

import taskmanagement.exceptions.ManagerSaveException;
import taskmanagement.status.TaskStatus;
import taskmanagement.task.BaseTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.Subtask;

import java.io.*;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

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

    protected void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (BaseTask task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }
            for (EpicTask epic : getAllEpics()) {
                writer.write(taskToString(epic) + "\n");
                for (Subtask subtask : getSubtasksByEpicId(epic.getId())) {
                    writer.write(taskToString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения менеджера задач", e);
        }
    }

    private String taskToString(BaseTask task) {
        String[] taskData = new String[]{
                String.valueOf(task.getId()),
                task instanceof SimpleTask ? "TASK" :
                        task instanceof EpicTask ? "EPIC" :
                                task instanceof Subtask ? "SUBTASK" : "",
                task.getTitle(),
                task.getStatus().name(),
                task.getDescription(),
                task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : ""
        };
        return String.join(",", taskData);
    }

    private static BaseTask taskFromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        BaseTask task = switch (type) {
            case "TASK" -> new SimpleTask(title, description);
            case "EPIC" -> new EpicTask(title, description);
            case "SUBTASK" -> {
                int epicId = Integer.parseInt(fields[5]);
                yield new Subtask(title, description, epicId);
            }
            default -> null;
        };
        if (task != null) {
            task.setId(id);
            task.setStatus(status);
        }
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");
            for (int i = 1; i < lines.length; i++) {
                BaseTask task = taskFromString(lines[i]);
                if (task instanceof SimpleTask) {
                    manager.addTask(task);
                } else if (task instanceof EpicTask) {
                    manager.addEpic((EpicTask) task);
                } else if (task instanceof Subtask) {
                    manager.addSubtask((Subtask) task);
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

        // Создание задач, эпиков и подзадач
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

        // Загрузка менеджера из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        // Проверка загруженных данных
        System.out.println("Загруженные задачи:");
        loadedManager.getAllTasks().forEach(System.out::println);
        loadedManager.getAllEpics().forEach(System.out::println);
        loadedManager.getAllSubtasks().forEach(System.out::println);
    }
}
