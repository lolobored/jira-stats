<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="Cache-control" content="public">
	<title>Jira Statistics</title>
	<spring:url value="/resources/css/graph.css" var="graphcss"/>
	<!-- From https://www.creativejuiz.fr/blog/tutoriels/creer-menu-sticky-avec-javascript-css#sticky-back -->
	<spring:url value="/resources/js/navigation-bar.js" var="navigationjs"/>
	<spring:url value="/resources/js/teamlog.js" var="teamlogjs"/>
	<link type="text/css" rel="stylesheet" href="${graphcss}"/>
	<script type="text/javascript" src="${teamlogjs}"></script>
	<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>

	<!-- Create all dashboards and set control states -->
	<script type="text/javascript">

		// Load the Visualization API and the chart package.
		google.charts.load('current', {
			'packages': ['corechart', 'bar', 'table', 'controls', 'annotationchart','calendar']
		});

		// Set a callback to run when the Google Visualization API is loaded.
		google.charts.setOnLoadCallback(initCharts);

		function initCharts() {

			team_worklog_control = drawChartCalendarPerResource();
			
			team_worklog_control.setState({'selectedValues': [sessionStorage.selectedProject]});

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


		// draw calendar
		function drawChartCalendarPerResource() {

			control = drawCalendarPerResource(${team_worklog}, '${team_worklog_dashboard}',
					'${team_worklog_chart}',
					'${team_worklog_project_box}',
					'${team_worklog_resource_box}',
					'${team_worklog_table}');
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
					<li><a href="teamlog">Teamlog</a></li>
				</ul>
			</nav>

		</div>
	</header>
	<section id="section" class="main-section first-section">
		<div class="container">
			<p class="bigtxt txtcenter">Teamlog Charts</p>
		</div>
	</section>
</div>

<br> <br> <br> <br>
<!-- Share per issue type -->
<div id="${team_worklog_dashboard}">
	<h2 style="float: left; width: 320px;" title="Click on any points to get to the related JIRA page" class="tooltip">
		Team log in hours</h2>
	<div class="clear"></div>
	<div id="${team_worklog_project_box}" class="range_type_box push_box"></div>
	<div id="${team_worklog_resource_box}" class="resource_box "></div>
	<div class="clear"></div>
	<div id="${team_worklog_chart}"></div>
	<div id="${team_worklog_table}" class="table"></div>
</div>

<script type="text/javascript" src="${navigationjs}"></script>

</body>
</html>