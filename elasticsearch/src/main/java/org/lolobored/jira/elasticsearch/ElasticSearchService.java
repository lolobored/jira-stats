package org.lolobored.jira.elasticsearch;


import org.lolobored.jira.model.Issue;

import java.util.List;

public interface ElasticSearchService {

	public void insertIssue(Issue issue);

	public List<Issue> getAllIssues(int maximum);

	public Issue getIssue(String jiraKey);

}
