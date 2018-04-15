package org.lolobored.jira.services.worklog.store;

import lombok.Data;

@Data
public class WorklogKey {
  private final String rangeLabel;
  private final String component;

  public WorklogKey(String rangeLabel,
                    String component){
    this.rangeLabel= rangeLabel;
    this.component= component;
  }
}
