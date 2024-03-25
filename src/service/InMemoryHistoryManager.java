package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public List<Task> getHistoryList() {
        List<Task> tasksList = new ArrayList<>();
        Node node = head;

        while (node != null) {
            tasksList.add(node.task);
            node = node.next;
        }
        return tasksList;
    }

    @Override
    public void addTaskHistory(Task task) {
        if (task != null) {
            int id = task.getId();
            Node tempNode = history.get(id);

            if (tempNode != null) {
                removeTaskFromHistory(id);
            }
            history.put(id, linkLast(task));
        }
    }

    @Override
    public void removeTaskFromHistory(int id) {
        Node node = history.get(id);
        if (node != null) {
            removeNode(node);
            history.remove(id);
        }
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(null, task, null);

        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;

        return newNode;
    }

    private void removeNode(Node removNode) {
        if (removNode.prev != null && removNode.next != null) {
            removNode.next.prev = removNode.prev;
            removNode.prev.next = removNode.next;
        } else if (removNode.next == null && removNode.prev != null) {
            removNode.prev.next = null;
            tail = removNode.prev;
        } else if (removNode.next != null) {
            removNode.next.prev = null;
            head = removNode.next;
        } else {
            head = null;
            tail = null;
        }
    }

    private static class Node {
        public Task task;
        public Node next;
        public Node prev;

        Node(Node prev, Task task, Node next) {
            this.next = next;
            this.prev = prev;
            this.task = task;
        }
    }
}
