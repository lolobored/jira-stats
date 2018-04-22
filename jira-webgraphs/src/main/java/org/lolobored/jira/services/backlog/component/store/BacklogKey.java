package org.lolobored.jira.services.backlog.component.store;

import lombok.Data;

@Data
public class BacklogKey {
  private final String rangeLabel;
  private final String entry;

  public BacklogKey(String rangeLabel,
                    String entry){
    this.rangeLabel= rangeLabel;
    this.entry= entry;
  }
}
