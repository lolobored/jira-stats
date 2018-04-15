<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta http-equiv="Cache-control" content="public">
  <title>Worklog statistics</title>
  <spring:url value="/resources/css/graph.css" var="graphcss"/>
  <!-- From https://www.creativejuiz.fr/blog/tutoriels/creer-menu-sticky-avec-javascript-css#sticky-back -->
  <spring:url value="/resources/js/navigation-bar.js" var="navigationjs"/>
  <spring:url value="/resources/js/sharepertype.js" var="sharepertypejs"/>

  <link type="text/css" rel="stylesheet" href="${graphcss}"/>
  <script type="text/javascript" src="${sharepertypejs}"></script>

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

          share_per_component_type_control = drawChartSharePerType();

      }

      // Share per issue type
      function drawChartSharePerType() {

          control = drawSharePerType(${share_per_component_type}, '${share_per_component_type_dashboard}',
              '${share_per_component_type_chart}',
              '${share_per_component_type_component_box}',
              '${share_per_component_type_range_type_box}',
              '${share_per_component_type_range_box}',
              '${share_per_component_type_table}');
          return control;
      }


  </script>

</head>

<div class="banner-body">
  <header id="header" role="banner" class="main-header">
    <div class="header-inner">

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
    Share per components</h2>
  <div id="${share_per_component_type_detailed_box}" class="detailed_box"></div>
  <div class="clear"></div>
  <div id="${share_per_component_type_range_type_box}" class="range_type_box push_box"></div>
  <div id="${share_per_component_type_project_box}" class="project_box"></div>
  <div class="clear"></div>
  <div id="${share_per_component_type_range_box}" class="range_label_box "></div>
  <div class="clear"></div>
  <div id="${share_per_component_type_chart}"></div>
  <div id="${share_per_component_type_table}" class="table"></div>
</div>
<script type="text/javascript" src="${navigationjs}"></script>

</body>
</html>