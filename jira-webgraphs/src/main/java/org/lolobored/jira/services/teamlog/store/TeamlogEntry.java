package org.lolobored.jira.services.teamlog.store;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamlogEntry {
  private TeamlogKey teamlogKey;
  private Integer totalTimeSpent;
  private List<String> jiraIssues= new ArrayList<>();

  public void addTime(int newTime){
    totalTimeSpent+=newTime;
  }

  public void addJiraIssue(String key){
    if (!jiraIssues.contains(key)){
      jiraIssues.add(key);
    }
  }
}
