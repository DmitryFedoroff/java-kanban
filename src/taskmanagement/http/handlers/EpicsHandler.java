package taskmanagement.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanagement.exceptions.NotFoundException;
import taskmanagement.manager.TaskManager;
import taskmanagement.task.EpicTask;
import taskmanagement.task.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String[] splitStrings = path.split("/");

        if (splitStrings.length == 2) {
            switch (method) {
                case "GET":
                    handleGetEpics(exchange);
                    break;
                case "POST":
                    handlePostEpics(exchange);
                    break;
                case "DELETE":
                    handleDeleteEpics(exchange);
                    break;
                default:
                    badRequestResponse(exchange);
            }
        } else if (splitStrings.length == 3) {
            Optional<Integer> epicId = getIdFromPath(exchange);
            switch (method) {
                case "GET":
                    handleGetEpicById(exchange, epicId.orElse(null));
                    break;
                case "DELETE":
                    handleDeleteEpicById(exchange, epicId.orElse(null));
                    break;
                default:
                    badRequestResponse(exchange);
            }
        } else if (splitStrings.length == 4 && splitStrings[3].equals("subtasks")) {
            Optional<Integer> epicId = getIdFromPath(exchange);
            epicId.ifPresentOrElse(
                    id -> {
                        try {
                            handleGetEpicSubtasks(exchange, id);
                        } catch (IOException e) {
                            try {
                                errorResponse(exchange, e.getMessage());
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    },
                    () -> {
                        try {
                            badRequestResponse(exchange);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } else {
            badRequestResponse(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<EpicTask> epicsList = taskManager.getAllEpics();
        if (epicsList.isEmpty()) {
            notFoundResponse(exchange, "Эпики не найдены");
        } else {
            okResponse(exchange, gson.toJson(epicsList));
        }
    }

    private void handlePostEpics(HttpExchange exchange) throws IOException {
        EpicTask epic = getEpicFromRequestBody(exchange);
        try {
            if (epic.getId() != null) {
                taskManager.updateEpic(epic);
            } else {
                taskManager.addEpic(epic);
            }
            createdResponse(exchange);
        } catch (IllegalArgumentException e) {
            notAcceptableResponse(exchange);
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpics();
        okResponse(exchange, "Все эпики удалены");
    }

    private void handleGetEpicById(HttpExchange exchange, Integer epicId) throws IOException {
        if (epicId != null) {
            try {
                EpicTask epic = taskManager.getEpicById(epicId);
                okResponse(exchange, gson.toJson(epic));
            } catch (NotFoundException e) {
                notFoundResponse(exchange, e.getMessage());
            }
        } else {
            badRequestResponse(exchange);
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange, Integer epicId) throws IOException {
        if (epicId != null) {
            try {
                taskManager.deleteEpic(epicId);
                okResponse(exchange, "Эпик удален");
            } catch (NotFoundException e) {
                notFoundResponse(exchange, e.getMessage());
            }
        } else {
            badRequestResponse(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, int epicId) throws IOException {
        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epicId);
        if (subtasks.isEmpty()) {
            notFoundResponse(exchange, "Подзадачи для эпика не найдены");
        } else {
            okResponse(exchange, gson.toJson(subtasks));
        }
    }

    private EpicTask getEpicFromRequestBody(HttpExchange exchange) throws IOException {
        return gson.fromJson(getRequestBody(exchange), EpicTask.class);
    }
}
