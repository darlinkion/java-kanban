package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.Managers.getDefault;

class InMemoryTaskManagerTest {

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

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, 3);
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, 3);

        assertEquals(secondTask, taskManager.getTaskByld(2), "менеджер переделает id");
    }

    @Test
    void checkEpicAndSubtaskForChengedId() {
        TaskManager taskManager = getDefault();
        Epic firstEpic = new Epic("Уборка", "Нужно выполнить подзачачи", Status.NEW);
        SubTask subTask1 = new SubTask("Мыть окна", "взять тряку", Status.NEW, taskManager.createEpic(firstEpic));
        SubTask subTask2 = new SubTask("Мыть пол", "взять тряку", Status.NEW, firstEpic.getId());
        SubTask subTask3 = new SubTask("Мыть посуду", "взять тряку", Status.NEW, firstEpic.getId());

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        //Проверка id
        assertEquals(1, firstEpic.getId());
        assertEquals(2, subTask1.getId());
        assertEquals(3, subTask2.getId());
        assertEquals(4, subTask3.getId());

        //Проверяем id epic у subtask
        assertEquals(firstEpic.getId(), subTask1.getEpicId());
        assertEquals(firstEpic.getId(), subTask2.getEpicId());
        assertEquals(firstEpic.getId(), subTask3.getEpicId());

        // проверка id subtask у epic
        ArrayList<Integer> idSubtasks = new ArrayList<>();
        idSubtasks.add(subTask1.getId());
        idSubtasks.add(subTask2.getId());
        idSubtasks.add(subTask3.getId());
        assertEquals(firstEpic.getSubTaskIds(), idSubtasks);

        //попытка изменить id

        firstEpic.setId(19);
        subTask1.setId(45);
        subTask2.setId(414);
        subTask3.setId(5);

        //Проверка id
        assertEquals(1, firstEpic.getId());
        assertEquals(2, subTask1.getId());
        assertEquals(3, subTask2.getId());
        assertEquals(4, subTask3.getId());
    }

    @Test
    void checkEpicAfterRemoveSubtask() {
        TaskManager taskManager = getDefault();
        Epic firstEpic = new Epic("Уборка", "Нужно выполнить подзачачи", Status.NEW);
        SubTask subTask1 = new SubTask("Мыть окна", "взять тряку", Status.NEW, taskManager.createEpic(firstEpic));
        SubTask subTask2 = new SubTask("Мыть пол", "взять тряку", Status.NEW, firstEpic.getId());
        SubTask subTask3 = new SubTask("Мыть посуду", "взять тряку", Status.NEW, firstEpic.getId());

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        // проверка id subtask у epic
        ArrayList<Integer> idSubtasks = new ArrayList<>();
        idSubtasks.add(subTask1.getId());
        idSubtasks.add(subTask2.getId());
        idSubtasks.add(subTask3.getId());
        assertEquals(firstEpic.getSubTaskIds(), idSubtasks);

        taskManager.removeForIdSubTask(2);
        taskManager.removeForIdSubTask(4);
        idSubtasks.remove(0);
        idSubtasks.remove(1);
        assertEquals(firstEpic.getSubTaskIds(), idSubtasks, "Epic хранит удаленный subtask");
    }
}