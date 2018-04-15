package org.lolobored.jira.ranges;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.lolobored.jira.constants.RangeConstants;

import java.time.LocalDateTime;

@Data
public class Range implements Comparable<Range>{

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  public static String MONTH_TYPE = RangeConstants.MONTH_TYPE;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  public static String QUARTER_TYPE = RangeConstants.QUARTER_TYPE;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  public static String SPRINT_TYPE = RangeConstants.SPRINT_TYPE;

  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String type;
  private String label;

  public int compareTo(Range o) {

    if (!this.type.equals(o.getType())) {
      return type.compareTo(o.getType());
    }
    Range toCompare = (Range) o;

    if (!this.startDate.equals(toCompare.getStartDate())) {
      return startDate.compareTo(toCompare.getStartDate());
    }
    return 0;
  }

}
