package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
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
        Task task = new Task("Уборка", "Убрать квартиру", Status.NEW);
        int newId = taskManager.createTask(task);
        taskManager.getTaskByld(newId);

        Epic epic = new Epic("Готовка", "Выполинть дела", Status.NEW);
        int newIdEpic = taskManager.createEpic(epic);
        taskManager.getEpicByld(newIdEpic);

        SubTask subtaskFirst = new SubTask("Покупка продуктов", "идти в магазин", Status.NEW, epic.getId());
        int idSubtaskFirst = taskManager.createSubTask(subtaskFirst);
        taskManager.getSubTaskByld(idSubtaskFirst);
        SubTask subtaskSecond = new SubTask("Готовка", "Идти на кунхню", Status.NEW, epic.getId());
        int idSubtaskSecond = taskManager.createSubTask(subtaskSecond);

        List<Task> listHistory = taskManager.getHistory();

        taskManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> listLoadTask = taskManager.getAllTasks();
        List<Epic> listLoadEpic = taskManager.getAllEpics();
        List<SubTask> listLoadSubtask = taskManager.getAllSubTasks();
        List<Task> listLoadHistory = taskManager.getHistory();

        assertEquals(subtaskFirst, listLoadSubtask.get(0), "Задачи не совпадают.");
        assertEquals(task, listLoadTask.get(0), "Задачи не совпадают.");
        assertEquals(epic, listLoadEpic.get(0), "Задачи не совпадают.");
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