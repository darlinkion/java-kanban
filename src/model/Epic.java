package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskIds;

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subTaskIds = new ArrayList<>();
    }

    public void addSubTaskId(int idSubTask) {
        subTaskIds.add(idSubTask);
    }

    public void removeSubTaskId(Integer idSubTask) {
        subTaskIds.remove(idSubTask);
    }

}
