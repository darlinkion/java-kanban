package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.Managers.getDefault;

class ManagersTest {
    @Test
    void taskManagers() {
        TaskManager inMemoryTaskManager = getDefault();
        TaskManager memoryTaskManager = getDefault();
        assertEquals(inMemoryTaskManager.getAllTasks(), memoryTaskManager.getAllTasks(),
                "Таски менеджеров должны совпадать");
        assertEquals(inMemoryTaskManager.getAllEpics(), memoryTaskManager.getAllEpics(),
                "Епики менеджеров должны совпадать\"");
        assertEquals(inMemoryTaskManager.getAllSubTasks(), memoryTaskManager.getAllSubTasks(),
                "Сабтаски менеджеров должны совпадать\"");
    }
}