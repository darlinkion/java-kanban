package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    int id;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        id = 0;
    }

    public int generationId() {
        return ++id;
    }

    public Task createTask(Task task) {
        task.setId(generationId());
        tasks.put(task.getId(), task);
        return task;
    }

    public void createSubTask(SubTask subTask) {
        Epic tempEpic = getForIdEpic(subTask.getIdEpic());
        if (tempEpic == null) {
            return;
        }

        subTask.setId(generationId());
        subTasks.put(subTask.getId(), subTask);
        tempEpic.addSubTaskId(subTask.getId());
        updateEpicStatus(tempEpic.getId());
    }

    public void createEpic(Epic epic) {
        epic.setId(generationId());
        epics.put(epic.getId(), epic);
    }

    public Task getForIdTask(int id) {
        return tasks.get(id);
    }

    public Epic getForIdEpic(int id) {
        return epics.get(id);
    }

    public SubTask getForIdSubTask(int id) {
        return subTasks.get(id);
    }

    public ArrayList<Task> getAllTask() {
        ArrayList<Task> taskList = new ArrayList<>();

        for (Task task : tasks.values()) {
            taskList.add(task);
        }
        return taskList;
    }

    public ArrayList<SubTask> getAllSubTask() {
        ArrayList<SubTask> subTaskList = new ArrayList<>();

        for (SubTask subTask : subTasks.values()) {
            subTaskList.add(subTask);
        }
        return subTaskList;
    }

    public ArrayList<Epic> getAllEpic() {
        ArrayList<Epic> epicList = new ArrayList<>();

        for (Epic epic : epics.values()) {
            epicList.add(epic);
        }
        return epicList;
    }

    public void cleanTasks() {
        tasks.clear();
    }

    public void cleanSubTasks() {
        subTasks.clear();
    }

    public void cleanEpics() {
        epics.clear();
    }

    public void updateTask(Task task) {
        Task tempTask = tasks.get(task.getId());
        if (tempTask != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Tакого Task не существует!");
        }
    }

    public void updateSubTask(SubTask subTask) {
        SubTask tempSubTask = subTasks.get(subTask.getId());
        if (tempSubTask != null) {
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(subTask.getIdEpic());
        } else {
            System.out.println("Tакого SubTask не существует!");
        }
    }

    public void updateEpic(Epic epic) {
        Epic tempEpic = epics.get(epic.getId());
        if (tempEpic != null) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Tакого Epic не существует!");
        }
    }

    public void removeForIdTask(Integer id) {
        tasks.remove(id);
    }

    public void removeForIdEpic(Integer id) {
        Epic tempEpic = epics.get(id);
        if (tempEpic == null) return;
        for (Integer subTaskId : tempEpic.getListIdSubTasks()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    public void removeForIdSubTask(Integer id) {
        SubTask tempSubTask = subTasks.get(id);
        Epic tempEpic = epics.get(tempSubTask.getIdEpic());

        tempEpic.removeSubTaskId(id);
        subTasks.remove(id);
        updateEpicStatus(tempEpic.getId());

    }

    public ArrayList<SubTask> getEpicListSubTask(Integer id) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        Epic tempEpic = epics.get(id);
        if (tempEpic == null) return null;

        for (Integer tempId : tempEpic.getListIdSubTasks()) {
            subTaskList.add(subTasks.get(tempId));
        }

        return subTaskList;
    }

    private void updateEpicStatus(Integer epicId) {
        Epic tempEpic = epics.get(epicId);
        int statusNew = 0;
        int statusDone = 0;

        if (tempEpic == null) return;
        if (tempEpic.getListIdSubTasks() == null) {
            tempEpic.setStatus(Status.NEW);
        }
        SubTask tempSubTask;
        for (Integer tempId : tempEpic.getListIdSubTasks()) {
            tempSubTask = subTasks.get(tempId);

            if (tempSubTask.getStatus() == Status.NEW) {
                statusNew++;
            }
            if (tempSubTask.getStatus() == Status.DONE) {
                statusDone++;
            }
        }
        if (tempEpic.getListIdSubTasks().size() == statusNew) {
            tempEpic.setStatus(Status.NEW);
        } else if (tempEpic.getListIdSubTasks().size() == statusDone) {
            tempEpic.setStatus(Status.DONE);
        } else {
            tempEpic.setStatus(Status.IN_PROGRESS);
        }
    }
}
