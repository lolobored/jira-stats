package org.lolobored.jira.services.teamlog.store;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class TeamlogList {
  private Map<TeamlogKey, TeamlogEntry> teamlogEntries= new HashMap<>();

  public TeamlogEntry getTimeForEntry(String guy, LocalDate day){
    TeamlogKey key= new TeamlogKey(guy, day);
    TeamlogEntry result= teamlogEntries.get(key);
    if (result == null) {
      result= new TeamlogEntry();
      result.setTotalTimeSpent(0);
      result.setTeamlogKey(key);
      teamlogEntries.put(key, result);
    }
    return result;
  }
}
