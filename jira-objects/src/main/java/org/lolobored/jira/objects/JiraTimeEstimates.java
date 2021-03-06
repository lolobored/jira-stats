package org.lolobored.jira.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraTimeEstimates {
  private JiraTimetrackingFields fields;
}
