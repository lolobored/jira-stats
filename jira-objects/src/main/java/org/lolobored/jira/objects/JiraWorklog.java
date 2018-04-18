package org.lolobored.jira.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraWorklog {
  private Date created;
  private Date updated;
  private Date started;
  private BigInteger timeSpentSeconds;
  private WorklogAuthor author;
}
