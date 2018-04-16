package org.lolobored.jira.services.worklog.store;

import lombok.Data;

@Data
public class WorklogKey {
  private final String rangeLabel;
  private final String entry;

  public WorklogKey(String rangeLabel,
                    String entry){
    this.rangeLabel= rangeLabel;
    this.entry= entry;
  }
}
