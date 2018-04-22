package org.lolobored.jira.elasticsearch;


import org.lolobored.jira.http.HttpException;
import org.lolobored.jira.model.Issue;
import org.lolobored.jira.model.Sprint;

import java.time.LocalDateTime;
import java.util.List;

public interface ElasticSearchService {

	public void setMaxResultWindow(String url, Integer maxResult) throws HttpException;

	public void insertIssue(Issue issue);

	public List<Issue> getAllIssuesPerProject(String project, int maximum);

	public Issue getIssue(String jiraKey);

	public List<Issue> getIssuesWithWorklogBetweenPeriod(LocalDateTime startDate, LocalDateTime endDate, String project, int maximum);

	public List<Issue> getBugsOpenedWithinPeriod(LocalDateTime startDate, LocalDateTime endDate, String project, int maximum);

	public List<Issue> getBugsResolvedWithinPeriod(LocalDateTime startDate, LocalDateTime endDate, String project, int maximum);

	public List<Issue> getBugsOpenedBefore(LocalDateTime startDate, String project, int maximum);

	public List<Issue> getBugsResolvedBefore(LocalDateTime startDate, String project, int maximum);

	public void insertSprint(Sprint sprint);

  public List<Sprint> getAllSprintsPerProject(String project, int maximum);

}
