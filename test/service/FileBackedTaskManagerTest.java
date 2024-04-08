package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefaultFile();
    }

    @Test
    public void checkSaveAndLoadNorm() {
        File file = taskManager.getFile();
        Task task = new Task("Уборка", "Убрать квартиру", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        int newId = taskManager.createTask(task);
        taskManager.getTaskByld(newId);

        Epic epic = new Epic("Готовка", "Выполинть дела", Status.NEW);
        int newIdEpic = taskManager.createEpic(epic);
        taskManager.getEpicByld(newIdEpic);

        SubTask subtaskFirst = new SubTask("Покупка продуктов", "идти в магазин", Status.NEW, epic.getId(), LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));
        int idSubtaskFirst = taskManager.createSubTask(subtaskFirst);
        taskManager.getSubTaskByld(idSubtaskFirst);
        SubTask subtaskSecond = new SubTask("Готовка", "Идти на кунхню", Status.NEW, epic.getId(), LocalDateTime.now().plusMinutes(35), Duration.ofMinutes(10));
        int idSubtaskSecond = taskManager.createSubTask(subtaskSecond);

        List<Task> listHistory = taskManager.getHistory();

        taskManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> listLoadTask = taskManager.getAllTasks();
        List<Epic> listLoadEpic = taskManager.getAllEpics();
        List<SubTask> listLoadSubtask = taskManager.getAllSubTasks();
        List<Task> listLoadHistory = taskManager.getHistory();
        List<Task> tempPrioritiList = new ArrayList<>();
        tempPrioritiList.add(task);
        tempPrioritiList.add(subtaskFirst);
        tempPrioritiList.add(subtaskSecond);

        assertEquals(tempPrioritiList, taskManager.getPrioritizedTasks(), "Листы приоритетных задач не совпадают");

        assertEquals(subtaskFirst.getName(), listLoadSubtask.get(0).getName(), "Поля name у задачи не совпадают.");
        assertEquals(subtaskFirst.getId(), listLoadSubtask.get(0).getId(), "Поля id у задачи не совпадают.");
        assertEquals(subtaskFirst.getEpicId(), listLoadSubtask.get(0).getEpicId(), "Поля epicId у задачи не совпадают.");
        assertEquals(subtaskFirst.getStatus(), listLoadSubtask.get(0).getStatus(), "Поля staus у задачи не совпадают.");
        assertEquals(subtaskFirst.getDescription(), listLoadSubtask.get(0).getDescription(), "Поля description у задачи не совпадают.");
        assertEquals(subtaskFirst.getTaskType(), listLoadSubtask.get(0).getTaskType(), "Поля taskType у задачи не совпадают.");
        assertEquals(subtaskFirst.getStartTime(), listLoadSubtask.get(0).getStartTime(), "Поля startTime у задачи не совпадают.");
        assertEquals(subtaskFirst.getDuration(), listLoadSubtask.get(0).getDuration(), "Поля duration у задачи не совпадают.");

        assertEquals(task.getName(), listLoadTask.get(0).getName(), "Поля name у задачи не совпадают.");
        assertEquals(task.getId(), listLoadTask.get(0).getId(), "Поля id у задачи не совпадают.");
        assertEquals(task.getEpicId(), listLoadTask.get(0).getEpicId(), "Поля epicId у задачи не совпадают.");
        assertEquals(task.getStatus(), listLoadTask.get(0).getStatus(), "Поля staus у задачи не совпадают.");
        assertEquals(task.getDescription(), listLoadTask.get(0).getDescription(), "Поля description у задачи не совпадают.");
        assertEquals(task.getTaskType(), listLoadTask.get(0).getTaskType(), "Поля taskType у задачи не совпадают.");
        assertEquals(task.getStartTime(), listLoadTask.get(0).getStartTime(), "Поля startTime у задачи не совпадают.");
        assertEquals(task.getDuration(), listLoadTask.get(0).getDuration(), "Поля duration у задачи не совпадают.");

        assertEquals(epic.getName(), listLoadEpic.get(0).getName(), "Поля name у задачи не совпадают.");
        assertEquals(epic.getId(), listLoadEpic.get(0).getId(), "Поля id у задачи не совпадают.");
        assertEquals(epic.getEpicId(), listLoadEpic.get(0).getEpicId(), "Поля epicId у задачи не совпадают.");
        assertEquals(epic.getStatus(), listLoadEpic.get(0).getStatus(), "Поля staus у задачи не совпадают.");
        assertEquals(epic.getDescription(), listLoadEpic.get(0).getDescription(), "Поля description у задачи не совпадают.");
        assertEquals(epic.getTaskType(), listLoadEpic.get(0).getTaskType(), "Поля taskType у задачи не совпадают.");
        assertEquals(epic.getSubTaskIds(), listLoadEpic.get(0).getSubTaskIds(), "Поля subTaskIds у задачи не совпадают.");

        assertEquals(listHistory.get(0), listLoadHistory.get(0), "Истории не совпадают.");
        assertEquals(listHistory.get(1), listLoadHistory.get(1), "Истории не совпадают.");
        assertEquals(listHistory.size(), listLoadHistory.size(), "Количество историй не совпадает.");
        Epic checkEpic = listLoadEpic.get(0);
        assertNotNull(checkEpic, "Задача не существует.");
        List<Integer> epicSubtaskId = checkEpic.getSubTaskIds();

        assertEquals(epicSubtaskId.size(), 2, "Подзадачи не совпадают.");
        assertEquals(epicSubtaskId.get(0), idSubtaskFirst, "Задачи не совпадают.");
        assertEquals(epicSubtaskId.get(1), idSubtaskSecond, "Задачи не совпадают.");

    }

    @Test
    public void checkLoadVoidFail() {
        taskManager = FileBackedTaskManager.loadFromFile(new File("./resources/voidFile.csv"));

        List<Task> listLoadTask = taskManager.getAllTasks();
        List<Epic> listLoadEpic = taskManager.getAllEpics();
        List<SubTask> listLoadSubtask = taskManager.getAllSubTasks();
        List<Task> listLoadHistory = taskManager.getHistory();
        assertEquals(0, listLoadTask.size(), "Количество задач не совпадает.");
        assertEquals(0, listLoadEpic.size(), "Количество эпиков не совпадает.");
        assertEquals(0, listLoadSubtask.size(), "Количество подзадач не совпадает.");
        assertEquals(0, listLoadHistory.size(), "Количество историй не совпадает.");
    }
}