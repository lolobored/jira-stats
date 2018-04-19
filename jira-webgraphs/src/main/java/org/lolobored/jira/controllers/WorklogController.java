package org.lolobored.jira.controllers;

import org.lolobored.jira.ProcessException;
import org.lolobored.jira.dao.data.DAOTable;
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
@RequestMapping(value = {"/","/worklog", "/home"})
public class WorklogController {

  @Autowired
  WorklogComponentService worklogComponentService;

	@Autowired
	WorklogEpicService worklogEpicService;

	@Autowired
  JiraProperties jiraProperties;


  @RequestMapping(method = RequestMethod.GET)
  public String retrieveWorklogStatistics(ModelMap modelMap, HttpServletRequest httpRequest) throws ProcessException {

    DAOTable share_per_component_type = worklogComponentService.getSharedLoggedTimePerComponent();
    // filling the data which we will use in the JS page
    modelMap.addAttribute("share_per_component_type", share_per_component_type.toJSON());
    // filling the div id which we will use so that it's replaced at
    // everyplace we use it
    modelMap.addAttribute("share_per_component_type_project_box", "share_per_component_type_project_box");
    modelMap.addAttribute("share_per_component_type_range_type_box",
      "share_per_component_type_range_type_box");
    modelMap.addAttribute("share_per_component_type_range_box", "share_per_component_type_range_box");
    modelMap.addAttribute("share_per_component_type_chart", "share_per_component_type_chart");
    modelMap.addAttribute("share_per_component_type_table", "share_per_component_type_table");
    modelMap.addAttribute("share_per_component_type_dashboard", "share_per_component_type_dashboard");


    // filling the data which we will use in the JS page
    modelMap.addAttribute("share_per_component_type_history", share_per_component_type.toJSON());
    // filling the div id which we will use so that it's replaced at
    // everyplace we use it
    modelMap.addAttribute("share_per_component_type_history_project_box", "share_per_component_type_history_project_box");
    modelMap.addAttribute("share_per_component_type_history_range_type_box",
      "share_per_component_type_history_range_type_box");
    modelMap.addAttribute("share_per_component_type_history_range_box", "share_per_component_type_history_range_box");
    modelMap.addAttribute("share_per_component_type_history_chart", "share_per_component_type_history_chart");
    modelMap.addAttribute("share_per_component_type_history_table", "share_per_component_type_history_table");
    modelMap.addAttribute("share_per_component_type_history_dashboard", "share_per_component_type_history_dashboard");

    DAOTable share_per_epic_type = worklogEpicService.getSharedLoggedTimePerEpic();
    // filling the data which we will use in the JS page
    modelMap.addAttribute("share_per_epic_type", share_per_epic_type.toJSON());
    // filling the div id which we will use so that it's replaced at
    // everyplace we use it
    modelMap.addAttribute("share_per_epic_type_project_box", "share_per_epic_type_project_box");
    modelMap.addAttribute("share_per_epic_type_range_type_box",
        "share_per_epic_type_range_type_box");
    modelMap.addAttribute("share_per_epic_type_range_box", "share_per_epic_type_range_box");
    modelMap.addAttribute("share_per_epic_type_chart", "share_per_epic_type_chart");
    modelMap.addAttribute("share_per_epic_type_table", "share_per_epic_type_table");
    modelMap.addAttribute("share_per_epic_type_dashboard", "share_per_epic_type_dashboard");


    // filling the data which we will use in the JS page
    modelMap.addAttribute("share_per_epic_type_history", share_per_epic_type.toJSON());
    // filling the div id which we will use so that it's replaced at
    // everyplace we use it
    modelMap.addAttribute("share_per_epic_type_history_project_box", "share_per_epic_type_history_project_box");
    modelMap.addAttribute("share_per_epic_type_history_range_type_box",
        "share_per_epic_type_history_range_type_box");
    modelMap.addAttribute("share_per_epic_type_history_range_box", "share_per_epic_type_history_range_box");
    modelMap.addAttribute("share_per_epic_type_history_chart", "share_per_epic_type_history_chart");
    modelMap.addAttribute("share_per_epic_type_history_table", "share_per_epic_type_history_table");
    modelMap.addAttribute("share_per_epic_type_history_dashboard", "share_per_epic_type_history_dashboard");


    return "/worklog";
  }

  @ModelAttribute("projectList")
  public Map<String, String> getProjectList() {
    Map<String, String> projectList = new HashMap<>();
    String[] projects= jiraProperties.getProject().split(";");
    for (String project: projects) {
      projectList.put(project, project);
    }
    return projectList;
  }

}
