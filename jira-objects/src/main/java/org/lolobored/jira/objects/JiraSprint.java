package org.lolobored.jira.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraSprint {

  private Integer id;
  private String state;
  private String name;
  private Date startDate;
  private Date endDate;
  private Date completeDate;

  private Integer originBoardId;

}
