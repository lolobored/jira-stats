<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta http-equiv="Cache-control" content="public">
  <title>PO Steering Report</title>
  <spring:url value="/resources/css/graph.css" var="graphcss"/>
  <!-- From https://www.creativejuiz.fr/blog/tutoriels/creer-menu-sticky-avec-javascript-css#sticky-back -->
  <spring:url value="/resources/js/navigation-bar.js" var="navigationjs"/>
  <spring:url value="/resources/js/sharepertype.js" var="sharepertypejs"/>
  <spring:url value="/resources/js/sharepertypehistory.js" var="sharepertypehistoryjs"/>
  <link type="text/css" rel="stylesheet" href="${graphcss}"/>
  <script type="text/javascript" src="${sharepertypejs}"></script>
  <script type="text/javascript" src="${sharepertypehistoryjs}"></script>
  <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>


  <!-- Create all dashboards and set control states -->
  <script type="text/javascript">

      // Load the Visualization API and the chart package.
      google.charts.load('current', {
          'packages': ['corechart', 'bar', 'table', 'controls', 'annotationchart']
      });

      // Set a callback to run when the Google Visualization API is loaded.
      google.charts.setOnLoadCallback(initCharts);

      function initCharts() {

          share_per_component_type_control = drawChartSharePerComponent();
          share_per_component_type_history_control = drawChartSharePerComponentHistory();

          share_per_component_type_control.setState({'selectedValues': [sessionStorage.selectedProject]});
          share_per_component_type_history_control.setState({'selectedValues': [sessionStorage.selectedProject]});

      }

      function resetCharts() {
          // save the project selected value
          sessionStorage.selectedProject = document.getElementById("defaultProject").value;
          initCharts();
      }

      function initDefaultProjectBoxOnLoad() {

          if (sessionStorage.selectedProject != null) {
              // set the main project to the session storage value
              document.getElementById("defaultProject").value = sessionStorage.selectedProject;
          }
          else {
              // set the main project to the session storage value
              document.getElementById("defaultProject").selectedIndex = "0";
          }
      }


      // Share per issue type
      function drawChartSharePerComponent() {

          control = drawSharePerComponent(${share_per_component_type}, '${share_per_component_type_dashboard}',
              '${share_per_component_type_chart}',
              '${share_per_component_type_project_box}',
              '${share_per_component_type_range_type_box}',
              '${share_per_component_type_range_box}',
              '${share_per_component_type_table}');
          return control;
      }

      // Share per issue type history
      function drawChartSharePerComponentHistory() {

          control = drawSharePerComponentHistory(${share_per_component_type_history}, '${share_per_component_type_history_dashboard}',
              '${share_per_component_type_history_chart}',
              '${share_per_component_type_history_project_box}',
              '${share_per_component_type_history_range_type_box}',
              '${share_per_component_type_history_table}');
          return control;
      }

  </script>

</head>
<!-- Initialize the project box on page load -->
<body onload="initDefaultProjectBoxOnLoad();">
<div class="banner-body">
  <header id="header" role="banner" class="main-header">
    <div class="header-inner">
      <form:select path="projectList" items="${projectList}"
                   id="defaultProject"
                   style="position:relative; margin-top: 13px; color: #8f8f8f; font-family: Arial; font-size: 13px; font-weight: bold;background:-webkit-gradient(linear, left top, left bottom, color-stop(0.05, #ffffff), color-stop(1, #f6f6f6));"
                   onchange="resetCharts();" class="project_box">
      </form:select>

      <nav class="header-nav">
        <ul>
          <li><a href="worklog">Worklog</a></li>
        </ul>
      </nav>

    </div>
  </header>
  <section id="section" class="main-section first-section">
    <div class="container">
      <p class="bigtxt txtcenter">Worklog Charts</p>
    </div>
  </section>
</div>

<br> <br> <br> <br>
<!-- Share per issue type -->
<div id="${share_per_component_type_dashboard}">
  <h2 style="float: left; width: 320px;" title="Click on any points to get to the related JIRA page" class="tooltip">
    Share per component type</h2>
  <div class="clear"></div>
  <div id="${share_per_component_type_range_type_box}" class="range_type_box push_box"></div>
  <div id="${share_per_component_type_project_box}" class="project_box"></div>
  <div class="clear"></div>
  <div id="${share_per_component_type_range_box}" class="range_label_box "></div>
  <div class="clear"></div>
  <div id="${share_per_component_type_chart}"></div>
  <div id="${share_per_component_type_table}" class="table"></div>
</div>

<!-- Share per issue type history -->
<div id="${share_per_component_type_history_dashboard}">
  <h2 style="float: left; width: 370px;" title="Click on any points to get to the related JIRA page" class="tooltip">
    Share per component type history</h2>
  <div class="clear"></div>
  <div id="${share_per_component_type_history_range_type_box}" class="range_type_box push_box_further"></div>
  <div id="${share_per_component_type_history_project_box}" class="project_box"></div>
  <div class="clear"></div>
  <div id="${share_per_component_type_history_chart}" class="chart"></div>
  <div id="${share_per_component_type_history_table}" class="table"></div>
  <br> <br>
</div>

<script type="text/javascript" src="${navigationjs}"></script>

</body>
</html>