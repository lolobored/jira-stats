package org.lolobored.jira.services.teamlog.store;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TeamlogKey {
	private final String guy;
	private final LocalDate day;

	public TeamlogKey(String guy, LocalDate day){
    this.day= day;
    this.guy= guy;
  }
}
