package service;

import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public File getFile() {
        return file;
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

            for (int i = 0; i < linesList.size() - 2; i++) {

                String[] oneLine = linesList.get(i).split(",");
                if ((TaskType.TASK).toString().equals(oneLine[1])) {
                    Task tempTask = new Task(oneLine[2], oneLine[4], Status.valueOf(oneLine[3]));
                    tempId = Integer.parseInt(oneLine[0]);
                    tempTask.setId(tempId);
                    if (tempId > maxId) {
                        maxId = tempId;
                    }
                    fileBackedTaskManager.createTask(tempTask);
                } else if ((TaskType.EPIC).toString().equals(oneLine[1])) {
                    Epic tempEpic = new Epic(oneLine[2], oneLine[4], Status.valueOf(oneLine[3]));
                    tempId = Integer.parseInt(oneLine[0]);
                    tempEpic.setId(tempId);
                    if (tempId > maxId) {
                        maxId = tempId;
                    }
                    fileBackedTaskManager.createEpic(tempEpic);
                }
            }
            for (int i = 0; i < linesList.size() - 2; i++) {

                String[] oneLine = linesList.get(i).split(",");
                if ((TaskType.SUBTASK).toString().equals(oneLine[1])) {
                    String[] tempStr = oneLine[5].split(";");
                    SubTask subTask = new SubTask(oneLine[2], oneLine[4], Status.valueOf(oneLine[3]), Integer.parseInt(tempStr[0]));
                    tempId = Integer.parseInt(oneLine[0]);
                    subTask.setId(tempId);
                    if (tempId > maxId) {
                        maxId = tempId;
                    }
                    fileBackedTaskManager.createSubTask(subTask);
                }
            }
            fileBackedTaskManager.historyFromString(linesList.get(linesList.size() - 1));


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IndexOutOfBoundsException exception) {
            return Managers.getDefaultFile();
        }
        fileBackedTaskManager.id = maxId;

        return fileBackedTaskManager;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
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
    public Task getTaskByld(int id) {
        return super.getTaskByld(id);
    }

    @Override
    public Epic getEpicByld(int id) {
        return super.getEpicByld(id);
    }

    @Override
    public SubTask getSubTaskByld(int id) {
        return super.getSubTaskByld(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
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

    @Override
    public List<SubTask> getEpicListSubTask(Integer id) {
        return super.getEpicListSubTask(id);
    }

    private String historyToString(HistoryManager manager) {
        StringBuilder historyStringLine = new StringBuilder();
        List<Task> tempListHistoryManager = manager.getHistoryList();

        for (Task task : tempListHistoryManager) {
            historyStringLine.append(task.getId() + ",");
        }
        //historyStringLine.deleteCharAt(historyStringLine.toString().length()-1);
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
            fileWriter.write("id,type,name,status,description,epic\n");

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
