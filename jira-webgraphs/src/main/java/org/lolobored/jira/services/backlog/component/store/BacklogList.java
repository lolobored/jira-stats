package org.lolobored.jira.services.backlog.component.store;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BacklogList {
  private Map<BacklogKey, BacklogRange> backlogRanges= new HashMap<>();

  public BacklogRange getRangeForEntry(String rangeLabel, String entry){
    BacklogKey key= new BacklogKey(rangeLabel, entry);
    BacklogRange result= backlogRanges.get(key);
    if (result == null) {
      result= new BacklogRange();
      result.setCount(0);
      result.setBacklogKey(key);
      backlogRanges.put(key, result);
    }
    return result;
  }
}
