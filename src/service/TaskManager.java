package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    void createSubTask(SubTask subTask);

    void createEpic(Epic epic);

    Task getTaskByld(int id);

    Epic getEpicByld(int id);

    SubTask getSubTaskByld(int id);

    List<Task> getAllTasks();

    List<SubTask> getAllSubTasks();

    List<Epic> getAllEpics();

    void cleanTasks();

    void cleanSubTasks();

    void cleanEpics();

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void removeForIdTask(Integer id);

    void removeForIdEpic(Integer id);

    void removeForIdSubTask(Integer id);

    List<SubTask> getEpicListSubTask(Integer id);
}
