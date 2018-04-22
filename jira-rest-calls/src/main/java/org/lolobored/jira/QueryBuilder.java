package org.lolobored.jira;

import org.lolobored.jira.objects.JiraIssueFields;

public class QueryBuilder {

  public static String getIssueList(String searchUrl, String project, int startAt,
                                    int maximum) {
    String space = "%20";
    String greaterOrEquals = "%3E%3D";
    StringBuilder jqlQuery = new StringBuilder(searchUrl);

    jqlQuery.append("project").append(space).append("in").append(space).append("(");

    jqlQuery.append(project).append(")");

    jqlQuery.append(space);
    jqlQuery.append("ORDER").append(space).append("BY").append(space).append("key").append(space)
      .append("ASC").append(space);
    jqlQuery.append("&startAt=").append(startAt);
    jqlQuery.append("&maxResults=").append(maximum);
    jqlQuery.append("&fields=").append(JiraIssueFields.getListOfFields());

    return jqlQuery.toString();
  }

  public static String getWorklog(String baseUrl, String jiraKey) {
    String space = "%20";
    String greaterOrEquals = "%3E%3D";
    StringBuilder jqlQuery = new StringBuilder(baseUrl);
    jqlQuery.append("/rest/api/2/issue/");
    jqlQuery.append(jiraKey);
    jqlQuery.append("/worklog");

    return jqlQuery.toString();
  }

  public static String getTimeEstimate(String baseUrl, String jiraKey) {
    String space = "%20";
    String greaterOrEquals = "%3E%3D";
    StringBuilder jqlQuery = new StringBuilder(baseUrl);
    jqlQuery.append("/rest/api/2/issue/");
    jqlQuery.append(jiraKey);
    jqlQuery.append("?fields=timetracking");

    return jqlQuery.toString();
  }

  public static String getProjectBoards(String searchUrl, int startAt,
                                        int maximum) {
    String space = "%20";
    String greaterOrEquals = "%3E%3D";
    StringBuilder jqlQuery = new StringBuilder(searchUrl);
    jqlQuery.append("?startAt=").append(startAt);
    jqlQuery.append("&maxResults=").append(maximum);

    return jqlQuery.toString();
  }

  public static String getJiraSprintsList(String boardSearchUrl, int startAt, int maximum) {

    StringBuilder jqlQuery =
      new StringBuilder(boardSearchUrl).append("/sprint?startAt=").append(startAt);
    jqlQuery.append("&maxResults=").append(maximum);

    return jqlQuery.toString();
  }

  public static String getSprintViewProjectList(String baseUrl) {
    String space = "%20";
    String greaterOrEquals = "%3E%3D";
    StringBuilder jqlQuery = new StringBuilder(baseUrl);
    jqlQuery.append("/rest/greenhopper/1.0/rapidview");

    return jqlQuery.toString();
  }

  public static String getSprintList(String baseUrl, String sprintViewId) {
    String space = "%20";
    String greaterOrEquals = "%3E%3D";
    StringBuilder jqlQuery = new StringBuilder(baseUrl);
    jqlQuery.append("/rest/greenhopper/latest/sprintquery/");
    jqlQuery.append(sprintViewId);
    jqlQuery.append("?includeHistoricSprints=true&includeFutureSprints=true");

    return jqlQuery.toString();
  }

  public static String getSprintDetails(String baseUrl, int sprintViewId, int sprintId) {

    String space = "%20";
    String greaterOrEquals = "%3E%3D";
    StringBuilder jqlQuery = new StringBuilder(baseUrl);
    jqlQuery.append("/rest/greenhopper/latest/rapid/charts/sprintreport?rapidViewId=");
    jqlQuery.append(sprintViewId);
    jqlQuery.append("&sprintId=");
    jqlQuery.append(sprintId);

    return jqlQuery.toString();
  }

}
