package org.lolobored.jira.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraVersion {

  private int id;
  private String name;
  private String description;
  private Date releaseDate;

  private String projectId;

}
