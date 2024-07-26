package taskmanagement.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import taskmanagement.manager.TaskManager;
import taskmanagement.task.BaseTask;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        List<BaseTask> prioritizedList = taskManager.getPrioritizedTasks();
        if (prioritizedList.isEmpty()) {
            notFoundResponse(exchange, "Приоритетные задачи не найдены");
        } else {
            okResponse(exchange, gson.toJson(prioritizedList));
        }
    }
}
