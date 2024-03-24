package service;

import java.io.File;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static FileBackedTaskManager getDefaultFile() {
        return new FileBackedTaskManager(getDefaultHistory(), new File("./resources/task.csv"));
    }

    public static FileBackedTaskManager getDefaultFile(File file) {
        return new FileBackedTaskManager(getDefaultHistory(), file);
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}