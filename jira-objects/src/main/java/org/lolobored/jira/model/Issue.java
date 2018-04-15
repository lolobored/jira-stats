package org.lolobored.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Document(indexName = "jira")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

  @Id
  private String key;
  private String project;
  private List<Sprint> sprints= new ArrayList<>();
  private List<Component> components= new ArrayList<>();
  private Sprint endSprint;
  private String status;
  private BigInteger originalEstimateSeconds;
  private String issueType;
  private List<Worklog> worklogs= new ArrayList<>();
}
