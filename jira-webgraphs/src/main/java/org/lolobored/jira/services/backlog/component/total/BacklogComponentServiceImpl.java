package org.lolobored.jira.services.backlog.component.total;

import org.lolobored.jira.dao.data.DAOHeader;
import org.lolobored.jira.dao.data.DAORow;
import org.lolobored.jira.dao.data.DAOTable;
import org.lolobored.jira.dao.data.HeaderColumn;
import org.lolobored.jira.elasticsearch.ElasticSearchService;
import org.lolobored.jira.model.Component;
import org.lolobored.jira.model.Issue;
import org.lolobored.jira.model.Sprint;
import org.lolobored.jira.ranges.Range;
import org.lolobored.jira.ranges.RangeUtil;
import org.lolobored.jira.services.backlog.component.store.BacklogList;
import org.lolobored.jira.services.backlog.component.store.BacklogRange;
import org.lolobored.jira.webgraphs.JiraProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BacklogComponentServiceImpl implements BacklogComponentService {

	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	JiraProperties jiraProperties;

	@Override
	public DAOTable getBacklogPerComponent() {
		String jiraSearchAppend= " jira search";
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
		List<List<Range>> commonRanges = new ArrayList();
		commonRanges.add(RangeUtil.getMonthlyRange());
		commonRanges.add(RangeUtil.getQuarterRange());


		for (String project : projectList) {
			List<List<Range>> rangesLists = new ArrayList<>(commonRanges);
			List<Sprint> sprints = elasticSearchService.getAllSprintsPerProject(project, Integer.valueOf(jiraProperties.getMaximum()));
			rangesLists.add(RangeUtil.getSprintRange(sprints));

			for (List<Range> rangesList : rangesLists) {
				if (rangesList.isEmpty()){
					continue;
				}
				// get the first item in the list
				LocalDateTime startDate = rangesList.get(0).getStartDate();
				// get opened bugs
				List<Issue> openBugs = elasticSearchService.getBugsOpenedBefore(startDate, project, Integer.valueOf(jiraProperties.getMaximum()));
				List<Issue> resolvedBugs = elasticSearchService.getBugsResolvedBefore(startDate, project, Integer.valueOf(jiraProperties.getMaximum()));

				// get remaining bugs at the start of the ranges
				openBugs.removeAll(resolvedBugs);

				removeStatusClosed(openBugs);

				for (Range range : rangesList) {
					startDate = range.getStartDate();
					LocalDateTime endDate = range.getEndDate();
					backlogList = new BacklogList();

					// retrieve all the issues
					// that contains a worklog
					// in that range
					List<Issue> openedBugs = elasticSearchService.getBugsOpenedWithinPeriod(startDate, endDate, project, Integer.valueOf(jiraProperties.getMaximum()));
					resolvedBugs = elasticSearchService.getBugsResolvedWithinPeriod(startDate, endDate, project, Integer.valueOf(jiraProperties.getMaximum()));

					// adding the new bugs
					openBugs.addAll(openedBugs);
					openBugs.removeAll(resolvedBugs);
					removeStatusClosed(openBugs);

					for (Issue issue : openBugs) {
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

							// update the value in the stored keys
							BacklogRange backlogRange = backlogList.getRangeForEntry(range.getLabel(), component);
							backlogRange.incrementCount(1);
							backlogRange.addJiraIssue(issue.getKey());

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
		}
		result.setHeader(daoHeader);
		// ensure everything is filled
		for (DAORow row: result){
			for (HeaderColumn column: daoHeader){

				if (DAOHeader.NUMBER_TYPE.equals(column.getType()) && row.get(column.getLabel()) == null){
					row.put(column.getLabel(), "0");
				}

			}
		}
		return result;
	}

	private void removeStatusClosed(List<Issue> openBugs) {
		List<Issue> toRemove= new ArrayList<>();
		for (Issue openBug: openBugs) {
			if (Issue.ClosedNotImplementedStatus.equals(openBug.getStatus())){
				toRemove.add(openBug);
			}
		}
		openBugs.removeAll(toRemove);
	}


}
