package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {
    int createTask(Task task);

    int createSubTask(SubTask subTask);

    int createEpic(Epic epic);

    Task getTaskByld(int id);

    Epic getEpicByld(int id);

    SubTask getSubTaskByld(int id);

    List<Task> getHistory();

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
