package org.lolobored.jira;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.codec.binary.Base64;
import org.lolobored.jira.http.HttpException;
import org.lolobored.jira.http.HttpUtil;
import org.lolobored.jira.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JiraServiceImpl implements JiraService {

  private static Logger logger = LoggerFactory.getLogger(JiraServiceImpl.class);

  private static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
    .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  @Override
  public List<JiraIssue> getAllIssues(String baseUrl, String project, String username, String password, int maximumPerSearch) throws IOException, HttpException {
    String searchUrl = baseUrl + "/rest/api/2/search?jql=";
    List<JiraIssue> completeList = new ArrayList<>();

    int startAt = 0;
    boolean fetching = true;

    // authentication
    Map<String, String> headers = getJiraHeader(username, password);

    HttpUtil httpUtil = HttpUtil.getInstance(false);

    while (fetching) {
      // building query that will fetch every issues in standard types
      String jqlRequest =
        QueryBuilder.getIssueList(searchUrl, project, startAt, maximumPerSearch);

      // fetch next part
      startAt += maximumPerSearch;
      // retrieve the issue list on which we need to work
      String response = httpUtil.get(jqlRequest, headers);

      JiraIssueList jiraIssueListReturned = mapper.readValue(response, JiraIssueList.class);
      if (jiraIssueListReturned.getIssues().isEmpty()) {
        fetching = false;
      } else {
        completeList.addAll(jiraIssueListReturned.getIssues());
        if (jiraIssueListReturned.getIssues().size() < maximumPerSearch) {
          fetching = false;
        }
      }
    }
    return completeList;
  }

  @Override
  public List<JiraWorklog> getWorklog(String baseUrl, JiraIssue jiraIssue, String username, String password) throws IOException, HttpException {
    List<JiraIssue> completeList = new ArrayList<>();

    // authentication
    Map<String, String> headers = getJiraHeader(username, password);

    HttpUtil httpUtil = HttpUtil.getInstance(false);

    String jqlRequest =
      QueryBuilder.getWorklog(baseUrl, jiraIssue.getKey());

    // retrieve the worklog list
    String response = httpUtil.get(jqlRequest, headers);

    JiraWorklogs jiraWorklogs = mapper.readValue(response, JiraWorklogs.class);

    return jiraWorklogs.getWorklogs();
  }

  @Override
  public JiraTimeTracking getTimetracker(String baseUrl, JiraIssue jiraIssue, String username, String password) throws IOException, HttpException {

    // authentication
    Map<String, String> headers = getJiraHeader(username, password);

    HttpUtil httpUtil = HttpUtil.getInstance(false);

    String jqlRequest =
      QueryBuilder.getTimeEstimate(baseUrl, jiraIssue.getKey());

    // retrieve the worklog list
    String response = httpUtil.get(jqlRequest, headers);

    JiraTimeEstimates jiraTimeEstimates = mapper.readValue(response, JiraTimeEstimates.class);

    return jiraTimeEstimates.getFields().getTimetracking();
  }

  @Override
  public JiraBoard getProjectBoard(String baseUrl, String boardName, String username, String password, int maximumPerSearch) throws ProcessException, IOException, HttpException{
    String searchUrl = baseUrl + "/rest/agile/1.0/board";
    List<JiraSprint> completeList = new ArrayList<>();

    int startAt = 0;
    boolean fetching = true;



    HttpUtil httpUtil = HttpUtil.getInstance(false);
    Map<String, String> headers = getJiraHeader(username, password);

    while (fetching) {
      // building query that will fetch every issues in standard types
      String jqlRequest =
        QueryBuilder.getProjectBoards(searchUrl, startAt, maximumPerSearch);

      // fetch next part
      startAt += maximumPerSearch;
      // retrieve the issue list on which we need to work
      String response = httpUtil.get(jqlRequest, headers);

      JiraBoardList jiraBoarList = mapper.readValue(response, JiraBoardList.class);
      for (JiraBoard jiraBoard : jiraBoarList.getValues()) {
        if (jiraBoard.getName().equals(boardName)) {
          return jiraBoard;
        }
      }
      if (jiraBoarList.getValues().isEmpty()) {
        fetching = false;
      } else if (jiraBoarList.getValues().size() < maximumPerSearch) {
          fetching = false;
        }
      }
    throw new ProcessException(String.format("Unable to find any board for project [%s]", boardName));
  }

  @Override
  public List<JiraSprint> getAllSprints(JiraBoard board, String username, String password, int maximumPerSearch) throws IOException, HttpException {
    List<JiraSprint> completeList = new ArrayList<>();

    int startAt = 0;
    boolean fetching = true;

    // authentication
    Map<String, String> headers = getJiraHeader(username, password);

    HttpUtil httpUtil = HttpUtil.getInstance(false);

    while (fetching) {
      // building query that will fetch every issues in standard types
      String jqlRequest =
        QueryBuilder.getJiraSprintsList(board.getSelf(), startAt, maximumPerSearch);

      // fetch next part
      startAt += maximumPerSearch;
      // retrieve the issue list on which we need to work
      String response = httpUtil.get(jqlRequest, headers);

      JiraSprintList jiraSprintList = mapper.readValue(response, JiraSprintList.class);
      if (jiraSprintList.getValues().isEmpty()) {
        fetching = false;
      } else {
        completeList.addAll(jiraSprintList.getValues());
        if (jiraSprintList.getValues().size() < maximumPerSearch) {
          fetching = false;
        }
      }
    }
    return completeList;

  }

  @Override
  public JiraSprintView getProjectSprintView(String baseUrl, String boardName, String username, String password) throws ProcessException, IOException, HttpException{

    HttpUtil httpUtil = HttpUtil.getInstance(false);
    Map<String, String> headers = getJiraHeader(username, password);

      // building query that will fetch every issues in standard types
      String jqlRequest =
        QueryBuilder.getSprintViewProjectList(baseUrl);

      // retrieve the issue list on which we need to work
      String response = httpUtil.get(jqlRequest, headers);

      JiraSprintViewList jiraSprintViewList = mapper.readValue(response, JiraSprintViewList.class);
      for (JiraSprintView jiraSprintView : jiraSprintViewList.getViews()) {
        if (jiraSprintView.getName().equals(boardName)) {
          return jiraSprintView;
        }
      }

    throw new ProcessException(String.format("Unable to find any sprint views for project [%s]", boardName));
  }

  @Override
  public JiraSprintDetails getSprintDetails(String baseUrl, int sprintViewId, int sprintId, String username, String password) throws IOException, HttpException {

    // authentication
    Map<String, String> headers = getJiraHeader(username, password);

    HttpUtil httpUtil = HttpUtil.getInstance(false);

     // building query that will fetch every issues in standard types
      String jqlRequest =
        QueryBuilder.getSprintDetails(baseUrl, sprintViewId, sprintId);

      // retrieve the issue list on which we need to work
      String response = httpUtil.get(jqlRequest, headers);

    JiraSprintDetails jiraSprintDetails = mapper.readValue(response, JiraSprintDetails.class);

    return jiraSprintDetails;

  }

  private Map<String, String> getJiraHeader(String username, String password){
    // authentication
    String plainCreds = username + ":" + password;
    byte[] plainCredsBytes = plainCreds.getBytes();
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    String base64Creds = new String(base64CredsBytes);

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json;charset=UTF-8");
    headers.put("Authorization", "Basic " + base64Creds);
    return headers;
  }
}
