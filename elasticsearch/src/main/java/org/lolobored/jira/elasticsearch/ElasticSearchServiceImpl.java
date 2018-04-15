package org.lolobored.jira.elasticsearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.lolobored.jira.elasticsearch.repository.IssueRepository;
import org.lolobored.jira.model.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {


	@Autowired
	private IssueRepository repository;

	private static Logger logger = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);

	private static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
		.setSerializationInclusion(JsonInclude.Include.NON_NULL);


	@Override
	public void insertIssue(Issue issue) {
		repository.save(issue);
	}

	@Override
	public List<Issue> getAllIssues(int maximum) {
		List<Issue> result= new ArrayList();
		Pageable pageRequest= new PageRequest(0, maximum, Sort.Direction.ASC, "key.keyword");
		Page<Issue> page= repository.findAll(pageRequest);
		result.addAll(page.getContent()) ;
		for (int i=1; i< page.getTotalPages(); i++){
			pageRequest = pageRequest.next();
			page= repository.findAll(pageRequest);
			result.addAll(page.getContent()) ;
		}
		return result;

	}

	@Override
	public Issue getIssue(String jiraKey){
		return repository.findByKey(jiraKey);
	}

	@Override
	public List<Issue> getIssuesWithWorklogBetweenPeriod(LocalDateTime startDate, LocalDateTime endDate, int maximum){
    List<Issue> result= new ArrayList();
    Pageable pageRequest= new PageRequest(0, maximum, Sort.Direction.ASC, "key.keyword");
    long start= startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    long end= endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    Page<Issue> page= repository.findByWorklogsCreatedMillisecondsBetween(pageRequest, start, end);
    result.addAll(page.getContent()) ;
    for (int i=1; i< page.getTotalPages(); i++){
      pageRequest = pageRequest.next();
      page= repository.findByWorklogsCreatedMillisecondsBetween(pageRequest,  start, end);
      result.addAll(page.getContent()) ;
    }
    return result;
	}
}
