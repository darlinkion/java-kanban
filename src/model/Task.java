package model;

public class Task {
    protected String name;
    protected String description;
    protected Integer id = null;
    protected Status status;

    public Task() {
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id == null) {
            this.id = id;
        }
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
                ", name='" + name + '\'' +
                ",description='" + description + '\'' +
                ",status='" + status + '\'' +
                "}\n";
    }


    public String toString(Task task) {
        return task.getId() +
                "," + task.getTaskType() +
                "," + task.getName() +
                "," + task.getStatus() +
                "," + task.getDescription() +
                "," + task.getEpicId() +
                "\n";
    }

    public Integer getEpicId() {
        return null;
    }
}

