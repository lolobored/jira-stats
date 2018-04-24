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
function drawLineChart(jsonData, dashboard_name_div, chart_div, project_range_div, range_type_div, chart_table_div) {


	// set up columns number
	var range_column = 0;
	var range_type_column = 1;
	var project_range_column = 2;
	var component_type_column = 3;
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
		'#66AA00'
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

	// Now for the chart
	// we want to have a chart here
	// Now for the chart
	// we want to have a chart here
	var chart = new google.visualization.ChartWrapper({
		'chartType': 'LineChart',
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
	dashboard.bind(rangeTypeBox, table);
	dashboard.draw(chartDataTable);

	// Refresh the chart when a control has been used
	google.visualization.events.addListener(table, 'ready', function() {

		filteredData = table.getDataTable();

		var viewColumns = [0];
		// we want to get the COUNT only
		for (var column = 3; column < filteredData.getNumberOfColumns() - 1; column++) {
			if (!filteredData.getColumnLabel(column).endsWith(" jira search")) {
				// check there is at least one entry in there
				// 0 entries would be other projects
				for (var row = 0; row < filteredData.getNumberOfRows(); row++) {
					if (filteredData.getValue(row, column) !== 0){
						viewColumns.push(column);
						break;
					}
				}

			}
		}

		// apply the filter to the view
		var view = new google.visualization.DataView(filteredData);
		view.setColumns(viewColumns);

		// Draw chart
		chart.setDataTable(filteredData);
		chart.setView(view.toJSON());

		chart.draw();
	});

	// Now create the listeners first one is triggered when we select a pie so that we get redirection to the Jira URL
	var selectListener = function() {

		// get the selection
		var selection = chart.getChart().getSelection();
		var currentFilteredData = chart.getDataTable();
		var count=0;
		// we want to get the COUNT only
		for (var column = 3; column < filteredData.getNumberOfColumns() ; column++) {
			if (!filteredData.getColumnLabel(column).endsWith(" jira search")) {
				// check there is at least one entry in there
				// 0 entries would be other projects
					if (filteredData.getValue(selection[0].row, column) !== 0){
						count++;
					}
			}
			else{
				if (count ===selection[0].column){
					window.open(currentFilteredData.getValue(selection[0].row, column), '_blank');
					break;
				}
			}
		}

	};

	// Add the selection listener to the chart
	google.visualization.events.addListener(chart, 'select', selectListener);

	return projectBox;
}