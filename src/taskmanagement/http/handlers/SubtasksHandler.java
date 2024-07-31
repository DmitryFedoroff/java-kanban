package taskmanagement.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanagement.manager.TaskManager;
import taskmanagement.task.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetSubtasks(exchange);
                break;
            case "POST":
                handlePostSubtasks(exchange);
                break;
            case "DELETE":
                handleDeleteSubtasks(exchange);
                break;
            default:
                badRequestResponse(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasksList = taskManager.getAllSubtasks();
        if (subtasksList.isEmpty()) {
            notFoundResponse(exchange, "Подзадачи не найдены");
        } else {
            okResponse(exchange, gson.toJson(subtasksList));
        }
    }

    private void handlePostSubtasks(HttpExchange exchange) throws IOException {
        Subtask subtask = getSubtaskFromRequestBody(exchange);
        try {
            if (subtask.getId() != null) {
                taskManager.updateSubtask(subtask);
            } else {
                taskManager.addSubtask(subtask);
            }
            createdResponse(exchange);
        } catch (IllegalArgumentException e) {
            notAcceptableResponse(exchange);
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubtasks();
        okResponse(exchange, "Все подзадачи удалены");
    }

    private Subtask getSubtaskFromRequestBody(HttpExchange exchange) throws IOException {
        return gson.fromJson(getRequestBody(exchange), Subtask.class);
    }
}
