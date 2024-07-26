package taskmanagement.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanagement.manager.TaskManager;
import taskmanagement.task.BaseTask;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        List<BaseTask> history = taskManager.getPrioritizedTasks();
        if (history.isEmpty()) {
            notFoundResponse(exchange, "История задач пуста");
        } else {
            okResponse(exchange, gson.toJson(history));
        }
    }
}
