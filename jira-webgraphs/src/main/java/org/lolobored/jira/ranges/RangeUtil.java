package org.lolobored.jira.ranges;


import org.joda.time.PeriodType;
import org.lolobored.jira.constants.RangeConstants;

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
     *
     */
    public static List<Range> getMonthlyRange() {
      List<Range> result = new ArrayList();
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime current ;

      int maximumForLoop = RangeConstants.MAXIMUM_NUMBER_MONTH+1;
      for (int i = 0; i < maximumForLoop; i++) {
        current = now.minusMonths(i);
        Range month = new Range();
        month.setType(Range.MONTH_TYPE);
        LocalDateTime startDate = LocalDateTime.of(current.getYear(), current.getMonth(), 01, 00, 00, 00 );
        int nbDaysInMonth= startDate.getMonth().length(startDate.toLocalDate().isLeapYear());
        LocalDateTime endDate = LocalDateTime.of(current.getYear(), current.getMonth(), nbDaysInMonth, 23, 59, 59 );
        month.setStartDate(startDate);
        month.setEndDate(endDate);
        month.setLabel(startDate.getMonth().name()+" "+startDate.getYear());
        result.add(month);
      }

      // sort the results
      Collections.sort(result);
      return result;
    }



}
