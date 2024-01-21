package model;

public class SubTask extends Task {

    private int idEpic;

    public SubTask(String name, String description, Status status, int idEpic) {
        super(name, description, status);
        this.idEpic = idEpic;
    }

    public int getIdEpic() {
        return idEpic;
    }
}
