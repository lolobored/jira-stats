package org.lolobored.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Data
@Document(indexName = "sprint")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(exclude={"name", "startDate", "endDate"})
public class Sprint implements Comparable<Sprint>{

  @Id
  private Integer id;
  private String name;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime startDate;
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime endDate;

  @Override
  public int compareTo(Sprint toCompare) {
    // sort sprint by date
    if (toCompare.getStartDate() != null) {
      if (startDate != null) {
        return startDate.compareTo(toCompare.getStartDate());
      } else {
        // if this sprint has no date while the other has some, we have
        // to consider this one as greater
        // as it will be done after
        return 1;
      }
    } else {
      // if this sprint has a date while the other has none, we have to
      // consider this one as lesser
      // as it will be done before or has already been done
      if (startDate != null) {
        return -1;
      } else {
        return name.compareTo(toCompare.getName());
      }
    }
  }
}
