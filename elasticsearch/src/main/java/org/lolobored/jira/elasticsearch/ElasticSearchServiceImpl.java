package org.lolobored.jira.elasticsearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.lolobored.jira.elasticsearch.repository.IssueRepository;
import org.lolobored.jira.elasticsearch.repository.SprintRepository;
import org.lolobored.jira.http.HttpException;
import org.lolobored.jira.http.HttpUtil;
import org.lolobored.jira.model.Issue;
import org.lolobored.jira.model.Sprint;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {


  @Autowired
  private IssueRepository issueRepository;

  @Autowired
  private SprintRepository sprintRepository;

  private static Logger logger = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);

	private static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
		.setSerializationInclusion(JsonInclude.Include.NON_NULL);

	@Override
	public void setMaxResultWindow(String url, Integer maxResult) throws HttpException {
		HttpUtil httpUtil = HttpUtil.getInstance(false);
		String json= "{\n" +
			"  \"index.max_result_window\" : \""+maxResult+"\"\n" +
			"}";
		Map<String, String> header= new HashMap<>();
		header.put("Content-Type", "application/json;charset=UTF-8");
		httpUtil.put(url+"/jira/_settings", json, header);
	}


	@Override
	public void insertIssue(Issue issue) {
		issueRepository.save(issue);
	}

	@Override
	public void removeIssue(String key){
		issueRepository.deleteById(key);
	}

  @Override
	public void insertSprint(Sprint sprint)  {
    sprintRepository.save(sprint);
  }

	@Override
	public List<Issue> getAllIssuesPerProject(String project, int maximum) {
		List<Issue> result= new ArrayList();
		Pageable pageRequest= new PageRequest(0, maximum, Sort.Direction.ASC, "key.keyword");
		Page<Issue> page= issueRepository.findByProject(pageRequest, project);
		result.addAll(page.getContent()) ;
		for (int i=1; i< page.getTotalPages(); i++){
			pageRequest = pageRequest.next();
			page= issueRepository.findByProject(pageRequest, project);
			result.addAll(page.getContent()) ;
		}
		return result;

	}

  @Override
  public List<Sprint> getAllSprintsPerProject(String project, int maximum) {
    List<Sprint> result= new ArrayList();
    Pageable pageRequest= new PageRequest(0, maximum);
    Page<Sprint> page= sprintRepository.findByProject(pageRequest, project);
    result.addAll(page.getContent()) ;
    for (int i=1; i< page.getTotalPages(); i++){
      pageRequest = pageRequest.next();
      page= sprintRepository.findByProject(pageRequest, project);
      result.addAll(page.getContent()) ;
    }
    return result;

  }

	@Override
	public Issue getIssue(String jiraKey){
		return issueRepository.findByKey(jiraKey);
	}

	@Override
	public List<Issue> getIssuesWithWorklogBetweenPeriod(LocalDateTime startDate, LocalDateTime endDate, String project, int maximum){
    List<Issue> result= new ArrayList();
    Pageable pageRequest= new PageRequest(0, maximum, Sort.Direction.ASC, "key.keyword");
    long start= startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    long end= endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    Page<Issue> page= issueRepository.findByWorklogsCreatedMillisecondsBetweenAndProject(pageRequest, start, end, project);
    result.addAll(page.getContent()) ;
    for (int i=1; i< page.getTotalPages(); i++){
      pageRequest = pageRequest.next();
      page= issueRepository.findByWorklogsCreatedMillisecondsBetweenAndProject(pageRequest,  start, end, project);
      result.addAll(page.getContent()) ;
    }
    return result;
	}

	@Override
	public List<Issue> getBugsOpenedWithinPeriod(LocalDateTime startDate, LocalDateTime endDate, String project, int maximum){
		List<Issue> result= new ArrayList();
		Pageable pageRequest= new PageRequest(0, maximum, Sort.Direction.ASC, "key.keyword");
		long start= startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		long end= endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		Page<Issue> page= issueRepository.findByCreatedMillisecondsBetweenAndProjectAndIssueType(pageRequest, start, end, project, Issue.Bugs);
		result.addAll(page.getContent()) ;
		for (int i=1; i< page.getTotalPages(); i++){
			pageRequest = pageRequest.next();
			page= issueRepository.findByCreatedMillisecondsBetweenAndProjectAndIssueType(pageRequest,  start, end, project, Issue.Bugs);
			result.addAll(page.getContent()) ;
		}
		return result;
	}

	@Override
	public List<Issue> getBugsResolvedWithinPeriod(LocalDateTime startDate, LocalDateTime endDate, String project, int maximum){
		List<Issue> result= new ArrayList();
		Pageable pageRequest= new PageRequest(0, maximum, Sort.Direction.ASC, "key.keyword");
		long start= startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		long end= endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		Page<Issue> page= issueRepository.findByResolvedMillisecondsBetweenAndProjectAndIssueType(pageRequest, start, end, project, Issue.Bugs);
		result.addAll(page.getContent()) ;
		for (int i=1; i< page.getTotalPages(); i++){
			pageRequest = pageRequest.next();
			page= issueRepository.findByResolvedMillisecondsBetweenAndProjectAndIssueType(pageRequest,  start, end, project, Issue.Bugs);
			result.addAll(page.getContent()) ;
		}
		return result;
	}

	@Override
	public List<Issue> getBugsOpenedBefore(LocalDateTime startDate, String project, int maximum){
		List<Issue> result= new ArrayList();
		Pageable pageRequest= new PageRequest(0, maximum, Sort.Direction.ASC, "key.keyword");
		long start= startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		Page<Issue> page= issueRepository.findByCreatedMillisecondsBeforeAndProjectAndIssueType(pageRequest, start, project, Issue.Bugs);
		result.addAll(page.getContent()) ;
		for (int i=1; i< page.getTotalPages(); i++){
			pageRequest = pageRequest.next();
			page= issueRepository.findByCreatedMillisecondsBeforeAndProjectAndIssueType(pageRequest,  start, project, Issue.Bugs);
			result.addAll(page.getContent()) ;
		}
		return result;
	}

	@Override
	public List<Issue> getBugsResolvedBefore(LocalDateTime startDate, String project, int maximum){
		List<Issue> result= new ArrayList();
		Pageable pageRequest= new PageRequest(0, maximum, Sort.Direction.ASC, "key.keyword");
		long start= startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		Page<Issue> page= issueRepository.findByResolvedMillisecondsBeforeAndProjectAndIssueType(pageRequest, start, project, Issue.Bugs);
		result.addAll(page.getContent()) ;
		for (int i=1; i< page.getTotalPages(); i++){
			pageRequest = pageRequest.next();
			page= issueRepository.findByResolvedMillisecondsBeforeAndProjectAndIssueType(pageRequest,  start, project, Issue.Bugs);
			result.addAll(page.getContent()) ;
		}
		return result;
	}
}
