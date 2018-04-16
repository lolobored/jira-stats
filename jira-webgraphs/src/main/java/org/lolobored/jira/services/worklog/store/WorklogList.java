package org.lolobored.jira.services.worklog.store;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WorklogList {
  private Map<WorklogKey, WorklogRange> worklogRanges= new HashMap<>();

  public WorklogRange getRangeForEntry(String rangeLabel, String entry){
    WorklogKey key= new WorklogKey(rangeLabel, entry);
    WorklogRange result= worklogRanges.get(key);
    if (result == null) {
      result= new WorklogRange();
      result.setTotalTimeSpent(0);
      result.setWorklogKey(key);
      worklogRanges.put(key, result);
    }
    return result;
  }
}
