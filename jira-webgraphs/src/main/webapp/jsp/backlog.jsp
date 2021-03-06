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
	<spring:url value="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" var="awesomefontscss"/>
	<spring:url value="/resources/js/navigation-bar.js" var="navigationjs"/>
	<spring:url value="/resources/js/linechart.js" var="linechartjs"/>
	<link type="text/css" rel="stylesheet" href="${graphcss}"/>
	<link type="text/css" rel="stylesheet" href="${awesomefontscss}"/>
	<script type="text/javascript" src="${linechartjs}"></script>
	<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>

	<!-- Create all dashboards and set control states -->
	<script type="text/javascript">

		// Load the Visualization API and the chart package.
		google.charts.load('current', {
			'packages': ['corechart', 'bar', 'table', 'controls', 'annotationchart','line']
		});

		// Set a callback to run when the Google Visualization API is loaded.
		google.charts.setOnLoadCallback(initCharts);

		function initCharts() {

			backlog_per_component_control = drawLineChartPerComponent();
			backlog_opening_per_component_control = drawLineChartOpeningPerComponent();
			backlog_average_fix_time_per_component_control = drawLineChartAverageFixTimePerComponent();

			backlog_per_component_control.setState({'selectedValues': [sessionStorage.selectedProject]});
			backlog_opening_per_component_control.setState({'selectedValues': [sessionStorage.selectedProject]});
			backlog_average_fix_time_per_component_control.setState({'selectedValues': [sessionStorage.selectedProject]});

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


		// draw chart
		function drawLineChartPerComponent() {

			control = drawLineChart(${backlog__per_component}, '${backlog__per_component_dashboard}',
				'${backlog__per_component_chart}',
				'${backlog__per_component_project_box}',
				'${backlog__per_component_range_type_box}',
				'${backlog__per_component_table}');
			return control;
		}

		// draw chart
		function drawLineChartOpeningPerComponent() {

			control = drawLineChart(${backlog_opening_per_component}, '${backlog_opening_per_component_dashboard}',
				'${backlog_opening_per_component_chart}',
				'${backlog_opening_per_component_project_box}',
				'${backlog_opening_per_component_range_type_box}',
				'${backlog_opening_per_component_table}');
			return control;
		}

		// draw calendar
		function drawLineChartAverageFixTimePerComponent() {

			control = drawLineChart(${backlog_average_fix_per_component}, '${backlog_average_fix_per_component_dashboard}',
					'${backlog_average_fix_per_component_chart}',
					'${backlog_average_fix_per_component_project_box}',
					'${backlog_average_fix_per_component_range_type_box}',
					'${backlog_average_fix_per_component_table}');
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
					<li><a href="backlog">Backlog</a></li>
					<li><a href="teamlog">Teamlog</a></li>
				</ul>
			</nav>

		</div>
	</header>
	<section id="section" class="main-section first-section">
		<div class="container">
			<p class="bigtxt txtcenter">Backlog Charts</p>
		</div>
	</section>
</div>

<br> <br> <br> <br>
<!-- Backlog -->
<div id="${backlog__per_component_dashboard}">
	<h2 style="float: left; width: 320px;" title="Click on any points to get to the related JIRA page" class="tooltip">
		Bugs Backlog</h2>
	<div class="clear"></div>
	<div class="card">
		<p class="text">Follow the bug backlog</p>
		<p class="text2">Bugs are sorted by components for the project</p>
	</div>
	<div id="${backlog__per_component_project_box}" class="range_type_box push_box"></div>
	<div id="${backlog__per_component_range_type_box}" class="resource_box "></div>
	<div class="clear"></div>
	<div id="${backlog__per_component_chart}"></div>
	<div id="${backlog__per_component_table}" class="table"></div>
</div>
<!-- Opening backlog -->
<div id="${backlog_opening_per_component_dashboard}">
	<h2 style="float: left; width: 320px;" title="Click on any points to get to the related JIRA page" class="tooltip">
		Bugs Opening Per Component</h2>
	<div class="clear"></div>
	<div class="card">
		<p class="text">Follow the bug's opening per range</p>
		<p class="text2">Bugs are sorted by components for the project</p>
	</div>
	<div id="${backlog_opening_per_component_project_box}" class="range_type_box push_box"></div>
	<div id="${backlog_opening_per_component_range_type_box}" class="resource_box "></div>
	<div class="clear"></div>
	<div id="${backlog_opening_per_component_chart}"></div>
	<div id="${backlog_opening_per_component_table}" class="table"></div>
</div>
<!-- Average fix time -->
<div id="${backlog_average_fix_per_component_dashboard}">
	<h2 style="float: left; width: 320px;" title="Click on any points to get to the related JIRA page" class="tooltip">
		Average Fix Time Per Component (in working days)</h2>
	<div class="clear"></div>
	<div class="card">
		<p class="text">Follow the average fix time in work days for bugs</p>
		<p class="text2">Bugs are sorted by components for the project</p>
	</div>
	<div id="${backlog_average_fix_per_component_project_box}" class="range_type_box push_box"></div>
	<div id="${backlog_average_fix_per_component_range_type_box}" class="resource_box "></div>
	<div class="clear"></div>
	<div id="${backlog_average_fix_per_component_chart}"></div>
	<div id="${backlog_average_fix_per_component_table}" class="table"></div>
</div>

<script type="text/javascript" src="${navigationjs}"></script>

</body>
</html>