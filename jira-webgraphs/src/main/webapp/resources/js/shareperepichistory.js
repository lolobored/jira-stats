/*****************************************************************************************************************************************************
 * Main function Responsible to draw the share per type history graph parameters required are:
 *
 * @param {type}
 *          header : a list representing the column names
 * @param {type}
 *          data : a list representing the column names
 * @param {type}
 *          dashboard_name_div : the div name for the dashboard
 * @param {type}
 *          chart_div : the div name for the chart
 * @param {type}
 *          project_div : the div name for the project combobox
 * @param {type}
 *          range_type_div : the div name for the range type combobox
 * @returns {undefined}
 */
function drawSharePerEpicHistory(jsonData, dashboard_name_div, chart_div, project_range_div, range_type_div, epic_name_div, chart_table_div) {

	// The data we will be working on look like this:
	// ['Range Label', 'Range Type', 'Project', 'Bug', 'QA Task', 'Improvement', 'Kaizen', 'QA Task', 'Spike', 'Story', 'Task',
	// 'Task Time Bucket', 'Task with QA', 'Zephir Task', 'Bug Jira Search', 'QA Task Jira Search', 'Improvement Jira Search', 'Kaizen Jira Search', 'QA
	// Task Jira Search', 'Spike Jira Search', 'Story Jira Search', 'Task Jira Search', 'Task Time Bucket Jira Search', 'Task with QA Jira Search',
	// 'Zephir Task Jira Search' ]
	// [ 'February 2016', 'Month', 'GTFRame', 181020, 122000, 10000, 15000, 36000, 45754, 13156, 1234, 23456, 1236,
	// 12546, 'jiraSearchUrl', 'jiraSearchUrl', 'jiraSearchUrl', 'jiraSearchUrl', 'jiraSearchUrl', 'jiraSearchUrl', 'jiraSearchUrl', 'jiraSearchUrl',
	// 'jiraSearchUrl', 'jiraSearchUrl', 'jiraSearchUrl']
	// Where jiraSearchUrl is something like
	// 'https://jira.us-bottomline.root.bottomline.com/issues/?jql=key%20in(GTFRM-4046,GTFRM-3995,GTFRM-3981,GTFRM-3939,GTFRM-3938,GTFRM-3807)&maxResults=500'

	// set up columns number
	var range_column = 0;
	var range_type_column = 1;
	var project_range_column = 2;
	var epic_name_column = 3;
	var time_spent_column = 4;
	var jira_search_column = 5;
	var colors = [
		'#3366CC',
		'#DC3912',
		'#FF9900',
		'#109618',
		'#990099',
		'#0099C6',
		'#DD4477',
		'#66AA00',
		'#268f8f',
		'#D1BA53'
	];


	// will be the table we'll use to init the chart
	var chartDataTable;
	var view;
	var filteredData;
	var groupedData;

	// now create the google chart datatable against the json data
	chartDataTable = new google.visualization.DataTable(jsonData);

	// Create a dashboard with the 3 common combobox (project, range type and range selection)
	var dashboard = new google.visualization.Dashboard(document.getElementById(dashboard_name_div));

	// First one is the project combobox
	var projectBox = new google.visualization.ControlWrapper({
		'controlType': 'CategoryFilter',
		'containerId': project_range_div,
		'options': {
			'filterColumnIndex': project_range_column,
			'ui': {
				'label': '',
				'labelStacking': 'vertical',
				'allowTyping': false,
				'allowMultiple': false,
				'allowNone': false,
				'sortValues': false
			}
		}
	});

	// Then it's the range type
	var rangeTypeBox = new google.visualization.ControlWrapper({
		'controlType': 'CategoryFilter',
		'containerId': range_type_div,
		'options': {
			'filterColumnIndex': range_type_column,
			'values': ['Month', 'Quarter', 'Sprint'],
			'ui': {
				'label': '',
				'labelStacking': 'vertical',
				'allowTyping': false,
				'allowMultiple': false,
				'allowNone': false,
				'sortValues': false
			}
		}
	});

	// then it's the component name
	var epicBox = new google.visualization.ControlWrapper({
		'controlType': 'CategoryFilter',
		'containerId': epic_name_div,
		'options': {
			'filterColumnIndex': epic_name_column,
			'ui': {
				'label': '',
				'labelStacking': 'vertical',
				'allowTyping': false,
				'allowMultiple': true,
				'allowNone': true,
				'sortValues': true
			}
		}
	});

	// Now for the chart
	// we want to have a chart here
	var chart = new google.visualization.ChartWrapper({
		'chartType': 'AreaChart',
		'containerId': chart_div,
		'options': {
			'chartArea': {
				top: 30,
				bottom: 100,
				height: '80%'
			},
			'legend': {
				position: 'right',
				alignment: 'center'
			},
			'tooltip': {
				showColorCode: true,
				isHtml: true
			},
			colors: colors,
			// Group selections
			// by x-value.
			'isStacked': 'percent',
			'category': 'category',
			'width': 1200,
			'height': 700
		}
	});

	// Define a non-display table used only to trigger the datatable changes
	var table = new google.visualization.ChartWrapper({
		'chartType': 'Table',
		'containerId': chart_table_div,
	});

	// Establish dependencies, so that every time we change one combobox we update the others and drow dashboard
	dashboard.bind(projectBox, rangeTypeBox);
	dashboard.bind(rangeTypeBox, epicBox);
	dashboard.bind(epicBox, table);
	dashboard.draw(chartDataTable);
	var initialData = table.getDataTable();

	// Refresh the chart when a control has been used
	google.visualization.events.addListener(table, 'ready', function () {

		var otherColumnName = "Others";
		currentData = table.getDataTable();
		filteredData = currentData;

		var selectedValues = epicBox.getState()['selectedValues'];
		// if no value is selected then consider
		// every values are selected
		if (selectedValues.length === 0) {
			// explore every row to get eventual epics
			for (var i = 0; i < currentData.getNumberOfRows(); i++) {
				var epicName = currentData.getValue(i, epic_name_column);
				alreadyExists = false;
				for (var alreadyExistingEpic = 0; alreadyExistingEpic < selectedValues.length; alreadyExistingEpic++) {
					if (selectedValues[alreadyExistingEpic] === epicName) {
						alreadyExists = true;
						break;
					}
				}
				if (!alreadyExists) {
					selectedValues.push(epicName);
				}
			}
		}
		var selectedTypeRange = rangeTypeBox.getState()['selectedValues'][0];
		var selectedProject = projectBox.getState()['selectedValues'][0];
		var found = false;

		filteredData = new google.visualization.DataTable();
		// copy columns
		for (var i = 0; i < currentData.getNumberOfColumns(); i++) {
			filteredData.addColumn(currentData.getColumnType(i), currentData.getColumnLabel(i))
		}

		// create header
		var header = [];
		var possibleColumns = [];
		var alreadyExists;
		var newTable = new google.visualization.DataTable();

		// copy first 3 columns (range, range type and project)
		for (var singleColumn = 0; singleColumn < 3; singleColumn++) {
			newTable.addColumn(filteredData.getColumnType(singleColumn), filteredData.getColumnLabel(singleColumn));
		}
		// explore every row to get eventual epics
		for (var i = 0; i < currentData.getNumberOfRows(); i++) {
			var epicName = currentData.getValue(i, epic_name_column);
			alreadyExists = false;
			for (var alreadyExistingColumn = 0; alreadyExistingColumn < possibleColumns.length; alreadyExistingColumn++) {
				if (possibleColumns[alreadyExistingColumn] === epicName) {
					alreadyExists = true;
					break;
				}
			}
			if (!alreadyExists) {
				newTable.addColumn('number', epicName);
				newTable.addColumn('string', epicName + " jira search");
				possibleColumns.push(epicName);
			}
		}
		// push others
		newTable.addColumn('number', otherColumnName);
		newTable.addColumn('string', otherColumnName + " jira search");

		// create rows
		// each row is a range label
		for (var i = 0; i < currentData.getNumberOfRows(); i++) {
			if (selectedTypeRange === currentData.getValue(i, range_type_column)) {

				var range = currentData.getValue(i, range_column);
				var found = false;
				// chack the range is not already added
				for (var j = 0; j < newTable.getNumberOfRows(); j++) {
					if (newTable.getValue(j, range_column) === range) {
						found = true;
						break;
					}
				}
				// create a new line
				if (found !== true) {

					var rowNumber = newTable.addRow();
					// create range, project and range type
					for (var column = 0;
							 column < 3;
							 column++) {

						newTable.setValue(rowNumber, column, currentData.getValue(i, column));
					}
					// create empty values
					for (var column = 3;
							 column < newTable.getNumberOfColumns();
							 column++) {
						if (newTable.getColumnType(column) === 'number') {
							newTable.setValue(rowNumber, column, 0);
						}
						else {
							newTable.setValue(rowNumber, column, '');
						}
					}

				}
			}
		}

		// table is built
		// we now need to fill it
		for (var i = 0; i < chartDataTable.getNumberOfRows(); i++) {
			var found = false;
			for (var j = 0; j < selectedValues.length; j++) {

				// check first if the row
				// - component is the same as one of those selected
				// - range type is the same
				// - project is the same
				if (selectedValues[j] === chartDataTable.getValue(i, epic_name_column)
					&& selectedTypeRange === chartDataTable.getValue(i, range_type_column)
					&& selectedProject === chartDataTable.getValue(i, project_range_column)) {
					for (var rowNewTable = 0;
							 rowNewTable < newTable.getNumberOfRows();
							 rowNewTable++) {
						// if the line for the range is the correct one
						if (chartDataTable.getValue(i, range_column) === newTable.getValue(rowNewTable, range_column)) {
							for (var column = 0;
									 column < newTable.getNumberOfColumns();
									 column++) {
								if (newTable.getColumnLabel(column) === chartDataTable.getValue(i, epic_name_column)) {
									newTable.setValue(rowNewTable, column, chartDataTable.getValue(i, time_spent_column));
									// set the jira search
									newTable.setValue(rowNewTable, column + 1, chartDataTable.getValue(i, time_spent_column + 1));
									found = true;
									break;
								}
							}
							if (found === true) {
								break;
							}
						}
					}
				}

				if (found === true) {
					break;
				}
			}

			// if not found add it to the "Others"
			if (found === false) {
				for (var rowNewTable = 0;
						 rowNewTable < newTable.getNumberOfRows();
						 rowNewTable++) {
					// if the line for the range is the correct one
					if (chartDataTable.getValue(i, range_column) === newTable.getValue(rowNewTable, range_column)
						&& selectedTypeRange === chartDataTable.getValue(i, range_type_column)
						&& selectedProject === chartDataTable.getValue(i, project_range_column)) {
						var time = newTable.getValue(rowNewTable, newTable.getNumberOfColumns() - 2);
						time = time + chartDataTable.getValue(i, time_spent_column);
						newTable.setValue(rowNewTable, newTable.getNumberOfColumns() - 2, time);
						// get the jira search
						var jiraSearch=newTable.getValue(rowNewTable, newTable.getNumberOfColumns() - 1);
						if (jiraSearch === ''){
							newTable.setValue(rowNewTable, newTable.getNumberOfColumns() - 1, chartDataTable.getValue(i, jira_search_column));
						}
						else{
							var existing = chartDataTable.getValue(i, jira_search_column);
							var jiraSearches= [];
							jiraSearches.push(existing);
							jiraSearches.push(jiraSearch);
							newTable.setValue(rowNewTable, newTable.getNumberOfColumns() - 1,
																getJiraUrls(jiraSearches));
						}
						break;
					}

				}
			}
		}

		var viewColumns = [0];
		// we want to get the time spent only
		for (var i = 3; i < newTable.getNumberOfColumns(); i++) {
			if (!newTable.getColumnLabel(i).endsWith(" jira search")) {
				viewColumns.push(i);
			}
		}

		var view = new google.visualization.DataView(newTable);
		view.setColumns(viewColumns);

		// Draw chart
		chart.setDataTable(newTable);
		chart.setView(view.toJSON());

		chart.draw();
	});

	// Now create the listeners first one is triggered when we select a pie so that we get redirection to the Jira URL
	var selectListener = function () {

		// get the selection
		var selection = chart.getChart().getSelection();
		var currentFilteredData = chart.getDataTable();
		// position is:
		// 1+ selection[0].column * 2 + 1
		// 1 becaue we start the view with a label
		// then selection[0].column * 2 because the view has only the numbers
		// no jira search in it
		// +1 to get the jira search
		if (currentFilteredData.getColumnLabel(1 + selection[0].column * 2 + 1).endsWith(" jira search")) {
			window.open(currentFilteredData.getValue(selection[0].row, 1 + selection[0].column * 2 + 1), '_blank');
		}

	};

	// Add the selection listener to the chart
	google.visualization.events.addListener(chart, 'select', selectListener);

	return projectBox;
}

/*****************************************************************************************************************************************************
 * Returns the appropriate jira search It will use the jira url start and close as well as the list of issues
 */
function getJiraUrls(issues) {
	var concat = "";
	for (var i = 0; i < issues.length; i++) {
		var startInKeys = issues[i].indexOf("in(") + "in(".length;
		var closeInKeys = issues[i].indexOf(")", startInKeys);
		if (i === 0) {
			var jiraStartSearchUrl = issues[i].slice(0, startInKeys);
			var jiraClosingSearchUrl = issues[i].slice(closeInKeys);
			concat += jiraStartSearchUrl;
		}
		if (i !== 0) {
			concat += ",";
		}
		concat += issues[i].slice(startInKeys, closeInKeys);
	}
	concat += jiraClosingSearchUrl;
	return concat;
}