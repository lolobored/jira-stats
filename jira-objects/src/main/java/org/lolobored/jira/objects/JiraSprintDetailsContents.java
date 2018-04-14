package org.lolobored.jira.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraSprintDetailsContents {

  private JiraSprintCompletedIssuesInitialEstimateSum completedIssuesInitialEstimateSum;
  private JiraSprintpuntedIssuesInitialEstimateSum puntedIssuesInitialEstimateSum;
  private JiraSprintCompletedIssuesEstimateSum completedIssuesEstimateSum;
  private JiraSprintIssuesCompletedInAnotherSprintEstimateSum issuesCompletedInAnotherSprintEstimateSum;

  private List<JiraIssue> completedIssues;
  private List<JiraIssue> issuesNotCompletedInCurrentSprint;
  private List<JiraIssue> puntedIssues;
  private List<JiraIssue> issuesCompletedInAnotherSprint;



}
