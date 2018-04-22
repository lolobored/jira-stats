package org.lolobored.jira.services.backlog.component.store;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BacklogRange {
  private BacklogKey backlogKey;
  private Integer count;
  private List<String> jiraIssues= new ArrayList<>();

  public void addCount(){
    count+=1;
  }

  public void addJiraIssue(String key){
    if (!jiraIssues.contains(key)){
      jiraIssues.add(key);
    }
  }
}
