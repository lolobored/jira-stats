package org.lolobored.jira.elasticsearch.repository;

import org.lolobored.jira.model.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

public interface IssueRepository extends ElasticsearchRepository<Issue, String> {

	Issue findByKey(String key);

	Page<Issue> findByProject(Pageable pageable, String project);

	Page<Issue> findByWorklogsCreatedMillisecondsBetweenAndProject(Pageable pageable, long startTime, long endTime, String project);

	Page<Issue> findByCreatedMillisecondsBetweenAndProjectAndIssueType(Pageable pageable, long startTime, long endTime, String project, String issueType);

	Page<Issue> findByResolvedMillisecondsBetweenAndProjectAndIssueType(Pageable pageable, long startTime, long endTime, String project, String issueType);

	Page<Issue> findByCreatedMillisecondsBeforeAndProjectAndIssueType(Pageable pageable, long startTime, String project, String issueType);

	Page<Issue> findByResolvedMillisecondsBeforeAndProjectAndIssueType(Pageable pageable, long startTime, String project, String issueType);

}
