import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = new TaskManager();
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

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubTask());

        firstTask.setStatus(Status.DONE);
        secondTask.setStatus(Status.DONE);
        firstSubTaskForFristEpic.setStatus(Status.DONE);
        secondSubTaskForFristEpic.setStatus(Status.DONE);
        firstSubTaskForSecondEpic.setStatus(Status.IN_PROGRESS);

        taskManager.updateTask(firstTask);
        taskManager.updateTask(secondTask);
        taskManager.updateSubTask(firstSubTaskForFristEpic);
        taskManager.updateSubTask(secondSubTaskForFristEpic);
        taskManager.updateSubTask(firstSubTaskForSecondEpic);

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubTask());

        taskManager.removeForIdTask(1);
        taskManager.removeForIdEpic(3);

        System.out.println(taskManager.getAllTask());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSubTask());
    }
}
