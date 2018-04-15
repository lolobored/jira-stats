package org.lolobored.jira.controllers;

import org.lolobored.jira.ProcessException;
import org.lolobored.jira.dao.data.DAOTable;
import org.lolobored.jira.services.worklog.WorklogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = {"/worklog", "/home"})
public class WorklogController {

  @Autowired
  WorklogService worklogService;


  @RequestMapping(method = RequestMethod.GET)
  public String retrieveWorklogStatistics(ModelMap modelMap, HttpServletRequest httpRequest) throws ProcessException {

    DAOTable share_per_component_type = worklogService.getSharedLoggedTimePerComponent();
    // filling the data which we will use in the JS page
    modelMap.addAttribute("share_per_component_type", share_per_component_type.toJSON());
    // filling the div id which we will use so that it's replaced at
    // everyplace we use it
    modelMap.addAttribute("share_per_component_type_component_box", "share_per_component_type_component_box");
    modelMap.addAttribute("share_per_component_type_range_type_box",
      "share_per_component_type_range_type_box");
    modelMap.addAttribute("share_per_component_type_range_box", "share_per_component_type_range_box");
    modelMap.addAttribute("share_per_component_type_chart", "share_per_component_type_chart");
    modelMap.addAttribute("share_per_component_type_table", "share_per_component_type_table");
    modelMap.addAttribute("share_per_component_type_dashboard", "share_per_component_type_dashboard");

    return "/worklog";
  }
}
