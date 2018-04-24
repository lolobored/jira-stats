package org.lolobored.jira.controllers;

import org.lolobored.jira.ProcessException;
import org.lolobored.jira.dao.data.DAOTable;
import org.lolobored.jira.properties.ProjectMainLabelsProperties;
import org.lolobored.jira.services.backlog.component.averagefix.BacklogAverageFixTimeComponentService;
import org.lolobored.jira.services.backlog.component.opening.BacklogOpeningComponentService;
import org.lolobored.jira.services.backlog.component.total.BacklogComponentService;
import org.lolobored.jira.properties.JiraProperties;
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
	BacklogOpeningComponentService backlogOpeningComponentService;

	@Autowired
	BacklogComponentService backlogComponentService;

	@Autowired
	BacklogAverageFixTimeComponentService backlogAverageFixTimeComponentService;

	@Autowired
  JiraProperties jiraProperties;

	@Autowired
	ProjectMainLabelsProperties projectMainLabelsProperties;



	@RequestMapping(method = RequestMethod.GET)
  public String retrieveWorklogStatistics(ModelMap modelMap, HttpServletRequest httpRequest) throws ProcessException {

		DAOTable backlog__per_component = backlogComponentService.getBacklogPerComponent();
		// filling the data which we will use in the JS page
		modelMap.addAttribute("backlog__per_component", backlog__per_component.toJSON());
		// filling the div id which we will use so that it's replaced at
		// everyplace we use it
		modelMap.addAttribute("backlog__per_component_project_box", "backlog__per_component_project_box");
		modelMap.addAttribute("backlog__per_component_range_type_box", "backlog__per_component_range_type_box");
		modelMap.addAttribute("backlog__per_component_chart", "backlog__per_component_chart");
		modelMap.addAttribute("backlog__per_component_table", "backlog__per_component_table");
		modelMap.addAttribute("backlog__per_component_dashboard", "backlog__per_component_dashboard");


		DAOTable backlog_opening_per_component = backlogOpeningComponentService.getBacklogOpeningPerComponent();
    // filling the data which we will use in the JS page
    modelMap.addAttribute("backlog_opening_per_component", backlog_opening_per_component.toJSON());
    // filling the div id which we will use so that it's replaced at
    // everyplace we use it
    modelMap.addAttribute("backlog_opening_per_component_project_box", "backlog_opening_per_component_project_box");
    modelMap.addAttribute("backlog_opening_per_component_range_type_box", "backlog_opening_per_component_range_type_box");
    modelMap.addAttribute("backlog_opening_per_component_chart", "backlog_opening_per_component_chart");
    modelMap.addAttribute("backlog_opening_per_component_table", "backlog_opening_per_component_table");
    modelMap.addAttribute("backlog_opening_per_component_dashboard", "backlog_opening_per_component_dashboard");

		DAOTable backlog_average_fix_per_component = backlogAverageFixTimeComponentService.getAverageFixTimePerComponent();
		// filling the data which we will use in the JS page
		modelMap.addAttribute("backlog_average_fix_per_component", backlog_average_fix_per_component.toJSON());
		// filling the div id which we will use so that it's replaced at
		// everyplace we use it
		modelMap.addAttribute("backlog_average_fix_per_component_project_box", "backlog_average_fix_per_component_project_box");
		modelMap.addAttribute("backlog_average_fix_per_component_range_type_box", "backlog_average_fix_per_component_range_type_box");
		modelMap.addAttribute("backlog_average_fix_per_component_chart", "backlog_average_fix_per_component_chart");
		modelMap.addAttribute("backlog_average_fix_per_component_table", "backlog_average_fix_per_component_table");
		modelMap.addAttribute("backlog_average_fix_per_component_dashboard", "backlog_average_fix_per_component_dashboard");


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
