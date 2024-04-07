package service;

import exception.NotFoundException;
import exception.ValidationExeption;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final TreeMap<LocalDateTime, Task> prioritizedTasks;
    protected final HistoryManager historyManager;

    protected int id;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.prioritizedTasks = new TreeMap<>();
        id = 0;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistoryList();
    }

    private boolean timeCrossing(Task task) {
        if (task == null) {
            throw new ValidationExeption("Передан пустой Task для проверки пересечения задач по времени");
        }
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        if (startTime == null || endTime == null) {
            throw new ValidationExeption("Ошибка задачи в части полей startTime или endTime\n" + task);
        }

        for (Task tempTask : prioritizedTasks.values()) {
            LocalDateTime oldStartTime = tempTask.getStartTime();
            LocalDateTime oldEndTime = tempTask.getEndTime();

            boolean firstCross = checkDataTimeCrossing(startTime, oldStartTime, oldEndTime);
            boolean secondCross = checkDataTimeCrossing(endTime, oldStartTime, oldEndTime);
            if (!firstCross || !secondCross) {
                return true;
            }
        }

        return false;
    }

    private boolean checkDataTimeCrossing(LocalDateTime localDateTime, LocalDateTime start, LocalDateTime end) {
        return localDateTime.isAfter(end) || localDateTime.isBefore(start);
    }


    @Override
    public int createTask(Task task) {
        int tempId;
        if (task == null) {
            return -1;
        }

        if (!timeCrossing(task)) {
            tempId = generationId();
            task.setId(tempId);
            tasks.put(task.getId(), task);
            prioritizedTasks.put(task.getStartTime(), task);
            return task.getId();
        }
        return -3;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int tempId;
        if (subTask == null) {
            return -2;
        }
        Integer tempEpicId = subTask.getEpicId();
        Epic tempEpic = getEpicByldForCreatSubtask(tempEpicId);
        if (tempEpic == null) {
            return -1;
        }
        if (!timeCrossing(subTask)) {
            tempId = generationId();
            subTask.setId(tempId);
            subTasks.put(tempId, subTask);
            prioritizedTasks.put(subTask.getStartTime(), subTask);
            tempEpic.addSubTaskId(tempId);
            updateEpicStatus(tempEpicId);
            updateTimeAndDurationEpic(tempEpicId);
            return tempId;
        }
        return -3;
    }

    @Override
    public int createEpic(Epic epic) {
        int tempId;
        if (epic == null) {
            return -1;
        }
        tempId = generationId();
        epic.setId(tempId);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Task getTaskByld(Integer id) {
        if (id == null) {
            throw new NotFoundException("Поиск Task по null id");
        }
        Task tempTask = tasks.get(id);
        if (tempTask == null) {
            throw new NotFoundException("Не найден Task по id" + id);
        }
        historyManager.addTaskHistory(tempTask);
        return tempTask;
    }

    @Override
    public Epic getEpicByld(Integer id) {
        if (id == null) {
            throw new NotFoundException("Поиск Epic по null id");
        }
        Epic tempEpic = epics.get(id);
        if (tempEpic == null) {
            throw new NotFoundException("Не найден Epic по id" + id);
        }
        historyManager.addTaskHistory(tempEpic);
        return tempEpic;
    }

    @Override
    public SubTask getSubTaskByld(Integer id) {
        if (id == null) {
            throw new NotFoundException("Поиск SubTask по null id");
        }
        SubTask tempSubTask = subTasks.get(id);
        if (tempSubTask == null) {
            throw new NotFoundException("Не найден Subtask по id" + id);
        }
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
        for (Integer key : tasks.keySet()) {
            historyManager.removeTaskFromHistory(key);
        }
        tasks.clear();
    }

    @Override
    public void cleanSubTasks() {
        for (Map.Entry<Integer, SubTask> tempMap : subTasks.entrySet()) {
            Epic tempEpic = epics.get(tempMap.getValue().getEpicId());
            tempEpic.cleanSubTasksInEpic();
            tempEpic.setStatus(Status.NEW);
            historyManager.removeTaskFromHistory(tempMap.getKey());
        }
        subTasks.clear();
    }

    @Override
    public void cleanEpics() {
        for (Map.Entry<Integer, Epic> tempMap : epics.entrySet()) {
            historyManager.removeTaskFromHistory(tempMap.getKey());
            Epic tempEpic = tempMap.getValue();
            for (Integer idSubtaskInEpic : tempEpic.getSubTaskIds()) {
                historyManager.removeTaskFromHistory(idSubtaskInEpic);
            }
            tempEpic.cleanSubTasksInEpic();
        }
        cleanSubTasks();
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void updateTask(Task task) {
        Integer taskId = task.getId();
        if (taskId == null) {
            throw new ValidationExeption("У Task не присвоен id");
        }
        Task tempTask = tasks.get(taskId);
        if (tempTask != null) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Tакого Task не существует!");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Integer subTaskId = subTask.getId();
        if (subTaskId == null) {
            throw new NotFoundException("У SubTask не присвоен id");
        }
        SubTask tempSubTask = subTasks.get(subTaskId);
        if (tempSubTask != null) {
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(subTask.getEpicId());
            updateTimeAndDurationEpic(subTask.getEpicId());
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
        historyManager.removeTaskFromHistory(id);
    }

    @Override
    public void removeForIdEpic(Integer id) {
        Epic tempEpic = epics.get(id);
        if (tempEpic == null) return;
        for (Integer subTaskId : tempEpic.getSubTaskIds()) {
            subTasks.remove(subTaskId);
            historyManager.removeTaskFromHistory(subTaskId);
        }
        epics.remove(id);
        historyManager.removeTaskFromHistory(id);
    }

    @Override
    public void removeForIdSubTask(Integer id) {
        SubTask tempSubTask = subTasks.get(id);
        Epic tempEpic = epics.get(tempSubTask.getEpicId());

        tempEpic.removeSubTaskId(id);
        subTasks.remove(id);
        historyManager.removeTaskFromHistory(id);
        updateEpicStatus(tempEpic.getId());
        updateTimeAndDurationEpic(tempEpic.getId());
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

    @Override
    public void updateTimeForAllEpics() {
        for (Integer tempEpicId : epics.keySet()) {
            updateTimeAndDurationEpic(tempEpicId);
        }
    }

    @Override
    public TreeMap<LocalDateTime, Task> getPrioritizedTasks() {
        return new TreeMap<>(prioritizedTasks);
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

        if (tempEpic == null) {
            throw new NotFoundException("Не найден Epic по id:" + epicId);
        }
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

    private void updateTimeAndDurationEpic(Integer epicId) {
        Epic tempEpic = epics.get(epicId);
        LocalDateTime startTimeEpic = null;
        LocalDateTime endTimeEpic = null;
        Duration durationEpic = null;
        List<Integer> subTasksList;

        if (tempEpic == null) {
            throw new NotFoundException("Не найден Epic по id:" + epicId);
        }

        LocalDateTime oldStartTimeEpic = tempEpic.getStartTime();
        subTasksList = tempEpic.getSubTaskIds();

        if (subTasksList == null) {
            return;
        }

        for (int i = 0; i < subTasksList.size(); i++) {
            SubTask tempSubtask = subTasks.get(subTasksList.get(i));
            if (tempSubtask == null) {
                throw new NotFoundException("Не найдет Subtask по id при расчете времени Epic");
            }

            if (durationEpic != null) {
                durationEpic = durationEpic.plus(tempSubtask.getDuration());
            } else {
                durationEpic = tempSubtask.getDuration();
            }

            if (startTimeEpic != null) {
                if (startTimeEpic.isAfter(tempSubtask.getStartTime())) {
                    startTimeEpic = tempSubtask.getStartTime();
                }
                if (endTimeEpic.isBefore(tempSubtask.getEndTime())) {
                    endTimeEpic = tempSubtask.getEndTime();
                }
            } else {
                startTimeEpic = tempSubtask.getStartTime();
                endTimeEpic = tempSubtask.getEndTime();
            }
        }

        tempEpic.setDuration(durationEpic);
        tempEpic.setStartTime(startTimeEpic);
        tempEpic.setEndTimeEpic(endTimeEpic);
    }
}
