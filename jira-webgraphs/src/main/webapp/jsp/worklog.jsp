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
	<!-- From https://www.creativejuiz.fr/blog/tutoriels/creer-menu-sticky-avec-javascript-css#sticky-back -->
	<spring:url value="/resources/js/navigation-bar.js" var="navigationjs"/>
	<spring:url value="/resources/js/piechart.js" var="piechartjs"/>
	<spring:url value="/resources/js/areagraphhistory.js" var="areagraphhistoryjs"/>
	<link type="text/css" rel="stylesheet" href="${graphcss}"/>
	<link type="text/css" rel="stylesheet" href="${awesomefontscss}"/>
	<script type="text/javascript" src="${piechartjs}"></script>
	<script type="text/javascript" src="${areagraphhistoryjs}"></script>
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

			share_per_category_control = drawPieChartPerCategory();
			share_per_category_history_control = drawChartSharePerCategoryHistory();
			share_per_component_type_control = drawPieChartPerComponent();
			share_per_component_type_history_control = drawChartSharePerComponentHistory();
			share_per_epic_type_control = drawPieChartPerEpic();
			share_per_epic_type_history_control = drawAreaGraphPerEpicHistory();

			share_per_category_control.setState({'selectedValues': [sessionStorage.selectedProject]});
			share_per_category_history_control.setState({'selectedValues': [sessionStorage.selectedProject]});
			share_per_component_type_control.setState({'selectedValues': [sessionStorage.selectedProject]});
			share_per_component_type_history_control.setState({'selectedValues': [sessionStorage.selectedProject]});
			share_per_epic_type_control.setState({'selectedValues': [sessionStorage.selectedProject]});
			share_per_epic_type_history_control.setState({'selectedValues': [sessionStorage.selectedProject]});

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

		function drawPieChartPerCategory() {

			control = drawPieChart(${share_per_category}, '${share_per_category_dashboard}',
					'${share_per_category_chart}',
					'${share_per_category_project_box}',
					'${share_per_category_range_type_box}',
					'${share_per_category_range_box}',
					'${share_per_category_table}');
			return control;
		}

		function drawChartSharePerCategoryHistory() {

			control = drawAreaChartHistory(${share_per_category_history}, '${share_per_category_history_dashboard}',
					'${share_per_category_history_chart}',
					'${share_per_category_history_project_box}',
					'${share_per_category_history_range_type_box}',
				'${share_per_category_history_category_name_box}',
				'${share_per_category_history_table}');
			return control;
		}

		// Share per issue type
		function drawPieChartPerComponent() {

			control = drawPieChart(${share_per_component_type}, '${share_per_component_type_dashboard}',
					'${share_per_component_type_chart}',
					'${share_per_component_type_project_box}',
					'${share_per_component_type_range_type_box}',
					'${share_per_component_type_range_box}',
					'${share_per_component_type_table}');
			return control;
		}

		// Share per issue type history
		function drawChartSharePerComponentHistory() {

			control = drawAreaChartHistory(${share_per_component_type_history}, '${share_per_component_type_history_dashboard}',
					'${share_per_component_type_history_chart}',
					'${share_per_component_type_history_project_box}',
					'${share_per_component_type_history_range_type_box}',
				'${share_per_component_type_history_component_name_box}',
				'${share_per_component_type_history_table}');
			return control;
		}

		// Share per issue type
		function drawPieChartPerEpic() {

			control = drawPieChart(${share_per_epic_type}, '${share_per_epic_type_dashboard}',
					'${share_per_epic_type_chart}',
					'${share_per_epic_type_project_box}',
					'${share_per_epic_type_range_type_box}',
					'${share_per_epic_type_range_box}',
					'${share_per_epic_type_table}');
			return control;
		}

		// Share per issue type history
		function drawAreaGraphPerEpicHistory() {

			control = drawAreaChartHistory(${share_per_epic_type_history}, '${share_per_epic_type_history_dashboard}',
					'${share_per_epic_type_history_chart}',
					'${share_per_epic_type_history_project_box}',
					'${share_per_epic_type_history_range_type_box}',
					'${share_per_epic_type_history_epic_name_box}',
					'${share_per_epic_type_history_table}');
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
			<p class="bigtxt txtcenter">Worklog Charts</p>
		</div>
	</section>
</div>

<br> <br> <br> <br>
<!-- Share per issue type -->
<div id="${share_per_category_dashboard}">
	<h2 style="float: left; width: 320px;" title="Click on any points to get to the related JIRA page" class="tooltip">
		Share per category in working days</h2>
	<div class="clear"></div>
	<div class="card">
		<p class="text">Repartition of tasks per main categories</p>
		<p class="text2">Unit is in work days</p>
	</div>
	<div id="${share_per_category_range_type_box}" class="range_type_box push_box"></div>
	<div id="${share_per_category_project_box}" class="project_box"></div>
	<div class="clear"></div>
	<div id="${share_per_category_range_box}" class="range_label_box "></div>
	<div class="clear"></div>
	<div id="${share_per_category_chart}"></div>
	<div id="${share_per_category_table}" class="table"></div>
</div>

<!-- Share per issue type -->
<div id="${share_per_category_history_dashboard}">
	<h2 style="float: left; width: 320px;" title="Click on any points to get to the related JIRA page" class="tooltip">
		Share per category history in working days</h2>
	<div class="clear"></div>
	<div class="card">
		<p class="text">History of the repartition of tasks per main categories</p>
		<p class="text2">Unit is in work days</p>
	</div>
	<div id="${share_per_category_history_range_type_box}" class="range_type_box push_box"></div>
	<div id="${share_per_category_history_project_box}" class="project_box"></div>
	<div class="clear"></div>
	<div id="${share_per_category_history_category_name_box}"  class="range_label_box "></div>
	<div class="clear"></div>
	<div id="${share_per_category_history_chart}"></div>
	<div id="${share_per_category_history_table}" class="table"></div>
</div>

<!-- Share per issue type -->
<div id="${share_per_component_type_dashboard}">
	<h2 style="float: left; width: 320px;" title="Click on any points to get to the related JIRA page" class="tooltip">
		Share per component type in working days</h2>
	<div class="clear"></div>
	<div class="card">
		<p class="text">Repartition of tasks per components</p>
		<p class="text2">Unit is in work days</p>
	</div>
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
		Share per component type history in working days</h2>
	<div class="clear"></div>
	<div class="card">
		<p class="text">History of the repartition of tasks per components</p>
		<p class="text2">Unit is in work days</p>
	</div>
	<div id="${share_per_component_type_history_range_type_box}" class="range_type_box push_box"></div>
	<div id="${share_per_component_type_history_project_box}" class="project_box"></div>
	<div class="clear"></div>
	<div id="${share_per_component_type_history_component_name_box}"  class="range_label_box "></div>
	<div class="clear"></div>
	<div id="${share_per_component_type_history_chart}" ></div>
	<div id="${share_per_component_type_history_table}" class="table"></div>
</div>

<!-- Share per issue type -->
<div id="${share_per_epic_type_dashboard}">
	<h2 style="float: left; width: 320px;" title="Click on any points to get to the related JIRA page" class="tooltip">
		Share per epic type in working days</h2>
	<div class="clear"></div>
	<div class="card">
		<p class="text">Repartition of tasks per epics</p>
		<p class="text2">Unit is in work days</p>
	</div>
	<div id="${share_per_epic_type_range_type_box}" class="range_type_box push_box"></div>
	<div id="${share_per_epic_type_project_box}" class="project_box"></div>
	<div class="clear"></div>
	<div id="${share_per_epic_type_range_box}" class="range_label_box "></div>
	<div class="clear"></div>
	<div id="${share_per_epic_type_chart}"></div>
	<div id="${share_per_epic_type_table}" class="table"></div>
</div>

<!-- Share per issue type history -->
<div id="${share_per_epic_type_history_dashboard}">
	<h2 style="float: left; width: 370px;" title="Click on any points to get to the related JIRA page" class="tooltip">
		Share per epic type history in working days</h2>
	<div class="clear"></div>
	<div class="card">
		<p class="text">History of the repartition of tasks per epics</p>
		<p class="text2">Unit is in work days</p>
	</div>
	<div id="${share_per_epic_type_history_range_type_box}" class="range_type_box push_box"></div>
	<div id="${share_per_epic_type_history_project_box}" class="project_box"></div>
	<div class="clear"></div>
	<div id="${share_per_epic_type_history_epic_name_box}"  class="range_label_box "></div>
	<div class="clear"></div>
	<div id="${share_per_epic_type_history_chart}" ></div>
	<div id="${share_per_epic_type_history_table}" class="table"></div>
</div>

<script type="text/javascript" src="${navigationjs}"></script>

</body>
</html>