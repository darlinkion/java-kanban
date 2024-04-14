package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected String name;
    protected String description;
    protected Integer id = null;
    protected Status status;
    protected LocalDateTime startTime = null;
    protected Duration duration = null;

    public Task() {
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        startTime = LocalDateTime.now();
        duration = Duration.ZERO;
    }

    public Task(Integer id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ",name='" + name + '\'' +
                ",description='" + description + '\'' +
                ",status='" + status + '\'' +
                ",startTime='" + startTime + '\'' +
                ",duration='" + duration + '\'' +
                "}\n";
    }


    public String toString(Task task) {
        return task.getId() +
                "," + task.getTaskType() +
                "," + task.getName() +
                "," + task.getStatus() +
                "," + task.getDescription() +
                "," + task.getEpicId() +
                "," + task.getDuration() +
                "," + task.getStartTime() +
                "\n";
    }

    public Integer getEpicId() {
        return null;
    }
}

