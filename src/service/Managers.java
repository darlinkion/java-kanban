package service;

public class Managers {
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());

    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}