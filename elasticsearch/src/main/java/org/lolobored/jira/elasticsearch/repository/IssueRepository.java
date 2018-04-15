package org.lolobored.jira.elasticsearch.repository;

import org.lolobored.jira.model.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

public interface IssueRepository extends ElasticsearchRepository<Issue, String> {

	Issue findByKey(String key);

  Page<Issue> findByWorklogsCreatedMillisecondsBetween(Pageable pageable, long startTime, long endTime);

  //Page<Media> findByUser(String user, Pageable pageable);

	//Page<Media> findByUserAndGenresInAndYearGreaterThanEqualAndYearLessThanEqual(String user, List<String> genres, Integer startYear, Integer endYear, Pageable pageable);
}
