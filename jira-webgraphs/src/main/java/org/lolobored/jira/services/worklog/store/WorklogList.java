package org.lolobored.jira.services.worklog.store;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WorklogList {
  private Map<WorklogKey, WorklogRange> worklogRanges= new HashMap<>();

  public WorklogRange getRangeForComponent(String rangeLabel, String component){
    WorklogKey key= new WorklogKey(rangeLabel, component);
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
