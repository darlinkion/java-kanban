package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Тестируем обычные таски")
class TaskTest {

    @Test
    void shouldEqualsWithCopy() {
        Task task1 = new Task("name", "temp", Status.DONE);
        Task task2 = new Task("name1", "temp1", Status.DONE);
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Должны совпадать");
    }
}