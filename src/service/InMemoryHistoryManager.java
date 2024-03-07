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
        } else if (tail == null) {
            tail = newNode;
            tail.prev = head;
            head.next = tail;
        } else {
            Node oldTail = tail;
            tail = newNode;
            oldTail.next = tail;
            tail.prev = oldTail;
        }
        return newNode;
    }

    private void removeNode(Node removNode) {
        Node tempNext;
        Node tempPrev;

        if (removNode.prev != null && removNode.next != null) {
            tempNext = removNode.next;
            tempPrev = removNode.prev;

            tempNext.prev = tempPrev;
            tempPrev.next = tempNext;
        } else if (removNode.next == null) {
            tempPrev = tail.prev;
            tempPrev.next = null;
            tail = tempPrev;
            if (tail.prev == null) {
                tail = null;
            }
        } else {
            head = head.next;
            head.prev = null;
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
