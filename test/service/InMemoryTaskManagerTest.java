package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static service.Managers.getDefault;

class InMemoryTaskManagerTest {

    @Test
    void mustAddTasksAndWorkWithThem() {
        TaskManager taskManager = getDefault();
        Task firstTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task secondTask = new Task("Записать расходы", "Взять выписку из банка", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, 3, LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, 3, LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));

        Epic secondEpic = new Epic("Купить штаны", "Разбить покупку штанов на маленькие задачи", Status.NEW);
        SubTask firstSubTaskForSecondEpic = new SubTask("Пойти в магазин", "Сесть на автобус, " +
                "доехать до магазина", Status.NEW, 6, LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(10));

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

        assertEquals(secondTask, taskManager.getTaskByld(1), "менеджер переделает id");
    }

    @Test
    void checkEpicAndSubtaskForChengedId() {
        TaskManager taskManager = getDefault();
        Epic firstEpic = new Epic("Уборка", "Нужно выполнить подзачачи", Status.NEW);
        SubTask subTask1 = new SubTask("Мыть окна", "взять тряку", Status.NEW, taskManager.createEpic(firstEpic), LocalDateTime.now(), Duration.ofMinutes(10));
        SubTask subTask2 = new SubTask("Мыть пол", "взять тряку", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));
        SubTask subTask3 = new SubTask("Мыть посуду", "взять тряку", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(35), Duration.ofMinutes(10));

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
        SubTask subTask1 = new SubTask("Мыть окна", "взять тряку", Status.NEW, taskManager.createEpic(firstEpic), LocalDateTime.now(), Duration.ofMinutes(10));
        SubTask subTask2 = new SubTask("Мыть пол", "взять тряку", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));
        SubTask subTask3 = new SubTask("Мыть посуду", "взять тряку", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(35), Duration.ofMinutes(10));

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

    @Test
    public void checkTasksCrossing() {
        TaskManager taskManager = getDefault();
        Task firstTask = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW,
                LocalDateTime.of(2024, 01, 03, 15, 0, 0), Duration.ofDays(1));
        Task secondTask = new Task("Записать расходы", "Взять выписку из банка", Status.IN_PROGRESS,
                LocalDateTime.of(2024, 01, 03, 15, 0, 0), Duration.ofMinutes(120));

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        assertEquals(firstTask.getId(), 1, "Задаче не присвоился id ");
        assertNull(secondTask.getId(), "Задаче присвоился id, хотя есть перемечение ");

        secondTask.setStartTime(LocalDateTime.of(2024, 01, 01, 15, 0, 0));
        taskManager.createTask(secondTask);

        assertEquals(secondTask.getId(), 2, "Задаче не присвоился id");

        Task task3 = new Task("Уборка", "Нужно убрать всю квартиру", Status.NEW,
                LocalDateTime.of(2024, 02, 03, 15, 0, 0), Duration.ofDays(2));
        Task task4 = new Task("Записать расходы", "Взять выписку из банка", Status.IN_PROGRESS,
                LocalDateTime.of(2024, 02, 03, 14, 0, 0), Duration.ofMinutes(120));

        taskManager.createTask(task3);
        taskManager.createTask(task4);

        assertEquals(task3.getId(), 3, "Задаче не присвоился id ");
        assertNull(task4.getId(), "Задаче присвоился id, хотя есть перемечение ");
    }

    @Test
    void checkPrioritiTasks() {
        TaskManager taskManager = getDefault();

        Task task1 = new Task("Мыть окна", "взять тряку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Мыть пол", "взять тряку", Status.NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));
        Task task3 = new Task("Мыть посуду", "взять тряку", Status.NEW, LocalDateTime.now().plusMinutes(35), Duration.ofMinutes(10));
        Task task4 = new Task("Сохранить грязную посуду", "взять тряку", Status.NEW, LocalDateTime.of(2024, 03, 2, 15, 0, 0), Duration.ofMinutes(10));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        List<Task> tempList = new ArrayList<>();
        tempList.add(task4);
        tempList.add(task1);
        tempList.add(task2);
        tempList.add(task3);

        List<Task> listFromTreeMap = taskManager.getPrioritizedTasks()
                .values()
                .stream()
                .toList();
        assertEquals(tempList, listFromTreeMap, "Не совпадает последовательность по времени");
    }
}