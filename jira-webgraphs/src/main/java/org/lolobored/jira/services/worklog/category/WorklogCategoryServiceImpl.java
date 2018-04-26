package org.lolobored.jira.services.worklog.category;

import org.apache.commons.lang.StringUtils;
import org.lolobored.jira.dao.data.DAOHeader;
import org.lolobored.jira.dao.data.DAORow;
import org.lolobored.jira.dao.data.DAOTable;
import org.lolobored.jira.elasticsearch.ElasticSearchService;
import org.lolobored.jira.model.Component;
import org.lolobored.jira.model.Issue;
import org.lolobored.jira.model.Sprint;
import org.lolobored.jira.model.Worklog;
import org.lolobored.jira.properties.JiraProperties;
import org.lolobored.jira.properties.ProjectMainLabelsProperties;
import org.lolobored.jira.ranges.Range;
import org.lolobored.jira.ranges.RangeUtil;
import org.lolobored.jira.services.worklog.store.WorklogList;
import org.lolobored.jira.services.worklog.store.WorklogRange;
import org.lolobored.jira.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class WorklogCategoryServiceImpl implements WorklogCategoryService {

  @Autowired
  ElasticSearchService elasticSearchService;

  @Autowired
  JiraProperties jiraProperties;

  @Autowired
  ProjectMainLabelsProperties projectMainLabelsProperties;

  @Override
  public DAOTable getSharedLoggedTimePerCategory() {
    DAOTable result = new DAOTable();
    WorklogList worklogList ;
    // create the header
    // we'll use it automatically in the JSP / JS scripts
    DAOHeader daoHeader = new DAOHeader();

    daoHeader.addHeader(DAOHeader.STRING_TYPE, "Range Label");
    daoHeader.addHeader(DAOHeader.STRING_TYPE, "Range Type");
    daoHeader.addHeader(DAOHeader.STRING_TYPE, "Project");
    daoHeader.addHeader(DAOHeader.STRING_TYPE, "Category");
    daoHeader.addHeader(DAOHeader.NUMBER_TYPE, "Time spent");
    daoHeader.addHeader(DAOHeader.STRING_TYPE, "Jira Search");

    result.setHeader(daoHeader);

    String projects = jiraProperties.getProject();
    String[] projectList = projects.split(";");

    // get months
    List<Range> commonRanges = RangeUtil.getMonthlyRange();
    commonRanges.addAll(RangeUtil.getQuarterRange());

    // here's the kind of results we want to get:
    // ['Range Label', 'Range Type', 'Component', 'Issue Type', 'Time spent', 'Jira Search'],
    // [ 'Shark 2', 'Sprints', 'ComponentName', 'Bug', 181020,
    // 'https://jira.us-bottomline.root.bottomline.com/issues/?jql=key%20in(GTFRM-4046,GTFRM-3995,GTFRM-3981,GTFRM-3939,GTFRM-3938,GTFRM-3807)&maxResults=500'],
    // [ 'April 2015', 'Monthly', 'ComponentName', 'Bug', 181020,
    // 'https://jira.us-bottomline.root.bottomline.com/issues/?jql=key%20in(GTFRM-4046,GTFRM-3995,GTFRM-3981,GTFRM-3939,GTFRM-3938,GTFRM-3807)&maxResults=500'],
    for (String project : projectList) {
			List<Range> ranges = new ArrayList<>(commonRanges);
			List<Sprint> sprints = elasticSearchService.getAllSprintsPerProject(project, Integer.valueOf(jiraProperties.getMaximum()));
			ranges.addAll(RangeUtil.getSprintRange(sprints));

			List<ProjectMainLabelsProperties.ProjectLabelsList.ProjectLabel> labelsPerProject = projectMainLabelsProperties.getLabelsPerProject(project);
			String mainCategory = "UNDEFINED";
			// get the default category
			for (ProjectMainLabelsProperties.ProjectLabelsList.ProjectLabel labelPerProject: labelsPerProject){
				if ("default".equalsIgnoreCase(labelPerProject.getLabelName())){
					mainCategory= labelPerProject.getCategoryName();

				}
			}

			for (Range range : ranges) {
      worklogList = new WorklogList();


        // retrieve the issues
        // that contains a worklog
        // in that range
        List<Issue> issues = elasticSearchService.getIssuesWithWorklogBetweenPeriod(range.getStartDate(), range.getEndDate(), project, 1000);

        for (Issue issue : issues) {
					String category = mainCategory;
          List<String> labels = issue.getLabels();
          boolean found = false;
					for (String label: labels){
						for (ProjectMainLabelsProperties.ProjectLabelsList.ProjectLabel labelPerProject: labelsPerProject){
							if (labelPerProject.getLabelName().equalsIgnoreCase(label)){
								found= true;
								category= labelPerProject.getCategoryName();
								break;
							}
						}
						if (found){
							break;
						}
					}
          for (Worklog worklog : issue.getWorklogs()) {

              if (worklog.getCreated().isAfter(range.getStartDate()) && worklog.getCreated().isBefore(range.getEndDate())) {
                BigInteger timeSpent = worklog.getTimeSpentSeconds();
                // browse components
								WorklogRange worklogRange = worklogList.getRangeForEntry(range.getLabel(), category);
                worklogRange.addTime(timeSpent.intValue());
                worklogRange.addJiraIssue(issue.getKey());

              }
            }
        }
        // add rows
        for (WorklogRange value : worklogList.getWorklogRanges().values()) {
          DAORow newRow = new DAORow();
          newRow.put(daoHeader.get(0).getValue(), range.getLabel());
          newRow.put(daoHeader.get(1).getValue(), range.getType());
          newRow.put(daoHeader.get(2).getValue(), project);
          newRow.put(daoHeader.get(3).getValue(), value.getWorklogKey().getEntry());

          String time = TimeUtils.returnTimeSpentPerDay(value.getTotalTimeSpent());
          newRow.put(daoHeader.get(4).getValue(), time);
          StringBuilder jiraSearch = new StringBuilder(jiraProperties.getBaseurl()).append("/issues/?jql=key%20in(");
          boolean start = true;
          for (String issueKey : value.getJiraIssues()) {
            if (!start) {
              jiraSearch.append(",");
            }
            jiraSearch.append(issueKey);
            start = false;

          }
          jiraSearch.append(")");
          newRow.put(daoHeader.get(5).getValue(), jiraSearch.toString());
          result.add(newRow);
        }

      }
    }

    return result;
  }



}
