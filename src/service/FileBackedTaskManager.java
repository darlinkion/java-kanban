package service;

import exception.ManagerIOException;
import exception.ManagerLoadFileException;
import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = Managers.getDefaultFile(file);
        List<String> linesList = new ArrayList<>();
        int maxId = 0;
        int tempId;


        try (FileReader fileReader = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fileReader);
            while (br.ready()) {
                String line = br.readLine();
                linesList.add(line);
            }

            if (linesList.isEmpty()) {
                return Managers.getDefaultFile();
            }

            for (int i = 1; i < linesList.size() - 2; i++) {

                String[] oneLine = linesList.get(i).split(",");
                if ((TaskType.TASK).toString().equals(oneLine[1])) {
                    Task tempTask = new Task(oneLine[2], oneLine[4], Status.valueOf(oneLine[3]));
                    tempId = Integer.parseInt(oneLine[0]);
                    tempTask.setId(tempId);
                    if (!oneLine[6].equals("null")) {
                        tempTask.setDuration(Duration.parse(oneLine[6]));
                    }
                    if (!oneLine[7].equals("null")) {
                        tempTask.setStartTime(LocalDateTime.parse(oneLine[7]));
                    }
                    if (tempId > maxId) {
                        maxId = tempId;
                    }
                    fileBackedTaskManager.tasks.put(tempId, tempTask);
                } else if ((TaskType.EPIC).toString().equals(oneLine[1])) {
                    Epic tempEpic = new Epic(oneLine[2], oneLine[4], Status.valueOf(oneLine[3]));
                    tempId = Integer.parseInt(oneLine[0]);
                    tempEpic.setId(tempId);
                    if (tempId > maxId) {
                        maxId = tempId;
                    }
                    fileBackedTaskManager.epics.put(tempId, tempEpic);
                } else if ((TaskType.SUBTASK).toString().equals(oneLine[1])) {
                    SubTask tempSubTask = new SubTask(oneLine[2], oneLine[4], Status.valueOf(oneLine[3]), Integer.parseInt(oneLine[5]));
                    tempId = Integer.parseInt(oneLine[0]);
                    tempSubTask.setId(tempId);
                    if (!oneLine[6].equals("null")) {
                        tempSubTask.setDuration(Duration.parse(oneLine[6]));
                    }
                    if (!oneLine[7].equals("null")) {
                        tempSubTask.setStartTime(LocalDateTime.parse(oneLine[7]));
                    }
                    if (tempId > maxId) {
                        maxId = tempId;
                    }
                    fileBackedTaskManager.subTasks.put(tempId, tempSubTask);
                }
            }

            for (SubTask subTask : fileBackedTaskManager.subTasks.values()) {
                int tempEpicId = subTask.getEpicId();
                Epic tempEpic = fileBackedTaskManager.epics.get(tempEpicId);
                tempEpic.addSubTaskId(subTask.getId());
            }
            fileBackedTaskManager.historyFromString(linesList.get(linesList.size() - 1));
            fileBackedTaskManager.updateTimeForAllEpics();

        } catch (FileNotFoundException e) {
            throw new ManagerLoadFileException("Файл не найден " + e.getMessage());
        } catch (IOException e) {
            throw new ManagerIOException("Ошибка IO " + e.getMessage());
        }
        fileBackedTaskManager.id = maxId;

        return fileBackedTaskManager;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int createTask(Task task) {
        int temp = super.createTask(task);
        save();
        return temp;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int temp = super.createSubTask(subTask);
        save();
        return temp;
    }

    @Override
    public int createEpic(Epic epic) {
        int temp = super.createEpic(epic);
        save();
        return temp;
    }

    @Override
    public Task getTaskByld(Integer id) {
        Task tempTask = super.getTaskByld(id);
        save();
        return tempTask;
    }

    @Override
    public Epic getEpicByld(Integer id) {
        Epic tempEpic = super.getEpicByld(id);
        save();
        return tempEpic;
    }

    @Override
    public SubTask getSubTaskByld(Integer id) {
        SubTask tempSubTask = super.getSubTaskByld(id);
        save();
        return tempSubTask;
    }

    @Override
    public void cleanTasks() {
        super.cleanTasks();
        save();
    }

    @Override
    public void cleanSubTasks() {
        super.cleanSubTasks();
        save();
    }

    @Override
    public void cleanEpics() {
        super.cleanEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeForIdTask(Integer id) {
        super.removeForIdTask(id);
        save();
    }

    @Override
    public void removeForIdEpic(Integer id) {
        super.removeForIdEpic(id);
        save();
    }

    @Override
    public void removeForIdSubTask(Integer id) {
        super.removeForIdSubTask(id);
        save();
    }

    private String historyToString(HistoryManager manager) {
        StringBuilder historyStringLine = new StringBuilder();
        List<Task> tempListHistoryManager = manager.getHistoryList();
        if (!tempListHistoryManager.isEmpty())
            historyStringLine.append(tempListHistoryManager.get(0).getId());
        for (int i = 1; i < tempListHistoryManager.size(); i++) {
            Task task = tempListHistoryManager.get(i);
            historyStringLine.append("," + task.getId());
        }
        return historyStringLine.toString();
    }

    private void historyFromString(String value) {
        if (!value.isBlank()) {
            String[] historyStringArray = value.split(",");
            for (String stringTask : historyStringArray) {
                Task task = tasks.get(Integer.parseInt(stringTask));
                if (task == null) {
                    task = epics.get(Integer.parseInt(stringTask));
                }
                if (task == null) {
                    task = subTasks.get(Integer.parseInt(stringTask));
                }
                historyManager.addTaskHistory(task);
            }
        }
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic,duration,startTime\n");

            for (Task task : tasks.values()) {
                fileWriter.write(task.toString(task));
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(epic.toString(epic));
            }
            for (SubTask subTask : subTasks.values()) {
                fileWriter.write(subTask.toString(subTask));
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи данных в файл ");
        }
    }
}
