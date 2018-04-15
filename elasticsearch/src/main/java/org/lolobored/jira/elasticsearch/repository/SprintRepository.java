package org.lolobored.jira.elasticsearch.repository;

import org.lolobored.jira.model.Issue;
import org.lolobored.jira.model.Sprint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SprintRepository extends ElasticsearchRepository<Sprint, Integer> {


}
