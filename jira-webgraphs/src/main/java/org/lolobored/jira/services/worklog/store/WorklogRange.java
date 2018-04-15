package org.lolobored.jira.services.worklog.store;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WorklogRange {
  private WorklogKey worklogKey;
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
