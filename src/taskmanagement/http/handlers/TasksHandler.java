package taskmanagement.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanagement.manager.TaskManager;
import taskmanagement.task.BaseTask;
import taskmanagement.task.SimpleTask;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGetTasks(exchange);
                break;
            case "POST":
                handlePostTasks(exchange);
                break;
            case "DELETE":
                handleDeleteTasks(exchange);
                break;
            default:
                badRequestResponse(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<BaseTask> tasksList = taskManager.getAllTasks();
        if (tasksList.isEmpty()) {
            notFoundResponse(exchange, "Задачи не найдены");
        } else {
            okResponse(exchange, gson.toJson(tasksList));
        }
    }

    private void handlePostTasks(HttpExchange exchange) throws IOException {
        SimpleTask task = getTaskFromRequestBody(exchange);
        try {
            if (task.getId() != 0) {
                taskManager.updateTask(task);
            } else {
                taskManager.addTask(task);
            }
            createdResponse(exchange);
        } catch (IllegalArgumentException e) {
            notAcceptableResponse(exchange);
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        okResponse(exchange, "Все задачи удалены");
    }

    private SimpleTask getTaskFromRequestBody(HttpExchange exchange) throws IOException {
        return gson.fromJson(getRequestBody(exchange), SimpleTask.class);
    }
}
