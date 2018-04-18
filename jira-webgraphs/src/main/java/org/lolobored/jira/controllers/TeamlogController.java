package org.lolobored.jira.controllers;

import org.lolobored.jira.ProcessException;
import org.lolobored.jira.dao.data.DAOTable;
import org.lolobored.jira.services.teamlog.TeamlogService;
import org.lolobored.jira.services.worklog.component.WorklogComponentService;
import org.lolobored.jira.services.worklog.epic.WorklogEpicService;
import org.lolobored.jira.webgraphs.JiraProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = {"/teamlog"})
public class TeamlogController {

  @Autowired
  TeamlogService teamlogService;
  
	@Autowired
  JiraProperties jiraProperties;


  @RequestMapping(method = RequestMethod.GET)
  public String retrieveTeamlogStatistics(ModelMap modelMap, HttpServletRequest httpRequest) throws ProcessException {

    DAOTable team_worklog = teamlogService.getLoggedTimePerTeamMember();
    // filling the data which we will use in the JS page
		modelMap.addAttribute("team_worklog", team_worklog.toJSON());
		// filling the div id which we will use so that it's replaced at
		// everyplace we use it
		modelMap.addAttribute("team_worklog_project_box", "team_worklog_project_box");
		modelMap.addAttribute("team_worklog_resource_box", "team_worklog_resource_box");
		modelMap.addAttribute("team_worklog_chart", "team_worklog_chart");
		modelMap.addAttribute("team_worklog_table", "team_worklog_table");
		modelMap.addAttribute("team_worklog_dashboard", "team_worklog_dashboard");
    return "/teamlog";
  }

  @ModelAttribute("projectList")
  public Map<String, String> getProjectList() {
    Map<String, String> projectList = new HashMap<>();
    String project= jiraProperties.getProject();
    projectList.put(project, project);
    return projectList;
  }

}
