package org.lolobored.jira.services.teamlog;

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
import org.lolobored.jira.ranges.Range;
import org.lolobored.jira.ranges.RangeUtil;
import org.lolobored.jira.services.teamlog.store.TeamlogEntry;
import org.lolobored.jira.services.teamlog.store.TeamlogList;
import org.lolobored.jira.services.worklog.store.WorklogList;
import org.lolobored.jira.services.worklog.store.WorklogRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@Service
public class TeamlogServiceImpl implements TeamlogService {

	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	JiraProperties jiraProperties;

	@Override
	public DAOTable getLoggedTimePerTeamMember() {
		DAOTable daoTable = new DAOTable();
		TeamlogList teamlogList = new TeamlogList();
		// create the header
		// we'll use it automatically in the JSP / JS scripts
		DAOHeader daoHeader = new DAOHeader();

		daoHeader.addHeader(DAOHeader.DATE_TYPE, "Date");
		daoHeader.addHeader(DAOHeader.NUMBER_TYPE, "Time Logged");
		daoHeader.addHeader(DAOHeader.STRING_TYPE, "Resource");
		daoHeader.addHeader(DAOHeader.STRING_TYPE, "Project");
		daoHeader.addHeader(DAOHeader.STRING_TYPE, "Jira Search");
		daoTable.setHeader(daoHeader);

		String projects = jiraProperties.getProject();
		String[] projectList = projects.split(";");

		// get all day in current year and last year
		LocalDateTime now = LocalDateTime.now();
		// add 1 day to get today's information as well
		now= now.plusDays(1);

		for (String project : projectList) {
			teamlogList= new TeamlogList();
			LocalDateTime currentDayTime = LocalDateTime.of(now.getYear() - 1, 1, 1, 0, 0, 0);
			while (!currentDayTime.toLocalDate().isEqual(now.toLocalDate())) {


				LocalDateTime endDayTime = LocalDateTime.of(currentDayTime.getYear(), currentDayTime.getMonthValue(),
					currentDayTime.getDayOfMonth(), 23, 59, 59);
				// retrieve the issues
				// that contains a worklog
				// for that day
				List<Issue> issues = elasticSearchService.getIssuesWithWorklogBetweenPeriod(currentDayTime, endDayTime,
					project, Integer.parseInt(jiraProperties.getMaximum()));

				for (Issue issue : issues) {
					List<Worklog> worklogs = issue.getWorklogs();
					for (Worklog worklog : worklogs) {
						if (worklog.getCreated().isAfter(currentDayTime) && worklog.getCreated().isBefore(endDayTime)) {
							String guy = worklog.getAuthor();
							TeamlogEntry teamlogEntry = teamlogList.getTimeForEntry(guy, currentDayTime.toLocalDate());
							teamlogEntry.addTime(worklog.getTimeSpentSeconds().intValue());
							teamlogEntry.addJiraIssue(issue.getKey());
						}
					}
				}

				currentDayTime = currentDayTime.plusDays(1);
			}


			// add rows
			for (TeamlogEntry value : teamlogList.getTeamlogEntries().values()) {
				DAORow newRow = new DAORow();
				LocalDate day = value.getTeamlogKey().getDay();
				// trick with google date starting at 0
				String date = String.format("Date(%d,%d,%d)", day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth());
				newRow.put(daoHeader.get(0).getValue(), date);
				int timeSpentInSec = value.getTotalTimeSpent();
				int timeSpentInHours = timeSpentInSec / 3600;
				// max it at 8 to get the graph right
				if (timeSpentInHours > 8) {
					timeSpentInHours = 8;
				}
				newRow.put(daoHeader.get(1).getValue(), Integer.toString(timeSpentInHours));
				newRow.put(daoHeader.get(2).getValue(), value.getTeamlogKey().getGuy());
				newRow.put(daoHeader.get(3).getValue(), project);
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
				newRow.put(daoHeader.get(4).getValue(), jiraSearch.toString());
				daoTable.add(newRow);
			}
		}
		return daoTable;
	}
}