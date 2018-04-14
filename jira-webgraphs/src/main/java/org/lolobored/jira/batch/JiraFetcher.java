package org.lolobored.jira.batch;


import org.lolobored.jira.ProcessException;
import org.lolobored.jira.elasticsearch.ElasticSearchService;
import org.lolobored.jira.JiraService;
import org.lolobored.jira.http.HttpException;
import org.lolobored.jira.model.Issue;
import org.lolobored.jira.model.Sprint;
import org.lolobored.jira.model.Worklog;
import org.lolobored.jira.objects.*;
import org.lolobored.jira.webgraphs.JiraProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class JiraFetcher {

	private static final Logger log = LoggerFactory.getLogger(JiraFetcher.class);


	@Autowired
  JiraProperties jiraProperties;

  @Autowired
  JiraService jiraService;

  @Autowired
  ElasticSearchService elasticSearchService;

  // every 15 minutes
  @Scheduled(fixedDelay = 900000)
	public void loadJira() throws IOException, HttpException, ProcessException {
    List<Issue> toBeLoaded = new ArrayList<>();
		List<JiraIssue> jiraIssues = jiraService.getAllIssues(jiraProperties.getBaseurl(),
      jiraProperties.getProject(),
      jiraProperties.getUsername(),
      jiraProperties.getPassword(),
      Integer.parseInt(jiraProperties.getMaximum()));

		// insert issues in elasticsearch
    for (JiraIssue jiraIssue : jiraIssues){
      Issue issue= new Issue();
      List<org.lolobored.jira.model.Component> components= new ArrayList<>();
      for (JiraIssueComponent jiraComponent: jiraIssue.getFields().getComponents()){
        org.lolobored.jira.model.Component component = new org.lolobored.jira.model.Component();
        component.setName(jiraComponent.getName());
        components.add(component);
      }
      issue.setComponents(components);
      issue.setIssueType(jiraIssue.getFields().getIssuetype().getName());
      issue.setKey(jiraIssue.getKey());
      issue.setStatus(jiraIssue.getFields().getStatus().getName());

      // check estimate
      JiraTimeTracking jiraTimeTracking= jiraService.getTimetracker(jiraProperties.getBaseurl(),
        jiraIssue,
        jiraProperties.getUsername(),
        jiraProperties.getPassword());

      issue.setOriginalEstimateSeconds(jiraTimeTracking.getOriginalEstimateSeconds());

      // check worklog
      List<JiraWorklog> jiraWorklogs = jiraService.getWorklog(jiraProperties.getBaseurl(),
        jiraIssue,
        jiraProperties.getUsername(),
        jiraProperties.getPassword());

      for (JiraWorklog jiraWorklog: jiraWorklogs){
        Worklog worklog = new Worklog();
        worklog.setTimeSpentSeconds(jiraWorklog.getTimeSpentSeconds());
        worklog.setUpdated(jiraWorklog.getUpdated().toInstant().atZone(ZoneId.systemDefault())
          .toLocalDateTime());
        issue.getWorklogs().add(worklog);
      }

      // add issue to elastic search
      elasticSearchService.insertIssue(issue);
    }

    JiraBoard board = jiraService.getProjectBoard(jiraProperties.getBaseurl(),
      jiraProperties.getBoard(),
      jiraProperties.getUsername(),
      jiraProperties.getPassword(),
      50);

    List<JiraSprint> jiraSprints = jiraService.getAllSprints(board,
      jiraProperties.getUsername(),
      jiraProperties.getPassword(),
      Integer.parseInt(jiraProperties.getMaximum()));

    JiraSprintView jiraSprintView = jiraService.getProjectSprintView(jiraProperties.getBaseurl(),
      jiraProperties.getBoard(),
      jiraProperties.getUsername(),
      jiraProperties.getPassword());

    for (JiraSprint singleSprint: jiraSprints){
      JiraSprintDetails sprintDetail = jiraService.getSprintDetails(jiraProperties.getBaseurl(),
        jiraSprintView.getId(),
        singleSprint.getId(),
        jiraProperties.getUsername(),
        jiraProperties.getPassword());

      Sprint currentSprint = new Sprint();
      currentSprint.setEndDate(singleSprint.getEndDate().toInstant().atZone(ZoneId.systemDefault())
        .toLocalDateTime());
      currentSprint.setStartDate(singleSprint.getStartDate().toInstant().atZone(ZoneId.systemDefault())
        .toLocalDateTime());
      currentSprint.setId(singleSprint.getId());
      currentSprint.setName(singleSprint.getName());
      for (JiraIssue completedIssue: sprintDetail.getContents().getCompletedIssues()){
        Issue issue= elasticSearchService.getIssue(completedIssue.getKey());
        issue.getSprints().add(currentSprint);
        issue.setEndSprint(currentSprint);
        elasticSearchService.insertIssue(issue);
      }
      for (JiraIssue removedIssue: sprintDetail.getContents().getPuntedIssues()){
        Issue issue= elasticSearchService.getIssue(removedIssue.getKey());
        issue.getSprints().add(currentSprint);
        elasticSearchService.insertIssue(issue);

      }
      for (JiraIssue notCompleted: sprintDetail.getContents().getIssuesNotCompletedInCurrentSprint()){
        Issue issue= elasticSearchService.getIssue(notCompleted.getKey());
        issue.getSprints().add(currentSprint);
        elasticSearchService.insertIssue(issue);
      }

    }





		int i=0;
		/*for (JiraIssue jiraIssue: jiraIssues){
      elasticSearchService.insertIssue(jiraIssue);
    }*/
	}

}
