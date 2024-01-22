package model;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, Status status, int idEpic) {
        super(name, description, status);
        this.epicId = idEpic;
    }

    public int getEpicId() {
        return epicId;
    }
}
