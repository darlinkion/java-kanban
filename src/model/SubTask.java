package model;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, Status status, int idEpic) {
        super(name, description, status);
        this.epicId = idEpic;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }
}
