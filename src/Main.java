import taskmanagement.task.BaseTask;
import taskmanagement.task.SimpleTask;
import taskmanagement.task.EpicTask;
import taskmanagement.task.Subtask;
import taskmanagement.status.TaskStatus;
import taskmanagement.manager.TaskManager;
import taskmanagement.manager.InMemoryTaskManager;

import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();

        // Создаем задачи и эпики
        SimpleTask task1 = new SimpleTask("Переезд", "Переехать в новый офис");
        SimpleTask task2 = new SimpleTask("Подготовка отчета", "Подготовить ежемесячный отчет");
        manager.addTask(task1);
        manager.addTask(task2);

        EpicTask epic1 = new EpicTask("Разработка нового продукта", "Разработка и запуск нового продукта на рынок");
        manager.addEpic(epic1);
        Subtask subtask1 = new Subtask("Исследование рынка", "Провести исследование рынка", epic1.getId());
        Subtask subtask2 = new Subtask("Прототипирование", "Создать прототип продукта", epic1.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        EpicTask epic2 = new EpicTask("Организация мероприятия", "Подготовка и проведение корпоративного мероприятия");
        manager.addEpic(epic2);
        Subtask subtask3 = new Subtask("Бронирование места", "Забронировать место для мероприятия", epic2.getId());
        manager.addSubtask(subtask3);

        // Выводим все задачи
        printAllTasks(manager);

        // Изменяем статусы и печатаем результаты
        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask2);
        subtask3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);

        // Выводим задачи после изменений
        printAllTasks(manager);

        // Удаление задачи и эпика
        manager.deleteTask(task2.getId());
        manager.deleteEpic(epic2.getId());

        // Выводим задачи после удаления
        printAllTasks(manager);

        // Просмотр задач для создания истории
        manager.getTaskById(task1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getEpicById(epic2.getId());
        manager.getTaskById(task2.getId());
        manager.getSubtaskById(subtask3.getId());
        manager.getEpicById(epic1.getId());

        // Выводим историю просмотров
        printHistory(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nВсе задачи:");
        manager.getAllTasks().forEach(System.out::println);
        System.out.println("\nВсе эпики:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println("\nВсе подзадачи:");
        manager.getAllSubtasks().forEach(System.out::println);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("\nИстория просмотров:");
        List<BaseTask> history = manager.getHistory();
        HashMap<Integer, Integer> viewCounts = ((InMemoryTaskManager) manager).getViewCounts();

        for (BaseTask task : history) {
            System.out.println(task + " (Просмотров: " + viewCounts.get(task.getId()) + ")");
        }
    }
}
