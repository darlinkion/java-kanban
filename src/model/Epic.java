package model;

import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<SubTask> listSubTasks;

    public Epic(String name, String description, Status status, ArrayList<SubTask> listSubTasks) {
        super(name, description, status);
        this.listSubTasks = listSubTasks;
    }
}
