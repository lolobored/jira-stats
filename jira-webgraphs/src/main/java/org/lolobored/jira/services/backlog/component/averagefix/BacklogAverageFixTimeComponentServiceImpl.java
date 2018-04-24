package org.lolobored.jira.services.backlog.component.averagefix;

import org.apache.commons.lang.StringUtils;
import org.lolobored.jira.dao.data.DAOHeader;
import org.lolobored.jira.dao.data.DAORow;
import org.lolobored.jira.dao.data.DAOTable;
import org.lolobored.jira.dao.data.HeaderColumn;
import org.lolobored.jira.elasticsearch.ElasticSearchService;
import org.lolobored.jira.model.Component;
import org.lolobored.jira.model.Issue;
import org.lolobored.jira.model.Sprint;
import org.lolobored.jira.model.Worklog;
import org.lolobored.jira.ranges.Range;
import org.lolobored.jira.ranges.RangeUtil;
import org.lolobored.jira.services.backlog.component.store.BacklogList;
import org.lolobored.jira.services.backlog.component.store.BacklogRange;
import org.lolobored.jira.properties.JiraProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BacklogAverageFixTimeComponentServiceImpl implements BacklogAverageFixTimeComponentService {

	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	JiraProperties jiraProperties;

	@Override
	public DAOTable getAverageFixTimePerComponent() {
		String jiraSearchAppend = " jira search";
		DAOTable result = new DAOTable();
		BacklogList backlogList;
		// create the header
		// we'll use it automatically in the JSP / JS scripts
		DAOHeader daoHeader = new DAOHeader();

		daoHeader.addHeader(DAOHeader.STRING_TYPE, "Range Label");
		daoHeader.addHeader(DAOHeader.STRING_TYPE, "Range Type");
		daoHeader.addHeader(DAOHeader.STRING_TYPE, "Project");
		// DAO Header will be completed afterwards

		String projects = jiraProperties.getProject();
		String[] projectList = projects.split(";");

		// get months
		List<Range> commonRanges = new ArrayList();
		commonRanges.addAll(RangeUtil.getMonthlyRange());
		commonRanges.addAll(RangeUtil.getQuarterRange());


		for (String project : projectList) {
			List<Range> ranges = new ArrayList<>(commonRanges);
			List<Sprint> sprints = elasticSearchService.getAllSprintsPerProject(project, Integer.valueOf(jiraProperties.getMaximum()));
			ranges.addAll(RangeUtil.getSprintRange(sprints));

			for (Range range : ranges) {
				LocalDateTime startDate = range.getStartDate();
				LocalDateTime endDate = range.getEndDate();
				backlogList = new BacklogList();

				// retrieve all the issues
				// which were resolved during that period
				List<Issue> resolvedBugs = elasticSearchService.getBugsResolvedWithinPeriod(startDate, endDate, project, Integer.valueOf(jiraProperties.getMaximum()));

				for (Issue issue : resolvedBugs) {
					for (Component singleComponent : issue.getComponents()) {
						String component = singleComponent.getName();
						boolean found = false;
						// look for component column
						for (int i = 0; i < daoHeader.size(); i++) {
							HeaderColumn column = daoHeader.get(i);
							if (component.equals(column.getLabel())) {
								found = true;
								break;
							}
						}
						// if not found we have to add the header column
						if (!found) {
							daoHeader.addHeader(DAOHeader.NUMBER_TYPE, component);
							daoHeader.addHeader(DAOHeader.STRING_TYPE, component + jiraSearchAppend);
						}

						List<Worklog> worklogs = issue.getWorklogs();
						if (issue.getComponents().size() != 0) {
							for (Worklog worklog : worklogs) {

								// update the value in the stored keys
								BacklogRange backlogRange = backlogList.getRangeForEntry(range.getLabel(), component);
								// add time per component
								BigInteger nbComponents = BigInteger.valueOf(issue.getComponents().size());
								BigInteger timeSpent = worklog.getTimeSpentSeconds().divide(nbComponents);
								backlogRange.incrementCount(timeSpent.intValue());
								backlogRange.addJiraIssue(issue.getKey());
							}
						}
					}
				}

				DAORow newRow = new DAORow();
				newRow.put(daoHeader.get(0).getValue(), range.getLabel());
				newRow.put(daoHeader.get(1).getValue(), range.getType());
				newRow.put(daoHeader.get(2).getValue(), project);

				// add rows
				for (BacklogRange value : backlogList.getBacklogRanges().values()) {
					newRow.put(value.getBacklogKey().getEntry(), Integer.valueOf(value.getCount()).toString());
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
					newRow.put(value.getBacklogKey().getEntry() + jiraSearchAppend, jiraSearch.toString());

				}
				result.add(newRow);
			}
		}

		result.setHeader(daoHeader);
		// ensure everything is filled
		for (DAORow row : result) {
			for (HeaderColumn column : daoHeader) {

				if (DAOHeader.NUMBER_TYPE.equals(column.getType())) {
					if (row.get(column.getLabel()) == null) {
						row.put(column.getLabel(), "0");
					} else {
						Integer value = Integer.parseInt(row.get(column.getLabel()));
						int day = (int) TimeUnit.SECONDS.toDays(value);

						long hours = TimeUnit.SECONDS.toHours(value - (day * 24));
						// a working day is 8h not 24
						day = day * 3;
						hours = new Double((double) hours / 60 * 100).intValue();
						String time = String.format("%d.%s", day, StringUtils.leftPad(String.valueOf(hours), 2, "0"));
						row.put(column.getLabel(), time);
					}
				}

			}
		}
		return result;
	}

}
