package org.lolobored.jira.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraSprintList {

  // the issues returned when doing a search
  private List<JiraSprint> values = new ArrayList();
  private int maxResults;
  private boolean isLast;

}
