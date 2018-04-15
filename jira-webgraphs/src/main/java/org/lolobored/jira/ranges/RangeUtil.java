package org.lolobored.jira.ranges;


import org.joda.time.PeriodType;
import org.lolobored.jira.constants.RangeConstants;
import org.lolobored.jira.model.Sprint;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RangeUtil {

  public static int EVERY_RANGES = -1;
  // helpers (it's quicker to use this way)
  public static String MONTH_TYPE = RangeConstants.MONTH_TYPE;
  public static String QUARTER_TYPE = RangeConstants.QUARTER_TYPE;
  public static String SPRINT_TYPE = RangeConstants.SPRINT_TYPE;
  public static String ALL_TYPE = RangeConstants.ALL_TYPE;

  /**
   * Get month list
   */
  public static List<Range> getMonthlyRange() {
    List<Range> result = new ArrayList();
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime current;

    int maximumForLoop = RangeConstants.MAXIMUM_NUMBER_MONTH + 1;
    for (int i = 0; i < maximumForLoop; i++) {
      current = now.minusMonths(i);
      Range month = new Range();
      month.setType(Range.MONTH_TYPE);
      LocalDateTime startDate = LocalDateTime.of(current.getYear(), current.getMonth(), 01, 00, 00, 00);
      int nbDaysInMonth = startDate.getMonth().length(startDate.toLocalDate().isLeapYear());
      LocalDateTime endDate = LocalDateTime.of(current.getYear(), current.getMonth(), nbDaysInMonth, 23, 59, 59);
      month.setStartDate(startDate);
      month.setEndDate(endDate);
      month.setLabel(startDate.getMonth().name() + " " + startDate.getYear());
      result.add(month);
    }

    // sort the results
    Collections.sort(result);
    return result;
  }

  /**
   * Get Quarter list
   */
  public static List<Range> getQuarterRange() {
    List<Range> result = new ArrayList();
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime current;
    LocalDateTime quarterNow;

    if (now.getMonthValue() == 1 || now.getMonthValue() == 4
      || now.getMonthValue() == 7 || now.getMonthValue() == 10) {
      quarterNow = now;
    } else if (now.getMonthValue() == 2 || now.getMonthValue() == 5
      || now.getMonthValue() == 8 || now.getMonthValue() == 11) {
      quarterNow = now.minusMonths(1);
    } else {
      quarterNow = now.minusMonths(2);
    }

    int maximumForLoop = RangeConstants.MAXIMUM_NUMBER_MONTH + 1;
    for (int i = 0; i < maximumForLoop; i++) {
      current = quarterNow.minusMonths(3 * i);
      Range quarter = new Range();
      quarter.setType(Range.QUARTER_TYPE);

      LocalDateTime startDate = LocalDateTime.of(current.getYear(), current.getMonthValue(), 01, 00, 00, 00);
      LocalDateTime nextMonth = current.plusMonths(2);
      int nbDaysInMonth = nextMonth.getMonth().length(nextMonth.toLocalDate().isLeapYear());
      LocalDateTime endDate = LocalDateTime.of(current.getYear(), nextMonth.getMonthValue(), nbDaysInMonth, 23, 59, 59);
      quarter.setStartDate(startDate);
      quarter.setEndDate(endDate);
      quarter.setLabel("Q" + nextMonth.getMonthValue() / 3 + " " + nextMonth.getYear());
      result.add(quarter);
    }

    // sort the results
    Collections.sort(result);
    return result;
  }

  /**
   * Get Quarter list
   */
  public static List<Range> getSprintRange() {
    List<Range> result = new ArrayList();
    /*List<Sprint> sprints = SprintList.getInstance().getSortedList();
    if (sprints.isEmpty()){
      return result;
    }
    int maximumForLoop = RangeConstants.MAXIMUM_NUMBER_MONTH + 1;
    for (int i = 0; i < maximumForLoop; i++) {
      Range sprint = new Range();
      if (sprints.size() - maximumForLoop - 1< 0){
        break;
      }
      Sprint currentSprint = sprints.get(sprints.size() - maximumForLoop - 1);
      sprint.setLabel(currentSprint.getName());
      sprint.setStartDate(currentSprint.getStartDate());
      sprint.setEndDate(currentSprint.getEndDate());
      sprint.setType(Range.SPRINT_TYPE);
      result.add(sprint);
    }*/
    return result;
  }

}
