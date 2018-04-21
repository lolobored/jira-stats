package org.lolobored.jira.elasticsearch;


import org.lolobored.jira.model.Issue;
import org.lolobored.jira.model.Sprint;

import java.time.LocalDateTime;
import java.util.List;

public interface ElasticSearchService {

	public void insertIssue(Issue issue);

	public List<Issue> getAllIssuesPerProject(String project, int maximum);

	public Issue getIssue(String jiraKey);

	public List<Issue> getIssuesWithWorklogBetweenPeriod(LocalDateTime startDate, LocalDateTime endDate, String project, int maximum);

	public void insertSprint(Sprint sprint);

  public List<Sprint> getAllSprintsPerProject(String project, int maximum);

}
