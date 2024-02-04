package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static service.Managers.getDefault;

class InMemoryTaskManagerTest {

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

    @Test
    void mustAddTasksAndWorkWithThem() {
        TaskManager taskManager = getDefault();
        Task firstTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        Task secondTask = new Task("Записать расходы", "Взять выписку из банка", Status.IN_PROGRESS);

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, 3);
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, 3);

        Epic secondEpic = new Epic("Купить штаны", "Разбить покупку штанов на маленькие задачи", Status.NEW);
        SubTask firstSubTaskForSecondEpic = new SubTask("Пойти в магазин", "Сесть на автобус, " +
                "доехать до магазина", Status.NEW, 6);

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);
        taskManager.createEpic(firstEpic);
        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);
        taskManager.createEpic(secondEpic);
        taskManager.createSubTask(firstSubTaskForSecondEpic);

        assertEquals(firstTask, taskManager.getTaskByld(1), "Такса должна быть под id 1");
        assertEquals(secondTask, taskManager.getTaskByld(2), "Такса должна быть под id 2");
        assertEquals(firstEpic, taskManager.getEpicByld(3), "Епик должнен быть под id 3");
        assertEquals(firstSubTaskForFristEpic, taskManager.getSubTaskByld(4), "Сабтакс должнен быть под id 4");
        assertEquals(secondSubTaskForFristEpic, taskManager.getSubTaskByld(5), "Сабтакс должнен быть под id 5");
        assertEquals(secondEpic, taskManager.getEpicByld(6), "Епик должнен быть под id 6");
        assertEquals(firstSubTaskForSecondEpic, taskManager.getSubTaskByld(7), "Сабтакс должнен быть под id 7");
    }

    @Test
    void conflictBetweenTasks() {
        TaskManager taskManager = getDefault();
        Task firstTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        taskManager.createTask(firstTask);

        Task secondTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        secondTask.setId(1);
        taskManager.createTask(secondTask);

        assertEquals(secondTask, taskManager.getTaskByld(2), "менеджер переделает id");
    }

    @Test
    void isTheTaskHistoryCorrect() {
        InMemoryTaskManager taskManager = getDefault();
        taskManager.cleanTasks();
        taskManager.cleanEpics();
        taskManager.cleanSubTasks();
        List<Task> tempList = new ArrayList<>();
        Task firstTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        Task secondTask = new Task("Записать расходы", "Взять выписку из банка", Status.IN_PROGRESS);

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, 3);
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, 3);

        Epic secondEpic = new Epic("Купить штаны", "Разбить покупку штанов на маленькие задачи", Status.NEW);
        SubTask firstSubTaskForSecondEpic = new SubTask("Пойти в магазин", "Сесть на автобус, " +
                "доехать до магазина", Status.NEW, 6);

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);
        taskManager.createEpic(firstEpic);
        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);
        taskManager.createEpic(secondEpic);
        taskManager.createSubTask(firstSubTaskForSecondEpic);

        tempList.add(taskManager.getTaskByld(1));
        tempList.add(taskManager.getTaskByld(2));
        tempList.add(taskManager.getEpicByld(3));
        tempList.add(taskManager.getSubTaskByld(4));
        tempList.add(taskManager.getSubTaskByld(5));
        tempList.add(taskManager.getEpicByld(6));
        tempList.add(taskManager.getSubTaskByld(7));

        List<Task> historyList = taskManager.getHistoryList();

        assertEquals(tempList, historyList, "Должны совпадать");
    }
}