package org.lolobored.jira.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraBoardList {

  // the issues returned when doing a search
  private List<JiraBoard> values = new ArrayList();
  private int maxResults;
  private int total;

}
