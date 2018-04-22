package org.lolobored.jira.batch;


import org.lolobored.jira.ProcessException;
import org.lolobored.jira.elasticsearch.ElasticSearchService;
import org.lolobored.jira.JiraService;
import org.lolobored.jira.http.HttpException;
import org.lolobored.jira.model.Issue;
import org.lolobored.jira.model.Sprint;
import org.lolobored.jira.model.Worklog;
import org.lolobored.jira.objects.*;
import org.lolobored.jira.webgraphs.ApplicationProperties;
import org.lolobored.jira.webgraphs.JiraProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class JiraFetcher {

	private static final Logger log = LoggerFactory.getLogger(JiraFetcher.class);


	@Autowired
  JiraProperties jiraProperties;
  @Autowired
  ApplicationProperties applicationProperties;

  @Autowired
  JiraService jiraService;

  @Autowired
  ElasticSearchService elasticSearchService;

  // every 15 minutes: 900000
  // every 3h: 10800000
  @Scheduled(fixedDelay = 3600000)
	public void loadJira() throws IOException, HttpException, ProcessException {

    String projects = jiraProperties.getProject();
    String[] projectList = projects.split(";");
    String boards = jiraProperties.getBoard();
    String[] boardList = boards.split(";");
    int totalJiraIssues=0;

    for (int i=0; i< projectList.length; i++) {

      String project = projectList[i];
      String board= boardList[i];

      List<JiraIssue> jiraIssues = jiraService.getAllIssues(jiraProperties.getBaseurl(),
        project,
        jiraProperties.getUsername(),
        jiraProperties.getPassword(),
        Integer.parseInt(jiraProperties.getMaximum()));

      totalJiraIssues= jiraIssues.size();

      // insert issues in elasticsearch
      for (JiraIssue jiraIssue : jiraIssues) {
        Issue issue = new Issue();
        LocalDateTime lastUpdate = jiraIssue.getFields().getUpdated().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Issue currentIssue = elasticSearchService.getIssue(jiraIssue.getKey());
        // do not look for details if they are already up to date
        if (currentIssue== null || currentIssue.getUpdated()== null
          || !lastUpdate.isEqual(currentIssue.getUpdated())) {
          List<org.lolobored.jira.model.Component> components = new ArrayList<>();
          for (JiraIssueComponent jiraComponent : jiraIssue.getFields().getComponents()) {
            org.lolobored.jira.model.Component component = new org.lolobored.jira.model.Component();
            component.setName(jiraComponent.getName());
            components.add(component);
          }
          issue.setComponents(components);
          issue.setLabels(jiraIssue.getFields().getLabels());
          issue.setIssueType(jiraIssue.getFields().getIssuetype().getName());
          issue.setKey(jiraIssue.getKey());
          issue.setStatus(jiraIssue.getFields().getStatus().getName());
          issue.setProject(project);
          issue.setEpicIssue(jiraIssue.getFields().getCustomfield_10940());
          issue.setTitle(jiraIssue.getFields().getSummary());
          issue.setCreated(jiraIssue.getFields().getCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
          if (jiraIssue.getFields().getResolutiondate()!= null) {
            issue.setResolved(jiraIssue.getFields().getResolutiondate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
          }
          issue.setUpdated(lastUpdate);

          // check estimate
          JiraTimeTracking jiraTimeTracking = jiraService.getTimetracker(jiraProperties.getBaseurl(),
            jiraIssue,
            jiraProperties.getUsername(),
            jiraProperties.getPassword());

          issue.setOriginalEstimateSeconds(jiraTimeTracking.getOriginalEstimateSeconds());

          // check worklog
          List<JiraWorklog> jiraWorklogs = jiraService.getWorklog(jiraProperties.getBaseurl(),
            jiraIssue,
            jiraProperties.getUsername(),
            jiraProperties.getPassword());

          for (JiraWorklog jiraWorklog : jiraWorklogs) {
            Worklog worklog = new Worklog();
            worklog.setTimeSpentSeconds(jiraWorklog.getTimeSpentSeconds());
            worklog.setCreated(jiraWorklog.getUpdated().toInstant().atZone(ZoneId.systemDefault())
              .toLocalDateTime());
            worklog.setAuthor(jiraWorklog.getAuthor().getDisplayName());
            issue.getWorklogs().add(worklog);
          }

          // add issue to elastic search
          elasticSearchService.insertIssue(issue);
        }
      }

      // set the max result for elastic search (quick workaround)
      elasticSearchService.setMaxResultWindow(applicationProperties.getUri(), Integer.valueOf(applicationProperties.getMaxindexsearch()));

      // now let's replace component by it's name
      List<Issue> issues = elasticSearchService.getAllIssuesPerProject(project, Integer.parseInt(jiraProperties.getMaximum()));
      for (Issue issue : issues) {

        if (issue.getEpicIssue() != null) {
          Issue epicIssue = elasticSearchService.getIssue(issue.getEpicIssue());
          if (epicIssue != null) {
            // replace component by its title
            issue.setEpicTitle(epicIssue.getTitle());
            // update issue in elastic search
            elasticSearchService.insertIssue(issue);
          }
        }
      }

      JiraBoard jiraBoard = jiraService.getProjectBoard(jiraProperties.getBaseurl(),
        board,
        jiraProperties.getUsername(),
        jiraProperties.getPassword(),
        50);

      List<JiraSprint> jiraSprints = jiraService.getAllSprints(jiraBoard,
        jiraProperties.getUsername(),
        jiraProperties.getPassword(),
        Integer.parseInt(jiraProperties.getMaximum()));

      JiraSprintView jiraSprintView = jiraService.getProjectSprintView(jiraProperties.getBaseurl(),
        board,
        jiraProperties.getUsername(),
        jiraProperties.getPassword());

      for (JiraSprint singleSprint : jiraSprints) {
        JiraSprintDetails sprintDetail = jiraService.getSprintDetails(jiraProperties.getBaseurl(),
          jiraSprintView.getId(),
          singleSprint.getId(),
          jiraProperties.getUsername(),
          jiraProperties.getPassword());

        if (jiraBoard.getId().equals(singleSprint.getOriginBoardId())) {

          Sprint currentSprint = new Sprint();
          if (singleSprint.getEndDate() != null) {
            currentSprint.setEndDate(singleSprint.getEndDate().toInstant().atZone(ZoneId.systemDefault())
              .toLocalDateTime());
          }
          if (singleSprint.getStartDate() != null) {
            currentSprint.setStartDate(singleSprint.getStartDate().toInstant().atZone(ZoneId.systemDefault())
              .toLocalDateTime());
          }
          currentSprint.setId(singleSprint.getId());
          currentSprint.setName(singleSprint.getName());
          currentSprint.setProject(project);

          elasticSearchService.insertSprint(currentSprint);

          for (JiraIssue completedIssue : sprintDetail.getContents().getCompletedIssues()) {
            Issue issue = elasticSearchService.getIssue(completedIssue.getKey());
            issue.getSprints().add(currentSprint);
            issue.setEndSprint(currentSprint);
            elasticSearchService.insertIssue(issue);
          }
          for (JiraIssue removedIssue : sprintDetail.getContents().getPuntedIssues()) {
            Issue issue = elasticSearchService.getIssue(removedIssue.getKey());
            issue.getSprints().add(currentSprint);
            elasticSearchService.insertIssue(issue);

          }
          for (JiraIssue notCompleted : sprintDetail.getContents().getIssuesNotCompletedInCurrentSprint()) {
            Issue issue = elasticSearchService.getIssue(notCompleted.getKey());
            issue.getSprints().add(currentSprint);
            elasticSearchService.insertIssue(issue);
          }

        }
      }
    }
	}

}
