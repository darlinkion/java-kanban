package model;

import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldEqualsWithCopy() {
        Epic epic1 = new Epic("name", "temp", Status.NEW);
        Epic epic2 = new Epic("nameEpic", "tempEpic", Status.NEW);
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2, "Должны совпадать");
    }
}