package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, Status status, int idEpic) {
        super(name, description, status);
        this.epicId = idEpic;
    }

    public SubTask(String name, String description, Status status, int idEpic, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
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
