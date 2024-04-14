package server;

import com.sun.net.httpserver.HttpServer;
import service.InMemoryTaskManager;
import service.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final int port = 8080;
    private final InMemoryTaskManager taskManager;
    private HttpServer httpServer;
    private TaskHandler taskHandler;

    public HttpTaskServer() {
        taskManager = Managers.getDefault();
    }

    public HttpTaskServer(InMemoryTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void startServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        taskHandler = new TaskHandler(taskManager);

        httpServer.createContext("/tasks", taskHandler);
        httpServer.createContext("/tasks/id", taskHandler);
        httpServer.createContext("/tasks/Body:{task}", taskHandler);
        httpServer.createContext("/subtasks", taskHandler);
        httpServer.createContext("/subtasks/id", taskHandler);
        httpServer.createContext("/subtasks/Body:{subtasks}", taskHandler);
        httpServer.createContext("/epics", taskHandler);
        httpServer.createContext("/epics/id", taskHandler);
        httpServer.createContext("/epics/Body:{subtasks}", taskHandler);
        httpServer.createContext("/epics/id/subtasks", taskHandler);
        httpServer.createContext("/history", taskHandler);
        httpServer.createContext("/prioritized", taskHandler);


        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + port + " порту!");
    }

    public void stopServer() {
        httpServer.stop(1);
        System.out.println("HTTP-сервер остановлен");
    }
}
