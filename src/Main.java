import taskmanagement.task.BaseTask;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.Subtask;
import taskmanagement.manager.TaskManager;
import taskmanagement.manager.HistoryManager;
import taskmanagement.manager.Managers;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        // Создаем две задачи
        SimpleTask task1 = new SimpleTask("Задача 1", "Описание задачи 1");
        SimpleTask task2 = new SimpleTask("Задача 2", "Описание задачи 2");
        manager.addTask(task1);
        manager.addTask(task2);

        // Создаем эпик с тремя подзадачами
        EpicTask epic1 = new EpicTask("Эпик 1", "Описание эпика 1");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        // Создаем эпик без подзадач
        EpicTask epic2 = new EpicTask("Эпик 2", "Описание эпика 2");
        manager.addEpic(epic2);

        // Запрос задач и вывод истории
        BaseTask t1 = manager.getTaskById(task1.getId());
        historyManager.add(t1);
        printHistory(historyManager);

        BaseTask e1 = manager.getEpicById(epic1.getId());
        historyManager.add(e1);
        printHistory(historyManager);

        BaseTask t2 = manager.getTaskById(task2.getId());
        historyManager.add(t2);
        printHistory(historyManager);

        t1 = manager.getTaskById(task1.getId());
        historyManager.add(t1);
        printHistory(historyManager);

        BaseTask st1 = manager.getSubtaskById(subtask1.getId());
        historyManager.add(st1);
        printHistory(historyManager);

        BaseTask st2 = manager.getSubtaskById(subtask2.getId());
        historyManager.add(st2);
        printHistory(historyManager);

        BaseTask e2 = manager.getEpicById(epic2.getId());
        historyManager.add(e2);
        printHistory(historyManager);

        // Удаление задачи и проверка истории
        manager.deleteTask(task1.getId());
        printHistory(historyManager);

        // Удаление эпика с подзадачами и проверка истории
        manager.deleteEpic(epic1.getId());
        printHistory(historyManager);

        // Итоговая история
        printHistory(historyManager);
    }

    private static void printHistory(HistoryManager historyManager) {
        System.out.println("\nИстория просмотров:");
        List<BaseTask> history = historyManager.getHistory();
        history.forEach(task -> System.out.println(task));
    }
}
