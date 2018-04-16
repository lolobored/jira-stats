package org.lolobored.jira.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraIssueFields {

  private String summary;

  private JiraIssueType issuetype;
  private JiraProject project;
  private JiraIssueStatus status;
  private List<JiraIssueComponent> components;
  // the sprints to which the issue belong
  private List<String> customfield_10540;
  private Date created;
  private Date resolutiondate;
  private JiraResolution resolution;
  private JiraIssuePriority priority;

  private JiraAssignee assignee;
  private JiraReporter reporter;

  private JiraParent parent;
  private List<String> labels;
  private String description;
  // the related Epic key
  private String customfield_10940;


}
