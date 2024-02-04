package model;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private final List<Integer> subTaskIds;

    public List<Integer> getSubTaskIds() {
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
