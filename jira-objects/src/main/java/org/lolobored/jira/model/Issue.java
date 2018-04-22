package org.lolobored.jira.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Document(indexName = "jira")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static final String Bugs = "Bug";
	public static final String ClosedNotImplementedStatus= "Closed Not Implemented";

	@Id
	private String key;
	private String project;
	private String title;
	private List<Sprint> sprints = new ArrayList<>();
	private List<Component> components = new ArrayList<>();
	private List<String> labels = new ArrayList<>();
	private Sprint endSprint;
	private String status;
	private BigInteger originalEstimateSeconds;
	private String issueType;
	private List<Worklog> worklogs = new ArrayList<>();
	private String epicIssue;
	private String epicTitle;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Setter(AccessLevel.NONE)
	private LocalDateTime created;
	@Setter(AccessLevel.NONE)
	private Long createdMilliseconds;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Setter(AccessLevel.NONE)
	private LocalDateTime resolved;
	@Setter(AccessLevel.NONE)
	private Long resolvedMilliseconds;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Setter(AccessLevel.NONE)
	private LocalDateTime updated;
	@Setter(AccessLevel.NONE)
	private Long updatedMilliseconds;

	public void setCreated(LocalDateTime created){
		this.created= created;
		if (created!=null) {
			this.createdMilliseconds = created.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		}
	}

	public void setResolved(LocalDateTime resolved){
		this.resolved= resolved;
		if (resolved!=null) {
			this.resolvedMilliseconds = resolved.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		}
	}

	public void setUpdated(LocalDateTime updated){
		this.updated= updated;
		if (updated!=null) {
			this.updatedMilliseconds = updated.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		}
	}

	@Override
	public boolean equals(Object obj){
		if (obj instanceof Issue){
			Issue issue = (Issue) obj;
			return issue.getKey().equals(this.getKey());
		}
		return false;

	}
}
