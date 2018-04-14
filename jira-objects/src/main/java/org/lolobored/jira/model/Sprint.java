package org.lolobored.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sprint {
  private Integer id;
  private String name;
  private LocalDateTime startDate;
  private LocalDateTime endDate;

}
