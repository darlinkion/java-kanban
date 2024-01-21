package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> listIdSubTasks;

    public ArrayList<Integer> getListIdSubTasks() {
        return listIdSubTasks;
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        listIdSubTasks = new ArrayList<>();
    }

    public void addSubTaskId(int idSubTask) {
        listIdSubTasks.add(idSubTask);
    }

    public void removeSubTaskId(Integer idSubTask) {
        listIdSubTasks.remove(idSubTask);
    }

}
