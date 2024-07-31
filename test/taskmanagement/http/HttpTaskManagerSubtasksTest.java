package taskmanagement.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.manager.InMemoryTaskManager;
import taskmanagement.manager.TaskManager;
import taskmanagement.task.EpicTask;
import taskmanagement.task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerSubtasksTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // создаём эпик
        EpicTask epic = new EpicTask("Test Epic", "Testing epic");
        manager.addEpic(epic);

        // создаём подзадачу
        Subtask subtask = new Subtask("Test Subtask", "Testing subtask", epic.getId());
        subtask.setStartTime(LocalDateTime.now().plusMinutes(10));
        subtask.setDuration(Duration.ofMinutes(60));
        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем REST, отвечающий за создание подзадач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что подзадача была добавлена
        assertEquals(1, manager.getSubtasksByEpicId(epic.getId()).size(), "Некорректное количество подзадач");
    }
}
