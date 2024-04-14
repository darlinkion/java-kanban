package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.NotFoundException;
import exception.ValidationExeption;
import model.Epic;
import model.SubTask;
import model.Task;
import service.InMemoryTaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskHandler implements HttpHandler {

    private final InMemoryTaskManager taskManager;
    private final Gson gson;

    public TaskHandler(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
        LocalDateTimeTypeAdapter localDateTimeTypeAdapter = new LocalDateTimeTypeAdapter();
        DurationTypeAdapter localDurationTypeAdapter = new DurationTypeAdapter();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, localDateTimeTypeAdapter);
        gsonBuilder.registerTypeAdapter(Duration.class, localDurationTypeAdapter);
        gson = gsonBuilder
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metod = exchange.getRequestMethod();
        switch (metod) {
            case ("GET"):
                handlerGetRequest(exchange);
                break;
            case ("POST"):
                handlerPostRequest(exchange);
                break;
            case ("DELETE"):
                handlerDeleteRequest(exchange);
                break;
            default:
                writeResponse("Неправильный http метод", exchange, 405);
                break;
        }
    }

    private void handlerGetRequest(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String task = pathParts[1];

        switch (task) {
            case ("tasks"):
                handlerGetTaskRequest(exchange);
                break;
            case ("epics"):
                handlerGetEpicRequest(exchange);
                break;
            case ("subtasks"):
                handlerGetSubtaskRequest(exchange);
                break;
            case ("history"):
                writeResponse(gson.toJson(taskManager.getHistory()), exchange, 200);
                break;
            case ("prioritized"):
                writeResponse(gson.toJson(taskManager.getPrioritizedTasks()), exchange, 200);
                break;
            default:
                writeResponse("Неправильный адрес", exchange, 400);
                break;
        }

    }

    private void handlerPostRequest(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String task = pathParts[1];

        switch (task) {
            case ("tasks"):
                handlerPostTaskRequest(exchange);
                break;
            case ("epics"):
                handlerPostEpicRequest(exchange);
                break;
            case ("subtasks"):
                handlerPostSubtaskRequest(exchange);
                break;
            default:
                writeResponse("Неправильный адрес", exchange, 400);
                break;
        }
    }

    private void handlerDeleteRequest(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String task = pathParts[1];

        switch (task) {
            case ("tasks"):
                handlerDeleteTaskRequest(exchange);
                break;
            case ("epics"):
                handlerDeleteEpicRequest(exchange);
                break;
            case ("subtasks"):
                handlerDeleteSubtaskRequest(exchange);
                break;
            default:
                writeResponse("Неправильный адрес", exchange, 400);
                break;
        }
    }

    private void handlerGetTaskRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] arrayPath = path.split("/");
        int id;
        if (arrayPath.length > 2) {
            id = Integer.parseInt(arrayPath[2]);
            try {
                writeResponse(gson.toJson(taskManager.getTaskByld(id)), exchange, 200);

            } catch (NotFoundException exception) {
                writeResponse("Не найдена задача по id" + id, exchange, 404);
            }
        } else {
            writeResponse(gson.toJson(taskManager.getAllTasks()), exchange, 200);
        }
    }

    private void handlerGetSubtaskRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] arrayPath = path.split("/");
        int id;
        if (arrayPath.length > 2) {
            id = Integer.parseInt(arrayPath[2]);
            try {
                writeResponse(gson.toJson(taskManager.getSubTaskByld(id)), exchange, 200);
            } catch (NotFoundException exception) {
                writeResponse("Не найдена подзадача по id" + id, exchange, 404);
            }
        } else {
            writeResponse(gson.toJson(taskManager.getAllSubTasks()), exchange, 200);
        }
    }

    private void handlerGetEpicRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] arrayPath = path.split("/");
        int id;
        if (arrayPath.length == 4) {
            id = Integer.parseInt(arrayPath[2]);
            try {
                List<SubTask> tempList = taskManager.getEpicListSubTask(id);
                if (tempList != null) {
                    writeResponse(gson.toJson(tempList), exchange, 200);
                } else {
                    writeResponse("Не найден эпик", exchange, 404);
                }
            } catch (NotFoundException exception) {
                writeResponse("Не найден эпик по id:" + id, exchange, 404);
            }
        } else if (arrayPath.length == 3) {
            id = Integer.parseInt(arrayPath[2]);
            try {
                writeResponse(gson.toJson(taskManager.getEpicByld(id)), exchange, 200);
            } catch (NotFoundException exception) {
                writeResponse("Не найден эпик по id:" + id, exchange, 404);
            }
        } else {
            writeResponse(gson.toJson(taskManager.getAllEpics()), exchange, 200);
        }
    }

    private void handlerPostTaskRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        try {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isEmpty()) {
                writeResponse("Пустое тело запроса", exchange, 400);
            } else {
                Task gsonTask = gson.fromJson(body, Task.class);
                if (gsonTask.getId() == null) {
                    Integer tempId = taskManager.createTask(gsonTask);
                    if (tempId < 0) {
                        writeResponse("Ошибка создания задачи Task", exchange, 406);
                    }
                    writeResponse("Создана задача с id =" + tempId, exchange, 201);
                } else {
                    taskManager.updateTask(gsonTask);
                    writeResponse("Изменена задача", exchange, 200);
                }
            }
        } catch (Exception exeption) {
            writeResponse("Передана неверная задача", exchange, 406);
        }
    }

    private void handlerPostEpicRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        try {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isEmpty()) {
                writeResponse("Пустое тело запроса", exchange, 400);
            } else {
                Epic epic = gson.fromJson(body, Epic.class);
                if (epic.getId() == null) {
                    Integer tempId = taskManager.createEpic(epic);
                    if (tempId < 0) {
                        writeResponse("Ошибка создания задачи Epic", exchange, 406);
                    }
                    writeResponse("Создана задача с id =" + tempId, exchange, 201);
                } else {
                    taskManager.updateEpic(epic);
                    writeResponse("Epic обновлен", exchange, 200);
                }
            }
        } catch (Exception exeption) {
            writeResponse("Передана неверная задача", exchange, 406);
            exeption.getStackTrace();
        }
    }

    private void handlerPostSubtaskRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        try {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isEmpty()) {
                writeResponse("Пустое тело запроса", exchange, 400);
            } else {
                SubTask subTask = gson.fromJson(body, SubTask.class);

                if (subTask.getId() == null) {
                    Integer tempId = taskManager.createSubTask(subTask);
                    if (tempId < 0) {
                        writeResponse("Ошибка создания подзадачи SubTask", exchange, 406);
                    }
                    writeResponse("Создана задача с id =" + tempId, exchange, 201);
                } else {
                    taskManager.updateSubTask(subTask);
                    writeResponse("Изменена задача", exchange, 200);
                }
            }
        } catch (Exception exeption) {
            writeResponse("Передана неверная задача", exchange, 406);
        }
    }

    private void handlerDeleteTaskRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] arrayPath = path.split("/");
        int id;

        if (arrayPath.length == 3) {
            id = Integer.parseInt(arrayPath[2]);
            try {
                taskManager.removeForIdTask(id);
                writeResponse("Задача успешно удалена", exchange, 204);
            } catch (NotFoundException exception) {
                writeResponse("Не удалось удалить Task по id:" + id, exchange, 404);
            }
        } else {
            taskManager.cleanTasks();
            writeResponse("Удалены все задачи", exchange, 204);
        }
    }

    private void handlerDeleteSubtaskRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] arrayPath = path.split("/");
        int id;

        if (arrayPath.length == 3) {
            id = Integer.parseInt(arrayPath[2]);
            try {
                taskManager.removeForIdSubTask(id);
                writeResponse("Подзадача успешно удалена", exchange, 204);
            } catch (NotFoundException exception) {
                writeResponse("Не удалось удалить Subtask по id:" + id, exchange, 404);
            }
        } else {
            taskManager.cleanSubTasks();
            writeResponse("Удалены все подзадачи", exchange, 204);
        }
    }

    private void handlerDeleteEpicRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] arrayPath = path.split("/");
        int id;

        if (arrayPath.length == 3) {
            id = Integer.parseInt(arrayPath[2]);
            try {
                taskManager.removeForIdEpic(id);
                writeResponse("Эпик успешно удален", exchange, 204);
            } catch (NotFoundException exception) {
                writeResponse("Не удалось удалить Epic по id:" + id, exchange, 404);
            }
        } else {
            taskManager.cleanEpics();
            writeResponse("Удалены все эпики", exchange, 204);
        }
    }

    private void writeResponse(String body, HttpExchange exchange, int code) throws IOException {
        byte[] responseBody = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, responseBody.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBody);
        } catch (ValidationExeption exeption) {
            System.out.println("ошибка валидации " + exeption.getMessage());
        }
    }

    class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localTime) throws IOException {
            if (localTime == null) {
                jsonWriter.value("null");
            } else {
                jsonWriter.value(localTime.format(timeFormatter));
            }

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
}
