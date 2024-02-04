package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void shouldEqualsWithCopy() {
        SubTask subTask1 = new SubTask("name", "temp", Status.NEW, 1);
        SubTask subTask2 = new SubTask("nameSubTask", "tempEpic", Status.NEW, 1);
        subTask1.setId(1);
        subTask2.setId(1);
        assertEquals(subTask1, subTask2, "Должны совпадать");
    }
}