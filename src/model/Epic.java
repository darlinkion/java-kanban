package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTaskIds;

    public List<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subTaskIds = new ArrayList<>();
    }

    public void cleanSubTasksInEpic() {
        subTaskIds.clear();
    }

    public void addSubTaskId(int idSubTask) {
        subTaskIds.add(idSubTask);
    }

    public void removeSubTaskId(Integer idSubTask) {
        subTaskIds.remove(idSubTask);
    }

}
