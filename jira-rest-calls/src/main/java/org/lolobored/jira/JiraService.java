package org.lolobored.jira;


import org.lolobored.jira.http.HttpException;
import org.lolobored.jira.objects.*;

import java.io.IOException;
import java.util.List;

public interface JiraService {

  public List<JiraIssue> getAllIssues(String baseUrl, String project, String username, String password, int maximumPerSearch) throws IOException, HttpException;

  public JiraBoard getProjectBoard(String baseUrl, String boardName, String username, String password, int maximumPerSearch) throws ProcessException, IOException, HttpException;

  public List<JiraSprint> getAllSprints(JiraBoard board, String username, String password, int maximumPerSearch) throws IOException, HttpException;

  public JiraSprintView getProjectSprintView(String baseUrl, String boardName, String username, String password) throws ProcessException, IOException, HttpException;

  public JiraSprintDetails getSprintDetails(String baseUrl, int sprintViewId, int sprintId, String username, String password) throws IOException, HttpException;

  public JiraTimeTracking getTimetracker(String baseUrl, JiraIssue jiraIssue, String username, String password) throws IOException, HttpException;

  public List<JiraWorklog> getWorklog(String baseUrl, JiraIssue jiraIssue, String username, String password) throws IOException, HttpException;
}
