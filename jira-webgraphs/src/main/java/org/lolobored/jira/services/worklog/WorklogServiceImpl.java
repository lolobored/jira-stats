package org.lolobored.jira.services.worklog;

import org.lolobored.jira.dao.data.DAOHeader;
import org.lolobored.jira.dao.data.DAORow;
import org.lolobored.jira.dao.data.DAOTable;
import org.lolobored.jira.elasticsearch.ElasticSearchService;
import org.lolobored.jira.model.Component;
import org.lolobored.jira.model.Issue;
import org.lolobored.jira.model.Worklog;
import org.lolobored.jira.ranges.Range;
import org.lolobored.jira.ranges.RangeUtil;
import org.lolobored.jira.services.worklog.store.WorklogKey;
import org.lolobored.jira.services.worklog.store.WorklogList;
import org.lolobored.jira.services.worklog.store.WorklogRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
public class WorklogServiceImpl implements WorklogService {

  @Autowired
  ElasticSearchService elasticSearchService;

  @Override
  public DAOTable getSharedLoggedTimePerComponent() {
    DAOTable result = new DAOTable();
    WorklogList worklogList = new WorklogList();
    // time spent per standard grouping
    Map<String, Integer> timeSpentPerStandardGrouping;
    Map<String, List<Issue>> parentIssuesPerStandardGrouping;
    // create the header
    // we'll use it automatically in the JSP / JS scripts
    DAOHeader daoHeader = new DAOHeader();

    daoHeader.addHeader(DAOHeader.STRING_TYPE, "Range Label");
    daoHeader.addHeader(DAOHeader.STRING_TYPE, "Range Type");
    daoHeader.addHeader(DAOHeader.STRING_TYPE, "Component");
    daoHeader.addHeader(DAOHeader.NUMBER_TYPE, "Time spent");
    daoHeader.addHeader(DAOHeader.STRING_TYPE, "Jira Search");

    result.setHeader(daoHeader);

    // get months
    List<Range> ranges = RangeUtil.getMonthlyRange();

    // here's the kind of results we want to get:
    // ['Range Label', 'Range Type', 'Component', 'Issue Type', 'Time spent', 'Jira Search'],
    // [ 'Shark 2', 'Sprints', 'ComponentName', 'Bug', 181020,
    // 'https://jira.us-bottomline.root.bottomline.com/issues/?jql=key%20in(GTFRM-4046,GTFRM-3995,GTFRM-3981,GTFRM-3939,GTFRM-3938,GTFRM-3807)&maxResults=500'],
    // [ 'April 2015', 'Monthly', 'ComponentName', 'Bug', 181020,
    // 'https://jira.us-bottomline.root.bottomline.com/issues/?jql=key%20in(GTFRM-4046,GTFRM-3995,GTFRM-3981,GTFRM-3939,GTFRM-3938,GTFRM-3807)&maxResults=500'],
    for (Range range : ranges) {
      worklogList = new WorklogList();
      // retrieve the issues
      // that contains a worklog
      // in that range
      List<Issue> issues = elasticSearchService.getIssuesWithWorklogBetweenPeriod(range.getStartDate(), range.getEndDate(), 1000);

      for (Issue issue : issues) {
        List<Worklog> worklogs = issue.getWorklogs();
        if (issue.getComponents().size()!=0) {
          for (Worklog worklog : worklogs) {

            if (worklog.getCreated().isAfter(range.getStartDate()) && worklog.getCreated().isBefore(range.getEndDate())) {
              BigInteger nbComponents = BigInteger.valueOf(issue.getComponents().size());
              BigInteger timeSpent = worklog.getTimeSpentSeconds().divide(nbComponents);
              // browse components
              for (Component component : issue.getComponents()) {
                WorklogRange worklogRange = worklogList.getRangeForComponent(range.getLabel(), component.getName());
                worklogRange.addTime(timeSpent.intValue());
              }
            }
          }

        }

      }
      // add rows
      for (WorklogRange value : worklogList.getWorklogRanges().values()) {
        DAORow newRow = new DAORow();
        newRow.put(daoHeader.get(0).getValue(), range.getLabel());
        newRow.put(daoHeader.get(1).getValue(), range.getType());
        newRow.put(daoHeader.get(2).getValue(), value.getWorklogKey().getComponent());
        newRow.put(daoHeader.get(3).getValue(), String.valueOf(value.getTotalTimeSpent()));
        newRow.put(daoHeader.get(4).getValue(), "");
        result.add(newRow);
      }

    }

    return result;
  }

}
