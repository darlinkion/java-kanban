package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    private HistoryManager historyManager;

    private int id;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        id = 0;
    }

    public List<Task> getHistoryList() {
        return historyManager.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generationId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void createSubTask(SubTask subTask) {
        Epic tempEpic = getEpicByldForCreatSubtask(subTask.getEpicId());
        if (tempEpic == null) {
            return;
        }

        subTask.setId(generationId());
        subTasks.put(subTask.getId(), subTask);
        tempEpic.addSubTaskId(subTask.getId());
        updateEpicStatus(tempEpic.getId());
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generationId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public Task getTaskByld(int id) {
        Task tempTask = tasks.get(id);
        historyManager.addTaskHistory(tempTask);
        return tempTask;
    }

    @Override
    public Epic getEpicByld(int id) {
        Epic tempEpic = epics.get(id);
        historyManager.addTaskHistory(tempEpic);
        return tempEpic;
    }

    @Override
    public SubTask getSubTaskByld(int id) {
        SubTask tempSubTask = subTasks.get(id);
        historyManager.addTaskHistory(tempSubTask);
        return tempSubTask;
    }

    @Override
    public List<Task> getAllTasks() {
        ArrayList<Task> taskList = new ArrayList<>();

        for (Task task : tasks.values()) {
            taskList.add(task);
        }
        return taskList;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        ArrayList<SubTask> subTaskList = new ArrayList<>();

        for (SubTask subTask : subTasks.values()) {
            subTaskList.add(subTask);
        }
        return subTaskList;
    }

    @Override
    public List<Epic> getAllEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();

        for (Epic epic : epics.values()) {
            epicList.add(epic);
        }
        return epicList;
    }


    @Override
    public void cleanTasks() {
        tasks.clear();
    }

    @Override
    public void cleanSubTasks() {
        subTasks.clear();
    }

    @Override
    public void cleanEpics() {
        epics.clear();
    }

    @Override
    public void updateTask(Task task) {
        Task tempTask = tasks.get(task.getId());
        if (tempTask != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Tакого Task не существует!");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask tempSubTask = subTasks.get(subTask.getId());
        if (tempSubTask != null) {
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(subTask.getEpicId());
        } else {
            System.out.println("Tакого SubTask не существует!");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic tempEpic = epics.get(epic.getId());
        if (tempEpic != null) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Tакого Epic не существует!");
        }
    }

    @Override
    public void removeForIdTask(Integer id) {
        tasks.remove(id);
    }

    @Override
    public void removeForIdEpic(Integer id) {
        Epic tempEpic = epics.get(id);
        if (tempEpic == null) return;
        for (Integer subTaskId : tempEpic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    @Override
    public void removeForIdSubTask(Integer id) {
        SubTask tempSubTask = subTasks.get(id);
        Epic tempEpic = epics.get(tempSubTask.getEpicId());

        tempEpic.removeSubTaskId(id);
        subTasks.remove(id);
        updateEpicStatus(tempEpic.getId());

    }

    @Override
    public List<SubTask> getEpicListSubTask(Integer id) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        Epic tempEpic = epics.get(id);
        if (tempEpic == null) return null;

        for (Integer tempId : tempEpic.getSubTaskIds()) {
            subTaskList.add(subTasks.get(tempId));
        }

        return subTaskList;
    }

    private int generationId() {
        return ++id;
    }

    private Epic getEpicByldForCreatSubtask(int id) {
        return epics.get(id);
    }

    private void updateEpicStatus(Integer epicId) {
        Epic tempEpic = epics.get(epicId);
        int statusNew = 0;
        int statusDone = 0;

        if (tempEpic == null) return;
        if (tempEpic.getSubTaskIds() == null) {
            tempEpic.setStatus(Status.NEW);
        }
        SubTask tempSubTask;
        for (Integer tempId : tempEpic.getSubTaskIds()) {
            tempSubTask = subTasks.get(tempId);

            if (tempSubTask.getStatus() == Status.NEW) {
                statusNew++;
            }
            if (tempSubTask.getStatus() == Status.DONE) {
                statusDone++;
            }
        }
        if (tempEpic.getSubTaskIds().size() == statusNew) {
            tempEpic.setStatus(Status.NEW);
        } else if (tempEpic.getSubTaskIds().size() == statusDone) {
            tempEpic.setStatus(Status.DONE);
        } else {
            tempEpic.setStatus(Status.IN_PROGRESS);
        }
    }
}
