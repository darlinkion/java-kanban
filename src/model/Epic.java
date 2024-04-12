package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTaskIds;
    private LocalDateTime endTimeEpic = null;

    public Epic(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        subTaskIds = new ArrayList<>();
    }

    public List<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
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

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTimeEpic;
    }

    public void setEndTimeEpic(LocalDateTime end) {
        endTimeEpic = end;
    }
}
