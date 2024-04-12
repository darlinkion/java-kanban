package service;

import exception.NotFoundException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void Init() {
        taskManager = Managers.getDefault();
    }


    @Test
    public void checkCreateTask() {
        Task task = new Task("Проверка тасков", "а", Status.NEW);
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskByld(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void checkCreateEpic() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        final int epicId = taskManager.createEpic(epic);

        final Epic savedEpic = taskManager.getEpicByld(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void checkCreateSubtask() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic);
        SubTask subtask = new SubTask("Проверка эпиков", "а", Status.NEW, 1);
        final int subtaskId = taskManager.createSubTask(subtask);

        final SubTask savedSubtask = taskManager.getSubTaskByld(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<SubTask> subtasks = taskManager.getAllSubTasks();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
        List<Integer> idList = epic.getSubTaskIds();
        int id = idList.get(0);

        assertEquals(taskManager.getSubTaskByld(id), subtask, "Подзадачи не совпадают.");
        assertEquals(taskManager.getEpicByld(subtask.getEpicId()), epic, "Эпики не совпадают.");

    }

    @Test
    public void checkUpdateTask() {
        Task firstTask = new Task("Проверка тасков", "а", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        final int taskId = taskManager.createTask(firstTask);
        Task secondTask = new Task("Проверка тасков", "а", Status.NEW, LocalDateTime.now().plusDays(1), Duration.ofDays(10));
        secondTask.setId(taskId);
        taskManager.updateTask(secondTask);

        final Task savedTask = taskManager.getTaskByld(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(secondTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(secondTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void checkUpdateVoidTaskException() {
        Task task = new Task("Проверка тасков", "а", Status.NEW);
        task.setId(2);
        taskManager.updateTask(task);
        assertThrows(NotFoundException.class, () -> {
            taskManager.getTaskByld(task.getId());
        });
    }

    @Test
    public void checkUpdateEpic() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        final int epicId = taskManager.createEpic(epic);
        Epic secondEpic = new Epic("Проверка эпиков", "lol", Status.NEW);
        secondEpic.setId(epicId);
        taskManager.updateEpic(secondEpic);

        final Epic savedEpic = taskManager.getEpicByld(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(secondEpic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(secondEpic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void checkUpdateVoidEpicException() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        epic.setId(84);
        taskManager.updateTask(epic);

        assertThrows(NotFoundException.class, () -> {
            taskManager.getTaskByld(epic.getId());
        });
    }

    @Test
    public void checkUpdateSubtask() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic);
        SubTask subtask = new SubTask("Проверка эпиков", "а", Status.NEW, 1);
        final int subtaskId = taskManager.createSubTask(subtask);
        SubTask seconSubTask = new SubTask("Проверка эпиков", "а", Status.NEW, 1);
        seconSubTask.setId(subtaskId);
        taskManager.updateSubTask(seconSubTask);

        final SubTask savedSubtask = taskManager.getSubTaskByld(subtaskId);
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(seconSubTask, savedSubtask, "Задачи не совпадают.");

        final List<SubTask> subtasks = taskManager.getAllSubTasks();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(seconSubTask, subtasks.get(0), "Задачи не совпадают.");
        List<Integer> idList = epic.getSubTaskIds();
        int id = idList.get(0);

        assertEquals(taskManager.getSubTaskByld(id), seconSubTask, "Подзадачи не совпадают.");
        assertEquals(taskManager.getEpicByld(subtask.getEpicId()), epic, "Эпики не совпадают.");
    }

    @Test
    public void checkUpdateVoidSubtaskException() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic);
        SubTask subTask = new SubTask("Проверка эпиков", "а", Status.NEW, 1);

        assertThrows(NotFoundException.class, () -> {
            taskManager.updateSubTask(subTask);
        });
    }

    @Test
    public void deleteAllTaskAndTryGetTaskByIdException() {
        Task task = new Task("Проверка тасков", "а", Status.NEW);
        int taskId = taskManager.createTask(task);
        Task secondTask = new Task("Проверка тасков", "а", Status.NEW);
        int task1 = taskManager.createTask(secondTask);
        taskManager.cleanTasks();

        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(0, tasks.size(), "Неверное количество задач.");

        assertThrows(NotFoundException.class, () -> {
            taskManager.getTaskByld(taskId);
        });
        assertThrows(NotFoundException.class, () -> {
            taskManager.getTaskByld(task1);
        });
    }

    @Test
    public void deleteAllEpicAndTryGetEpicByIdException() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        final int epicId = taskManager.createEpic(epic);
        Epic epic1 = new Epic("Проверка эпиков", "а", Status.NEW);
        final int epic2 = taskManager.createEpic(epic1);

        taskManager.cleanEpics();
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество задач.");

        assertThrows(NotFoundException.class, () -> {
            taskManager.getEpicByld(epicId);
        });
        assertThrows(NotFoundException.class, () -> {
            taskManager.getEpicByld(epic2);
        });
    }

    @Test
    public void deleteAllSubtaskAndTryGetSubTaskByIdException() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic);
        SubTask subtask = new SubTask("Проверка эпиков", "а", Status.NEW, 1);
        final int subTaskId1 = taskManager.createSubTask(subtask);
        SubTask secondSubTask = new SubTask("Проверка эпиков", "а", Status.NEW, 1);
        final int subTaskId2 = taskManager.createSubTask(secondSubTask);
        taskManager.cleanSubTasks();

        final List<SubTask> subtasks = taskManager.getAllSubTasks();

        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");
        List<Integer> idList = epic.getSubTaskIds();
        assertEquals(0, idList.size(), "Подзадачи у эпика не удалены");

        assertThrows(NotFoundException.class, () -> {
            taskManager.getSubTaskByld(subTaskId1);
        });
        assertThrows(NotFoundException.class, () -> {
            taskManager.getSubTaskByld(subTaskId2);
        });
    }

    @Test
    public void getAllTaskNorm() {

        Task task = new Task("Проверка тасков", "а", Status.NEW,
                LocalDateTime.now().plusDays(1), Duration.ofMinutes(40));
        taskManager.createTask(task);
        Task task2 = new Task("Проверка тасков2", "а", Status.NEW,
                LocalDateTime.now().plusDays(2), Duration.ofDays(1));
        taskManager.createTask(task2);
        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
        assertEquals(task2, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    public void getAllTaskVoid() {
        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void getAllEpicNorm() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic);
        Epic epic150 = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic150);
        final List<Epic> epics = taskManager.getAllEpics();

        assertEquals(2, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
        assertEquals(epic150, epics.get(1), "Задачи не совпадают.");
    }

    @Test
    public void getAllEpicVoid() {
        final List<Epic> epics = taskManager.getAllEpics();
        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    public void getAllSubtaskNorm() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic);
        SubTask subtask = new SubTask("Проверка эпиков", "а", Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(15));
        taskManager.createSubTask(subtask);
        SubTask subtask2 = new SubTask("Проверка эпиков", "а", Status.NEW, 1, LocalDateTime.now().plusDays(7), Duration.ofDays(1));
        taskManager.createSubTask(subtask2);
        final List<SubTask> subtasks = taskManager.getAllSubTasks();

        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");

        List<Integer> idList = epic.getSubTaskIds();
        assertEquals(2, idList.size(), "Подзадачи у эпика не определены");
    }

    @Test
    public void getAllSubtaskVoid() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic);
        final List<SubTask> subtasks = taskManager.getAllSubTasks();
        assertEquals(0, subtasks.size(), "Неверное количество задач.");

        List<Integer> idList = epic.getSubTaskIds();
        assertEquals(0, idList.size(), "Подзадачи у эпика определены");
    }

    @Test
    public void deleteForIdTaskNormAndException() {
        Task task = new Task("Проверка тасков", "а", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        int taskId = taskManager.createTask(task);
        Task task1 = new Task("Проверка тасков", "а", Status.NEW, LocalDateTime.now().plusDays(2), Duration.ofMinutes(40));
        int taskId1 = taskManager.createTask(task1);

        taskManager.removeForIdTask(taskId);

        assertThrows(NotFoundException.class, () -> {
            taskManager.getTaskByld(taskId);
        });
        assertNotNull(taskManager.getTaskByld(taskId1), "Удалена не та задача.");

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(1, tasks.size(), "Неверное количество задач.");
    }

    @Test
    public void deleteForIdTaskNotNormException() {
        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        int taskId = 100;
        taskManager.removeForIdTask(taskId);

        assertThrows(NotFoundException.class, () -> {
            taskManager.getTaskByld(taskId);
        });
        List<Task> tasks2 = taskManager.getAllTasks();
        assertEquals(0, tasks2.size(), "Неверное количество задач.");
    }

    @Test
    public void deleteForIdEpicNorm() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        int epicId = taskManager.createEpic(epic);
        Epic epic1 = new Epic("Проверка эпиков", "а", Status.NEW);
        int epicId1 = taskManager.createEpic(epic1);

        taskManager.removeForIdEpic(epicId);

        assertThrows(NotFoundException.class, () -> {
            taskManager.getEpicByld(epicId);
        });
        assertNotNull(taskManager.getEpicByld(epicId1), "Удалена не та задача.");

        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(1, epics.size(), "Неверное количество задач.");
    }

    @Test
    public void deleteForIdSubtaskNormException() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic);
        SubTask subtask = new SubTask("Проверка эпиков", "а", Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(10));
        int subtaskId = taskManager.createSubTask(subtask);
        SubTask subTask2 = new SubTask("Проверка эпиков", "а", Status.NEW, 1, LocalDateTime.now().plusDays(1), Duration.ofDays(1));
        int subtaskId2 = taskManager.createSubTask(subTask2);
        List<Integer> idList = epic.getSubTaskIds();
        assertEquals(2, idList.size(), "Подзадачи у эпика не добавлены");
        taskManager.removeForIdSubTask(subtaskId);

        assertThrows(NotFoundException.class, () -> {
            taskManager.getSubTaskByld(subtaskId);
        });
        assertNotNull(taskManager.getSubTaskByld(subtaskId2), "Удалена не та Подзадача.");

        List<SubTask> subtasks = taskManager.getAllSubTasks();
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        List<Integer> idList2 = epic.getSubTaskIds();
        assertEquals(1, idList2.size(), "Подзадачи у эпика не удалены");
    }

    @Test
    public void getEpicSubtasksNorm() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic);
        SubTask subtask = new SubTask("Проверка эпиков", "а", Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(10));
        final int subtaskId = taskManager.createSubTask(subtask);
        SubTask subtask2 = new SubTask("Проверка эпиков", "а", Status.NEW, 1, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        final int subtaskId2 = taskManager.createSubTask(subtask2);
        List<Integer> idList = epic.getSubTaskIds();
        assertEquals(2, idList.size(), "Подзадачи у эпика не удалены");

        assertEquals(subtaskId, idList.get(0), "Задачи не совпадают.");
        assertEquals(subtaskId2, idList.get(1), "Задачи не совпадают.");
    }

    @Test
    public void getTaskNorm() {
        Task task = new Task("Проверка тасков", "а", Status.NEW, LocalDateTime.now(), Duration.ofDays(1));
        int taskId = taskManager.createTask(task);
        Task task1 = new Task("Проверка тасков", "а", Status.NEW, LocalDateTime.now().plusDays(3), Duration.ofMinutes(13));
        int taskId1 = taskManager.createTask(task1);

        List<Task> tasks = taskManager.getAllTasks();
        Task chekTask = taskManager.getTaskByld(taskId);
        assertEquals(chekTask, tasks.get(0), "Задачи не совпадают.");
        assertEquals(chekTask, task, "Задачи не совпадают.");

        Task chekTask1 = taskManager.getTaskByld(taskId1);
        assertEquals(chekTask1, tasks.get(1), "Задачи не совпадают.");
        assertEquals(chekTask1, task1, "Задачи не совпадают.");
    }

    @Test
    public void getTaskVoidException() {
        int taskId = 100;
        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertThrows(NotFoundException.class, () -> {
            taskManager.getTaskByld(taskId);
        });
    }

    @Test
    public void getEpicNorm() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        final int epicId = taskManager.createEpic(epic);
        Epic epic1 = new Epic("Проверка эпиков", "а", Status.NEW);
        final int epicId1 = taskManager.createEpic(epic1);
        Epic chekEpic = taskManager.getEpicByld(epicId);
        final List<Epic> epics = taskManager.getAllEpics();

        assertEquals(chekEpic, epics.get(0), "Задачи не совпадают.");
        assertEquals(chekEpic, epic, "Задачи не совпадают.");
        assertNotNull(chekEpic, "Задача не существует.");

        Epic chekEpic1 = taskManager.getEpicByld(epicId1);

        assertEquals(chekEpic1, epics.get(1), "Задачи не совпадают.");
        assertEquals(chekEpic1, epic1, "Задачи не совпадают.");
        assertNotNull(chekEpic1, "Задача не существует.");
    }

    @Test
    public void getEpicVoidException() {
        int epicId = 100;
        final List<Epic> epics = taskManager.getAllEpics();

        assertThrows(NotFoundException.class, () -> {
            taskManager.getEpicByld(epicId);
        });
        assertEquals(0, epics.size(), "Неверное количество задач.");
    }

    @Test
    public void getSubtaskNorm() {
        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        taskManager.createEpic(epic);
        SubTask subtask = new SubTask("Проверка эпиков", "а", Status.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(20));
        int subtaskId = taskManager.createSubTask(subtask);
        SubTask subtask1 = new SubTask("Проверка эпиков", "а", Status.NEW, 1, LocalDateTime.now().plusDays(2), Duration.ofMinutes(30));
        int subtaskId1 = taskManager.createSubTask(subtask1);
        SubTask chekSubtask = taskManager.getSubTaskByld(subtaskId);

        List<SubTask> subtasks = taskManager.getAllSubTasks();
        assertEquals(chekSubtask, subtasks.get(0), "Задачи не совпадают.");
        assertEquals(chekSubtask, subtask, "Задачи не совпадают.");
        assertNotNull(chekSubtask, "Задача не существует.");

        SubTask chekSubtask1 = taskManager.getSubTaskByld(subtaskId1);
        assertEquals(chekSubtask1, subtasks.get(1), "Задачи не совпадают.");
        assertEquals(chekSubtask1, subtask1, "Задачи не совпадают.");
        assertNotNull(chekSubtask1, "Задача не существует.");

    }

    @Test
    public void getSubtaskVoidException() {
        int subtaskId = 100;
        final List<SubTask> subtasks = taskManager.getAllSubTasks();

        assertThrows(NotFoundException.class, () -> {
            taskManager.getSubTaskByld(subtaskId);
        });
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
    }

    @Test
    public void getHistoryNorm() {
        Task task = new Task("Проверка тасков", "а", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        final int taskId = taskManager.createTask(task);
        Task task1 = new Task("Проверка тасков", "а", Status.NEW, LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        Task chekTask = taskManager.getTaskByld(taskId);

        Epic epic = new Epic("Проверка эпиков", "а", Status.NEW);
        int epicId = taskManager.createEpic(epic);
        Epic chekEpic = taskManager.getEpicByld(epicId);

        SubTask subtask = new SubTask("Проверка эпиков", "а", Status.NEW, 3, LocalDateTime.now().plusDays(1), Duration.ofMinutes(10));
        int subtaskId = taskManager.createSubTask(subtask);
        SubTask subtask1 = new SubTask("Проверка эпиков", "а", Status.NEW, 3, LocalDateTime.now().plusDays(2), Duration.ofMinutes(180));
        taskManager.createSubTask(subtask1);
        SubTask chekSubtask = taskManager.getSubTaskByld(subtaskId);

        List<Task> historyList = taskManager.getHistory();

        assertEquals(chekSubtask, historyList.get(2), "Задачи не совпадают.");
        assertEquals(chekTask, historyList.get(0), "Задачи не совпадают.");
        assertEquals(chekEpic, historyList.get(1), "Задачи не совпадают.");
        assertNotNull(historyList, "История задач пустая.");
    }

    @Test
    public void getHistoryVoidException() {
        int subtaskId = 100;
        assertThrows(NotFoundException.class, () -> {
            taskManager.getSubTaskByld(subtaskId);
        });
        List<Task> historyList = taskManager.getHistory();
        assertEquals(historyList.size(), 0, "История задач не пустая.");
    }

}