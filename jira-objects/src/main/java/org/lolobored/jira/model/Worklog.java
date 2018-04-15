package org.lolobored.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Worklog {

  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @Setter(AccessLevel.NONE)
  private LocalDateTime created;
  @Setter(AccessLevel.NONE)
  private long createdMilliseconds;
  private BigInteger timeSpentSeconds;

  public void setCreated(LocalDateTime created){
    this.created= created;
    this.createdMilliseconds= created.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
}
