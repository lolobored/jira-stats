package org.lolobored.jira.elasticsearch.repository;

import org.lolobored.jira.model.Issue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IssueRepository extends ElasticsearchRepository<Issue, String> {

	Issue findByKey(String key);

	//Page<Media> findByUser(String user, Pageable pageable);

	//Page<Media> findByUserAndGenresInAndYearGreaterThanEqualAndYearLessThanEqual(String user, List<String> genres, Integer startYear, Integer endYear, Pageable pageable);
}
