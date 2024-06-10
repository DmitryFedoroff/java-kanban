package taskmanagement.manager;

import taskmanagement.task.BaseTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> nodes = new HashMap<>();
    private final HashMap<Integer, Integer> viewCounts = new HashMap<>();

    private static class Node {
        BaseTask task;
        Node next;
        Node prev;

        Node(Node prev, BaseTask task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    private void linkLast(BaseTask task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        nodes.put(task.getId(), newNode);
    }

    private List<BaseTask> getTasks() {
        List<BaseTask> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) return;

        final Node prev = node.prev;
        final Node next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        nodes.remove(node.task.getId());
    }

    @Override
    public void add(BaseTask task) {
        if (nodes.containsKey(task.getId())) {
            removeNode(nodes.get(task.getId()));
        }
        linkLast(task);
        viewCounts.put(task.getId(), viewCounts.getOrDefault(task.getId(), 0) + 1);
    }

    @Override
    public void remove(int id) {
        if (nodes.containsKey(id)) {
            removeNode(nodes.get(id));
            viewCounts.remove(id);
        }
    }

    @Override
    public List<BaseTask> getHistory() {
        return getTasks();
    }
}
