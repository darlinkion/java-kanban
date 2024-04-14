package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import exception.NotFoundException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static service.Managers.getDefault;

class HttpTaskServerTest {

    HttpTaskServer httpTaskServer;
    InMemoryTaskManager taskManager;
    LocalDateTimeTypeAdapter localDateTimeTypeAdapter;
    DurationTypeAdapter localDurationTypeAdapter;
    Gson gson;
    HttpClient httpClient;
    URI url;

    @AfterEach
    public void stop() {
        httpTaskServer.stopServer();
    }

    @BeforeEach
    public void init() {
        taskManager = getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        localDateTimeTypeAdapter = new LocalDateTimeTypeAdapter();
        localDurationTypeAdapter = new DurationTypeAdapter();
        httpClient = HttpClient.newHttpClient();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, localDateTimeTypeAdapter);
        gsonBuilder.registerTypeAdapter(Duration.class, localDurationTypeAdapter);
        gson = gsonBuilder
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        try {
            httpTaskServer.startServer();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void checkGetAllTasks() {

        String path = "http://localhost:8080/tasks";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

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

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<Task> tasks = gson.fromJson(response.body(), new TasksListTypeToken().getType());
            assertNotNull(tasks);
            assertEquals(tempList.size(), tasks.size());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Test
    void checkGetTaskById() {

        String path = "http://localhost:8080/tasks/1";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Task task1 = new Task("Мыть окна", "взять тряку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            Task tempTask = gson.fromJson(response.body(), Task.class);
            assertEquals(task1, tempTask, "Задачи не совпадают");

        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkPostCreateTask() {

        String path = "http://localhost:8080/tasks";
        url = URI.create(path);
        Task task2 = new Task("Мыть пол", "взять тряку в руки", Status.NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));
        taskManager.createTask(task2);

        Task task1 = new Task(1, "Мыть окна", "взять тряку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        String taskJson = gson.toJson(task1);
        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            Task taskFromManager = taskManager.getTaskByld(1);
            assertEquals(task1.getTaskType(), taskFromManager.getTaskType(), "Задачи не совпадают");
            assertEquals(task1.getName(), taskFromManager.getName(), "Задачи не совпадают");
            assertEquals(task1.getStartTime(), taskFromManager.getStartTime(), "Задачи не совпадают");
            assertEquals(task1.getDescription(), taskFromManager.getDescription(), "Задачи не совпадают");
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }


    @Test
    void checkPostUpdateTask() {

        String path = "http://localhost:8080/tasks";
        url = URI.create(path);

        Task task1 = new Task("Мыть окна", "взять тряку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        String taskJson = gson.toJson(task1);
        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            Task taskFromManager = taskManager.getTaskByld(1);
            assertEquals(task1.getTaskType(), taskFromManager.getTaskType(), "Задачи не совпадают");
            assertEquals(task1.getName(), taskFromManager.getName(), "Задачи не совпадают");
            assertEquals(task1.getStartTime(), taskFromManager.getStartTime(), "Задачи не совпадают");
            assertEquals(task1.getDescription(), taskFromManager.getDescription(), "Задачи не совпадают");
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkDeleteTasks() {

        String path = "http://localhost:8080/tasks";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

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

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            List<Task> tasks = taskManager.getAllTasks();
            assertNotEquals(tempList.size(), tasks.size());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkDeleteTaskById() {

        String path = "http://localhost:8080/tasks/1";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

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
        tempList.add(task2);
        tempList.add(task3);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            List<Task> tasks = taskManager.getAllTasks();
            assertEquals(tempList.size(), tasks.size());
            assertThrows(NotFoundException.class, () -> {
                taskManager.getTaskByld(1);
            });
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkGetAllSubTasks() {

        String path = "http://localhost:8080/subtasks";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));

        Epic secondEpic = new Epic("Купить штаны", "Разбить покупку штанов на маленькие задачи", Status.NEW);
        taskManager.createEpic(secondEpic);
        SubTask firstSubTaskForSecondEpic = new SubTask("Пойти в магазин", "Сесть на автобус, " +
                "доехать до магазина", Status.NEW, secondEpic.getId(), LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(10));

        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);
        taskManager.createSubTask(firstSubTaskForSecondEpic);
        List<SubTask> tempList = new ArrayList<>();
        tempList.add(firstSubTaskForFristEpic);
        tempList.add(secondSubTaskForFristEpic);
        tempList.add(firstSubTaskForSecondEpic);


        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<SubTask> subTaskList = gson.fromJson(response.body(), new TasksListTypeToken().getType());
            assertNotNull(subTaskList);
            assertEquals(tempList.size(), subTaskList.size());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Test
    void checkGetSubTaskById() {

        String path = "http://localhost:8080/subtasks/2";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            SubTask tempTask = gson.fromJson(response.body(), SubTask.class);
            assertEquals(firstSubTaskForFristEpic, tempTask, "Задачи не совпадают");

        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkPostCreateSubTask() {

        String path = "http://localhost:8080/subtasks";
        url = URI.create(path);
        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));

        taskManager.createSubTask(secondSubTaskForFristEpic);

        String taskJson = gson.toJson(firstSubTaskForFristEpic);
        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());

            Task taskFromManager = taskManager.getSubTaskByld(3);
            assertEquals(firstSubTaskForFristEpic.getTaskType(), taskFromManager.getTaskType(), "Задачи не совпадают");
            assertEquals(firstSubTaskForFristEpic.getName(), taskFromManager.getName(), "Задачи не совпадают");
            assertEquals(firstSubTaskForFristEpic.getStartTime(), taskFromManager.getStartTime(), "Задачи не совпадают");
            assertEquals(firstSubTaskForFristEpic.getDescription(), taskFromManager.getDescription(), "Задачи не совпадают");
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }


    @Test
    void checkPostUpdateSubTask() {

        String path = "http://localhost:8080/subtasks";
        url = URI.create(path);

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        taskManager.createSubTask(secondSubTaskForFristEpic);

        firstSubTaskForFristEpic.setId(2);

        String taskJson = gson.toJson(firstSubTaskForFristEpic);
        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            Task taskFromManager = taskManager.getSubTaskByld(2);
            assertEquals(firstSubTaskForFristEpic.getTaskType(), taskFromManager.getTaskType(), "Задачи не совпадают");
            assertEquals(firstSubTaskForFristEpic.getName(), taskFromManager.getName(), "Задачи не совпадают");
            assertEquals(firstSubTaskForFristEpic.getStartTime(), taskFromManager.getStartTime(), "Задачи не совпадают");
            assertEquals(firstSubTaskForFristEpic.getDescription(), taskFromManager.getDescription(), "Задачи не совпадают");
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkDeleteSubTasks() {

        String path = "http://localhost:8080/subtasks";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);
        List<SubTask> tempList = new ArrayList<>();
        tempList.add(firstSubTaskForFristEpic);
        tempList.add(secondSubTaskForFristEpic);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            List<SubTask> subTaskList = taskManager.getAllSubTasks();
            assertNotEquals(tempList.size(), subTaskList.size());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkDeleteSubTaskById() {

        String path = "http://localhost:8080/subtasks/2";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);
        List<SubTask> tempList = new ArrayList<>();
        tempList.add(secondSubTaskForFristEpic);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            List<SubTask> tasks = taskManager.getAllSubTasks();
            assertEquals(tempList.size(), tasks.size());
            assertThrows(NotFoundException.class, () -> {
                taskManager.getTaskByld(2);
            });
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkGetAllEpics() {

        String path = "http://localhost:8080/epics";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));

        Epic secondEpic = new Epic("Купить штаны", "Разбить покупку штанов на маленькие задачи", Status.NEW);
        taskManager.createEpic(secondEpic);
        SubTask firstSubTaskForSecondEpic = new SubTask("Пойти в магазин", "Сесть на автобус, " +
                "доехать до магазина", Status.NEW, secondEpic.getId(), LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(10));

        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);
        taskManager.createSubTask(firstSubTaskForSecondEpic);
        List<Epic> tempList = new ArrayList<>();
        tempList.add(firstEpic);
        tempList.add(secondEpic);


        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<Epic> epicList = gson.fromJson(response.body(), new TasksListTypeToken().getType());
            assertNotNull(epicList);
            assertEquals(tempList.size(), epicList.size());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Test
    void checkGetEpicById() {

        String path = "http://localhost:8080/epics/1";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            Epic tempTask = gson.fromJson(response.body(), Epic.class);
            assertEquals(firstEpic, tempTask, "Задачи не совпадают");

        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkGetEpicSubTasks() {

        String path = "http://localhost:8080/epics/1/subtasks";
        url = URI.create(path);

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));

        Epic secondEpic = new Epic("Купить штаны", "Разбить покупку штанов на маленькие задачи", Status.NEW);
        taskManager.createEpic(secondEpic);
        SubTask firstSubTaskForSecondEpic = new SubTask("Пойти в магазин", "Сесть на автобус, " +
                "доехать до магазина", Status.NEW, secondEpic.getId(), LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(10));

        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);
        taskManager.createSubTask(firstSubTaskForSecondEpic);
        List<SubTask> tempList = new ArrayList<>();
        tempList.add(firstSubTaskForFristEpic);
        tempList.add(secondSubTaskForFristEpic);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<SubTask> subTaskListId = gson.fromJson(response.body(), new TasksListTypeToken().getType());
            assertNotNull(subTaskListId);
            assertEquals(tempList.size(), subTaskListId.size());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkDeleteEpic() {

        String path = "http://localhost:8080/epics";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);
        List<Epic> tempList = new ArrayList<>();
        tempList.add(firstEpic);


        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            List<Epic> subTaskList = taskManager.getAllEpics();
            assertNotEquals(tempList.size(), subTaskList.size());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkDeleteEpicById() {

        String path = "http://localhost:8080/epics/1";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);
        Epic secondEpic = new Epic("Купить штаны", "Разбить покупку штанов на маленькие задачи", Status.NEW);
        taskManager.createEpic(secondEpic);
        SubTask firstSubTaskForSecondEpic = new SubTask("Пойти в магазин", "Сесть на автобус, " +
                "доехать до магазина", Status.NEW, secondEpic.getId(), LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(10));


        taskManager.createSubTask(firstSubTaskForSecondEpic);
        List<Epic> tempList = new ArrayList<>();
        tempList.add(secondEpic);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
            List<Epic> tasks = taskManager.getAllEpics();
            assertEquals(tempList.size(), tasks.size());
            assertThrows(NotFoundException.class, () -> {
                taskManager.getTaskByld(1);
            });
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkPrioritiTasks() {

        String path = "http://localhost:8080/prioritized";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Task task1 = new Task("Мыть окна", "взять тряку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Мыть пол", "взять тряку", Status.NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));
        Task task3 = new Task("Мыть посуду", "взять тряку", Status.NEW, LocalDateTime.now().plusMinutes(35), Duration.ofMinutes(10));
        Task task4 = new Task("Сохранить грязную посуду", "взять тряку", Status.NEW, LocalDateTime.of(2024, 03, 2, 15, 0, 0), Duration.ofMinutes(10));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        List<Task> tempList = taskManager.getPrioritizedTasks();

        List<Task> secondTempList = new ArrayList<>();
        secondTempList.add(task4);
        secondTempList.add(task1);
        secondTempList.add(task2);
        secondTempList.add(task3);

        assertEquals(secondTempList, tempList, "листы не совпадают");

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<Task> serverList = gson.fromJson(response.body(), new TasksListTypeToken().getType());
            assertEquals(tempList.size(), serverList.size());
            assertEquals(tempList.get(0).getName(), serverList.get(0).getName());
            assertEquals(tempList.get(1).getName(), serverList.get(1).getName());
            assertEquals(tempList.get(2).getName(), serverList.get(2).getName());
            assertEquals(tempList.get(3).getName(), serverList.get(3).getName());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkHistoryTasks() {

        String path = "http://localhost:8080/history";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Task task1 = new Task("Мыть окна", "взять тряку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Мыть пол", "взять тряку", Status.NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10));
        Task task3 = new Task("Мыть посуду", "взять тряку", Status.NEW, LocalDateTime.now().plusMinutes(35), Duration.ofMinutes(10));
        Task task4 = new Task("Сохранить грязную посуду", "взять тряку", Status.NEW, LocalDateTime.of(2024, 03, 2, 15, 0, 0), Duration.ofMinutes(10));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);

        taskManager.getTaskByld(4);
        taskManager.getTaskByld(3);
        taskManager.getTaskByld(2);
        taskManager.getTaskByld(1);
        List<Task> secondTempList = taskManager.getHistory();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            List<Task> serverList = gson.fromJson(response.body(), new TasksListTypeToken().getType());
            assertEquals(secondTempList.size(), serverList.size());
            assertEquals(secondTempList.get(0).getName(), serverList.get(0).getName());
            assertEquals(secondTempList.get(1).getName(), serverList.get(1).getName());
            assertEquals(secondTempList.get(2).getName(), serverList.get(2).getName());
            assertEquals(secondTempList.get(3).getName(), serverList.get(3).getName());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void stopServer() {
    }

    @Test
    void checkGetTaskByIdWithError() {

        String path = "http://localhost:8080/tasks/10";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Task task1 = new Task("Мыть окна", "взять тряку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        taskManager.createTask(task1);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkPostCreateTaskWithError() {

        String path = "http://localhost:8080/tasks";
        url = URI.create(path);
        Task task2 = new Task("Мыть пол", "взять тряку в руки", Status.NEW, LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(5000));
        taskManager.createTask(task2);

        Task task1 = new Task("Мыть окна", "взять тряку", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(1000));
        String taskJson = gson.toJson(task1);
        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkGetSubTaskByIdWithEror() {

        String path = "http://localhost:8080/subtasks/100";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());

        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkPostCreateSubTaskWithError() {

        String path = "http://localhost:8080/subtasks";
        url = URI.create(path);
        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(),
                LocalDateTime.of(2023, 01, 3, 10, 0, 0), Duration.ofMinutes(100000));
        taskManager.createSubTask(firstSubTaskForFristEpic);

        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "сдать 9 спринт", Status.NEW, firstEpic.getId(),
                LocalDateTime.of(2023, 01, 3, 10, 55, 50), Duration.ofMinutes(160));

        String taskJson = gson.toJson(secondSubTaskForFristEpic);
        HttpRequest request = HttpRequest
                .newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(406, response.statusCode());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }


    @Test
    void checkGetEpicByIdWithError() {

        String path = "http://localhost:8080/epics/65";
        url = URI.create(path);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));
        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    void checkGetEpicSubTasksWithError() {

        String path = "http://localhost:8080/epics/64/subtasks";
        url = URI.create(path);

        Epic firstEpic = new Epic("Пройти 4 спринт", "Разбить прохождение српинта на маленькие задачи", Status.NEW);
        taskManager.createEpic(firstEpic);
        SubTask firstSubTaskForFristEpic = new SubTask("Изучить теорию", "Пройти уроки спринта", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(40), Duration.ofMinutes(10));
        SubTask secondSubTaskForFristEpic = new SubTask("Пройти тренажер", "...", Status.NEW, firstEpic.getId(), LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(10));

        Epic secondEpic = new Epic("Купить штаны", "Разбить покупку штанов на маленькие задачи", Status.NEW);
        taskManager.createEpic(secondEpic);
        SubTask firstSubTaskForSecondEpic = new SubTask("Пойти в магазин", "Сесть на автобус, " +
                "доехать до магазина", Status.NEW, secondEpic.getId(), LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(10));

        taskManager.createSubTask(firstSubTaskForFristEpic);
        taskManager.createSubTask(secondSubTaskForFristEpic);
        taskManager.createSubTask(firstSubTaskForSecondEpic);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        } catch (Exception e) {
            System.out.println("Ошибка в тесте\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localTime) throws IOException {
            if (localTime == null) {
                jsonWriter.value("null");
            } else
                jsonWriter.value(localTime.format(timeFormatter));
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse((jsonReader.nextString()), timeFormatter);
        }
    }

    class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration localTime) throws IOException {
            if (localTime == null) {
                jsonWriter.value("null");
            } else {
                jsonWriter.value(localTime.toString());
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            return Duration.parse((jsonReader.nextString()));
        }
    }

    class TasksListTypeToken extends TypeToken<List<Task>> {
    }

    class IntegerListTypeToken extends TypeToken<List<Integer>> {
    }
}