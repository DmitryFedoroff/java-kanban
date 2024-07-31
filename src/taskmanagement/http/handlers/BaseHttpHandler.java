package taskmanagement.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import taskmanagement.exceptions.NotFoundException;
import taskmanagement.manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;
    protected final TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            handleRequest(exchange);
        } catch (NotFoundException e) {
            notFoundResponse(exchange, e.getMessage());
        } catch (Exception e) {
            errorResponse(exchange, e.getMessage());
        }
    }

    protected abstract void handleRequest(HttpExchange exchange) throws IOException;

    protected void okResponse(HttpExchange exchange, String responseString) throws IOException {
        writeResponse(exchange, responseString, 200);
    }

    protected void createdResponse(HttpExchange exchange) throws IOException {
        writeResponse(exchange, "Создано", 201);
    }

    protected void notAcceptableResponse(HttpExchange exchange) throws IOException {
        writeResponse(exchange, "Недопустимо", 406);
    }

    protected void badRequestResponse(HttpExchange exchange) throws IOException {
        writeResponse(exchange, "Неверный запрос", 400);
    }

    protected void notFoundResponse(HttpExchange exchange, String message) throws IOException {
        writeResponse(exchange, message, 404);
    }

    protected void errorResponse(HttpExchange exchange, String message) throws IOException {
        writeResponse(exchange, message, 500);
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        exchange.sendResponseHeaders(responseCode, responseString.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected Optional<Integer> getIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        try {
            return Optional.of(Integer.parseInt(splitStrings[2]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    protected String getRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }
}
