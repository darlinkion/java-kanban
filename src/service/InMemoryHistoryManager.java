package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> tasksHistory;

    public InMemoryHistoryManager() {
        tasksHistory = new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void addTaskHistory(Task task) {
        if (task == null) return;
        if (tasksHistory.size() > 9) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }
}