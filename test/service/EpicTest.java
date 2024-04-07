package service;

import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class EpicTest {
    FileBackedTaskManager fileBackedTaskManager;
    Epic epic;

    @BeforeEach
    public void startTestEpic() {
        fileBackedTaskManager = Managers.getDefaultFile();
        epic = new Epic("Проверка", "аа", Status.NEW);
    }


    @Test
    public void calculationStatusEpicNotExistingSubtask() {
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.updateEpic(epic);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void calculationStatusEpicWithSubtaskNew() {
        fileBackedTaskManager.createEpic(epic);
        SubTask subtask1 = new SubTask("1", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 01, 07, 15, 0, 0), Duration.ofMinutes(120));
        fileBackedTaskManager.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("2", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 01, 10, 18, 0, 0), Duration.ofMinutes(120));
        fileBackedTaskManager.createSubTask(subtask2);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void calculationStatusEpicWithSubtaskDone() {
        fileBackedTaskManager.createEpic(epic);
        SubTask subtask1 = new SubTask("1", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 01, 07, 15, 0, 0), Duration.ofMinutes(120));
        fileBackedTaskManager.createSubTask(subtask1);
        subtask1.setStatus(Status.DONE);
        SubTask subtask2 = new SubTask("2", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 01, 10, 18, 0, 0), Duration.ofMinutes(120));
        fileBackedTaskManager.createSubTask(subtask2);
        subtask2.setStatus(Status.DONE);
        fileBackedTaskManager.updateSubTask(subtask2);
        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void calculationStatusEpicWithSubtaskNewAndDone() {
        fileBackedTaskManager.createEpic(epic);
        SubTask subtask1 = new SubTask("1", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 01, 07, 15, 0, 0), Duration.ofMinutes(120));
        fileBackedTaskManager.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("2", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 01, 10, 18, 0, 0), Duration.ofMinutes(120));
        fileBackedTaskManager.createSubTask(subtask2);
        subtask2.setStatus(Status.DONE);
        fileBackedTaskManager.updateSubTask(subtask2);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void calculationStatusEpicWithSubtaskNewAndInProcess() {
        fileBackedTaskManager.createEpic(epic);
        SubTask subtask1 = new SubTask("1", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 01, 07, 15, 0, 0), Duration.ofMinutes(120));
        fileBackedTaskManager.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("2", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 01, 10, 18, 0, 0), Duration.ofMinutes(120));
        fileBackedTaskManager.createSubTask(subtask2);
        subtask2.setStatus(Status.IN_PROGRESS);
        fileBackedTaskManager.updateSubTask(subtask2);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void checkEpicTimeNorm() {
        fileBackedTaskManager.createEpic(epic);
        SubTask subtask1 = new SubTask("1", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 4, 15, 14, 40), Duration.ofMinutes(30));
        fileBackedTaskManager.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("2", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 4, 10, 14, 20), Duration.ofDays(1));
        fileBackedTaskManager.createSubTask(subtask2);
        Assertions.assertEquals(subtask2.getStartTime(), epic.getStartTime());
        Assertions.assertEquals(subtask1.getEndTime(), epic.getEndTime());
        Assertions.assertEquals(subtask1.getDuration().plus(subtask2.getDuration()), epic.getDuration());
        Assertions.assertEquals(subtask2.getStartTime(), epic.getStartTime());
    }

    @Test
    public void checkEpicTimeVoid() {
        fileBackedTaskManager.createEpic(epic);
        SubTask subtask1 = new SubTask("1", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 4, 15, 14, 40), Duration.ofMinutes(30));
        fileBackedTaskManager.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("2", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 4, 10, 14, 20), Duration.ofDays(1));
        fileBackedTaskManager.createSubTask(subtask2);
        Assertions.assertEquals(subtask2.getStartTime(), epic.getStartTime());
        Assertions.assertEquals(subtask1.getEndTime(), epic.getEndTime());
        Assertions.assertEquals(subtask1.getDuration().plus(subtask2.getDuration()), epic.getDuration());
        Assertions.assertEquals(subtask2.getStartTime(), epic.getStartTime());
    }

    @Test
    public void checkEpicTimeIdentical() {
        fileBackedTaskManager.createEpic(epic);
        SubTask subtask1 = new SubTask("1", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 4, 15, 14, 40), Duration.ofMinutes(50));
        fileBackedTaskManager.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("2", "а", Status.NEW, epic.getId(),
                LocalDateTime.of(2023, 4, 15, 14, 40), Duration.ofMinutes(55));
        fileBackedTaskManager.createSubTask(subtask2);
        List<Integer> subtaskList = epic.getSubTaskIds();
        Assertions.assertEquals(1, subtaskList.size());
        Assertions.assertEquals(subtask1.getStartTime(), epic.getStartTime());
        Assertions.assertEquals(subtask1.getEndTime(), epic.getEndTime());
        Assertions.assertEquals(subtask1.getDuration(), epic.getDuration());
    }
}