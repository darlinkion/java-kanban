package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.Managers.getDefault;

class HistoryManagerTest {
    @Test
    void getHistoryListTest() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task firstTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        Task secondTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        Task threeTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        firstTask.setId(1);
        secondTask.setId(2);
        threeTask.setId(3);
        inMemoryHistoryManager.addTaskHistory(firstTask);
        inMemoryHistoryManager.addTaskHistory(secondTask);
        inMemoryHistoryManager.addTaskHistory(threeTask);

        List<Task> tempList = new ArrayList<>();
        tempList.add(firstTask);
        tempList.add(secondTask);
        tempList.add(threeTask);

        assertEquals(tempList, inMemoryHistoryManager.getHistoryList(), "Должны совпадать");
    }

    @Test
    void isTheTaskHistoryCorrect() {
        InMemoryTaskManager taskManager = getDefault();
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

        List<Task> historyList = taskManager.getHistory();

        assertEquals(tempList, historyList, "Должны совпадать");
    }

    @Test
    void removeHistoryStart() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task firstTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        Task secondTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        Task threeTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        firstTask.setId(1);
        secondTask.setId(2);
        threeTask.setId(3);
        inMemoryHistoryManager.addTaskHistory(firstTask);
        inMemoryHistoryManager.addTaskHistory(secondTask);
        inMemoryHistoryManager.addTaskHistory(threeTask);

        List<Task> tempList = new ArrayList<>();
        tempList.add(secondTask);
        tempList.add(threeTask);

        inMemoryHistoryManager.removeTaskFromHistory(1);
        assertEquals(tempList, inMemoryHistoryManager.getHistoryList(), "Должны совпадать");
    }

    @Test
    void removeHistoryLast() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task firstTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        Task secondTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        Task threeTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW);
        firstTask.setId(1);
        secondTask.setId(2);
        threeTask.setId(3);
        inMemoryHistoryManager.addTaskHistory(firstTask);
        inMemoryHistoryManager.addTaskHistory(secondTask);
        inMemoryHistoryManager.addTaskHistory(threeTask);

        List<Task> tempList = new ArrayList<>();
        tempList.add(firstTask);
        tempList.add(secondTask);

        inMemoryHistoryManager.removeTaskFromHistory(3);
        assertEquals(tempList, inMemoryHistoryManager.getHistoryList(), "Должны совпадать");
    }

}