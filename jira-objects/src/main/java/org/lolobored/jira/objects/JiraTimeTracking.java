package org.lolobored.jira.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraTimeTracking {

  private BigInteger originalEstimateSeconds;
  private BigInteger remainingEstimateSeconds;
  private BigInteger timeSpentSeconds;

}
