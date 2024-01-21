package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.HashMap;

public class TaskManager {

   private HashMap<Integer, Task> tasks;
   private HashMap<Integer, Epic>  epics;
   private HashMap<Integer, SubTask> subTasks;

   int id;

   public TaskManager(){
       this.tasks=new HashMap<>();
       this.epics=new HashMap<>();
       this.subTasks=new HashMap<>();
       id=0;
   }

}
