package taskmanagement.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.Subtask;
import taskmanagement.task.BaseTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() {
        File directory = new File("data");
        if (!directory.exists() && !directory.mkdir()) {
            fail("Не удалось создать директорию: " + directory.getAbsolutePath());
        }

        file = new File(directory, "tasks-test.csv");
        manager = new FileBackedTaskManager(file);
    }

    @AfterEach
    void tearDown() {
        boolean deleted = file.delete();
        if (!deleted) {
            System.err.println("Не удалось удалить тестовый файл: " + file.getAbsolutePath());
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        manager.save();
        String fileContent = Files.readString(file.toPath());
        assertTrue(fileContent.startsWith("id,type,name,status,description,epic"), "Файл должен начинаться с заголовка");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getAllTasks().isEmpty(), "Загруженный менеджер не должен содержать задач");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Загруженный менеджер не должен содержать эпиков");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Загруженный менеджер не должен содержать подзадач");
    }

    @Test
    void testSaveAndLoadTasks() throws IOException {
        SimpleTask task1 = new SimpleTask("Задача 1", "Описание задачи 1");
        SimpleTask task2 = new SimpleTask("Задача 2", "Описание задачи 2");
        manager.addTask(task1);
        manager.addTask(task2);

        EpicTask epic = new EpicTask("Эпик", "Описание эпика");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epic.getId());
        manager.addSubtask(subtask);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        manager.save();

        System.out.println("Содержание файла:");
        System.out.println(Files.readString(file.toPath()));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(2, loadedManager.getAllTasks().size(), "Должно быть загружено две задачи");
        assertEquals(1, loadedManager.getAllEpics().size(), "Должен быть загружен один эпик");
        assertEquals(1, loadedManager.getAllSubtasks().size(), "Должна быть загружена одна подзадача");

        List<BaseTask> history = loadedManager.historyManager.getHistory();
        assertEquals(4, history.size(), "История должна содержать четыре задачи");
        assertEquals(task1.getId(), history.get(0).getId(), "Первая задача в истории должна совпадать");
        assertEquals(task2.getId(), history.get(1).getId(), "Вторая задача в истории должна совпадать");
        assertEquals(epic.getId(), history.get(2).getId(), "Третья задача в истории должна быть эпиком");
        assertEquals(subtask.getId(), history.get(3).getId(), "Четвертая задача в истории должна быть подзадачей");
    }

    @Test
    void testSaveAndLoadWithHistory() throws IOException {
        SimpleTask task = new SimpleTask("Задача", "Описание задачи");
        EpicTask epic = new EpicTask("Эпик", "Описание эпика");
        manager.addTask(task);
        manager.addEpic(epic);

        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        manager.getTaskById(task.getId()); // Повторный вызов для проверки отсутствия дубликатов в истории

        manager.save();

        System.out.println("Содержание файла:");
        System.out.println(Files.readString(file.toPath()));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        List<BaseTask> history = loadedManager.historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать две задачи");
        assertEquals(task.getId(), history.get(0).getId(), "Первая задача в истории должна быть обычной задачей");
        assertEquals(epic.getId(), history.get(1).getId(), "Вторая задача в истории должна быть эпиком");
    }

    @Test
    void testLoadFromEmptyFile() throws IOException {
        Files.writeString(file.toPath(), "id,type,name,status,description,epic");
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getAllTasks().isEmpty(), "Не должно быть задач");
        assertTrue(loadedManager.getAllEpics().isEmpty(), "Не должно быть эпиков");
        assertTrue(loadedManager.getAllSubtasks().isEmpty(), "Не должно быть подзадач");
        assertTrue(loadedManager.historyManager.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    void testSaveAndLoadEpicWithSubtasks() throws IOException {
        EpicTask epic = new EpicTask("Эпик", "Описание эпика");
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        manager.save();

        System.out.println("Содержание файла:");
        System.out.println(Files.readString(file.toPath()));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        EpicTask loadedEpic = loadedManager.getAllEpics().getFirst();
        assertEquals(2, loadedEpic.getSubtaskIds().size(), "Эпик должен содержать две подзадачи");
        List<Subtask> loadedSubtasks = loadedManager.getSubtasksByEpicId(loadedEpic.getId());
        assertEquals(2, loadedSubtasks.size(), "Должно быть загружено две подзадачи");
        assertEquals(subtask1.getTitle(), loadedSubtasks.get(0).getTitle(), "Названия подзадач должны совпадать");
        assertEquals(subtask2.getTitle(), loadedSubtasks.get(1).getTitle(), "Названия подзадач должны совпадать");
    }
}