package org.lolobored.jira.controllers;

import org.lolobored.jira.ProcessException;
import org.lolobored.jira.dao.data.DAOTable;
import org.lolobored.jira.services.backlog.component.BacklogComponentService;
import org.lolobored.jira.webgraphs.JiraProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping(value = {"/backlog"})
public class BacklogController {

  @Autowired
  BacklogComponentService backlogComponentService;

	@Autowired
  JiraProperties jiraProperties;


  @RequestMapping(method = RequestMethod.GET)
  public String retrieveWorklogStatistics(ModelMap modelMap, HttpServletRequest httpRequest) throws ProcessException {

    DAOTable backlog_opening_per_component = backlogComponentService.getBacklogPerComponent();
    // filling the data which we will use in the JS page
    modelMap.addAttribute("backlog_opening_per_component", backlog_opening_per_component.toJSON());
    // filling the div id which we will use so that it's replaced at
    // everyplace we use it
    modelMap.addAttribute("backlog_opening_per_component_project_box", "backlog_opening_per_component_project_box");
    modelMap.addAttribute("backlog_opening_per_component_range_type_box", "backlog_opening_per_component_range_type_box");
    modelMap.addAttribute("backlog_opening_per_component_chart", "backlog_opening_per_component_chart");
    modelMap.addAttribute("backlog_opening_per_component_table", "backlog_opening_per_component_table");
    modelMap.addAttribute("backlog_opening_per_component_dashboard", "backlog_opening_per_component_dashboard");
    return "/backlog";
  }

  @ModelAttribute("projectList")
  public Map<String, String> getProjectList() {
    Map<String, String> projectList = new TreeMap<>();
    String[] projects= jiraProperties.getProject().split(";");
    for (String project: projects) {
      projectList.put(project, project);
    }
    return projectList;
  }

}
