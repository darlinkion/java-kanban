package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

public interface TaskManager {
    int createTask(Task task);

    int createSubTask(SubTask subTask);

    int createEpic(Epic epic);

    Task getTaskByld(Integer id);

    Epic getEpicByld(Integer id);

    SubTask getSubTaskByld(Integer id);

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

    void updateTimeForAllEpics();

    List<SubTask> getEpicListSubTask(Integer id);

    TreeMap<LocalDateTime, Task> getPrioritizedTasks();
}
