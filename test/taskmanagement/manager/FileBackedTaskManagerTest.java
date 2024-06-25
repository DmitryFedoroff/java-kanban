package taskmanagement.manager;

import org.junit.jupiter.api.Test;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.Subtask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @Test
    void testSaveAndLoadEmptyFile() throws IOException {
        File tempFile = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        manager.save();
        assertTrue(Files.readString(tempFile.toPath()).contains("id,type,name,status,description,epic"), "Файл должен содержать заголовок");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getAllTasks().isEmpty(), "Загруженный менеджер не должен содержать задач");
    }

    @Test
    void testSaveAndLoadTasks() throws IOException {
        File tempFile = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        SimpleTask task1 = new SimpleTask("Задача 1", "Описание задачи 1");
        SimpleTask task2 = new SimpleTask("Задача 2", "Описание задачи 2");
        manager.addTask(task1);
        manager.addTask(task2);

        EpicTask epic = new EpicTask("Эпик", "Описание эпика");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epic.getId());
        manager.addSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getAllTasks().size(), "Должно быть загружено две задачи");
        assertEquals(1, loadedManager.getAllEpics().size(), "Должен быть загружен один эпик");
        assertEquals(1, loadedManager.getAllSubtasks().size(), "Должна быть загружена одна подзадача");

        assertEquals(task1.getTitle(), loadedManager.getAllTasks().getFirst().getTitle(), "Названия задач должны совпадать");
        assertEquals(epic.getTitle(), loadedManager.getAllEpics().getFirst().getTitle(), "Названия эпиков должны совпадать");
        assertEquals(subtask.getTitle(), loadedManager.getAllSubtasks().getFirst().getTitle(), "Названия подзадач должны совпадать");
    }

    @Test
    void testSaveAndLoadWithDirectory() throws IOException {
        File tempDir = Files.createTempDirectory("taskmanager").toFile();
        File tempFile = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        SimpleTask task1 = new SimpleTask("Задача 1", "Описание задачи 1");
        SimpleTask task2 = new SimpleTask("Задача 2", "Описание задачи 2");
        manager.addTask(task1);
        manager.addTask(task2);

        manager.save();
        assertTrue(tempFile.exists(), "Файл должен существовать после сохранения");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(2, loadedManager.getAllTasks().size(), "Должно быть загружено две задачи");

        Files.deleteIfExists(tempFile.toPath());
        Files.deleteIfExists(tempDir.toPath());
    }
}
