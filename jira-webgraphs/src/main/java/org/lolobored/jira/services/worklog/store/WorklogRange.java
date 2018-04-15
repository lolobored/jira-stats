package org.lolobored.jira.services.worklog.store;

import lombok.Data;

@Data
public class WorklogRange {
  private WorklogKey worklogKey;
  private Integer totalTimeSpent;

  public void addTime(int newTime){
    totalTimeSpent+=newTime;
  }
}
