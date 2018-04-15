package org.lolobored.jira.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SprintList {

  private List<Sprint> sprints = new ArrayList<>();

  private static SprintList instance = new SprintList();

  public static SprintList getInstance(){
    return instance;
  }

  public void addSprint(Sprint sprint){
    if(!sprints.contains(sprint)){
      sprints.add(sprint);
    }
  }

  public List<Sprint> getSortedList(){
    Collections.sort(sprints);
    return sprints;
  }
}
