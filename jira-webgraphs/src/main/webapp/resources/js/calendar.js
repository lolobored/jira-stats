/*****************************************************************************************************************************************************
 * Main function Responsible to draw the share per type pie parameters required are:
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
 * @param {type}
 *          resource_div : the div name for the range combobox
 * @returns {undefined}
 */
function drawCalendarPerResource(jsonData, dashboard_name_div, chart_div, project_resource_div, resource_div,
                          chart_table_div) {

    // The data we will be working on look like this:
    // ['Range Label', 'Range Type', 'Project', 'Issue Type', 'Time spent', 'Jira Search'],
    // [ 'Shark 2', 'Sprints', 'GTFrame', 'Bug', 181020,
    // 'https://jira.us-bottomline.root.bottomline.com/issues/?jql=key%20in(GTFRM-4046,GTFRM-3995,GTFRM-3981,GTFRM-3939,GTFRM-3938,GTFRM-3807)&maxResults=500'],
    // [ 'April 2015', 'Monthly', 'GTFrame', 'Bug', 181020,
    // 'https://jira.us-bottomline.root.bottomline.com/issues/?jql=key%20in(GTFRM-4046,GTFRM-3995,GTFRM-3981,GTFRM-3939,GTFRM-3938,GTFRM-3807)&maxResults=500'],

    // set up columns number
    var resource_column = 2;
    var project_range_column = 3;
    var time_spent_jira_search_column = 5;

    // will be the table we'll use to init the chart
    var calendarDataTable;
    var groupedData;

    // now create the google chart datatable against the json data
    calendarDataTable = new google.visualization.DataTable(jsonData);

    // Create a dashboard with the 3 common combobox (project, range type and range selection)
    var dashboard = new google.visualization.Dashboard(document.getElementById(dashboard_name_div));

    // Now create the combo boxes
    //
    //

    // First one is the project combobox
    var projectBox = new google.visualization.ControlWrapper({
        'controlType': 'CategoryFilter',
        'containerId': project_resource_div,
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
    
    // Finally the range list
    var resourceListBox = new google.visualization.ControlWrapper({
        'controlType': 'CategoryFilter',
        'containerId': resource_div,
        'options': {
            'filterColumnIndex': resource_column,
            'ui': {
                'label': '',
                'labelStacking': 'vertical',
                'allowTyping': false,
                'allowMultiple': false,
                'allowNone': true,
                'sortValues': true
            }
        }
    });

    // Now for the chart
    // we want to have a chart here
    var chart = new google.visualization.ChartWrapper({
        'chartType': 'Calendar',
        'containerId': chart_div,
			 options : {
				title: 'Log',
				height: 350,
				calendar: {
					cellColor: {
						stroke: '#76a7fa',
						strokeOpacity: 0.5,
						strokeWidth: 1,
					}
				}
			}
    });

    // Define a non-display table used only to trigger the datatable changes
    var table = new google.visualization.ChartWrapper({
        'chartType': 'Table',
        'containerId': chart_table_div,
    });

    // Establish dependencies, so that every time we change one combobox we update the others and drow dashboard
    dashboard.bind(projectBox, resourceListBox);
    dashboard.bind(resourceListBox, table);
		dashboard.draw(calendarDataTable);

    // Refresh the chart when a control has been used
    google.visualization.events.addListener(table, 'ready', function() {

			groupedData = table.getDataTable();
			var view = new google.visualization.DataView(groupedData)
			 view.setColumns([0, 1]);
			 chart.setView(view);
        // Draw chart
        chart.setDataTable(view);
        chart.setView(view);
        chart.draw();
    });

    // Now create the listeners first one is triggered when we select a pie so that we get redirection to the Jira URL
    var selectListener = function(e) {

        // get the selection
        var selection = chart.getChart().getSelection();
        // we should have only one but still
        for (var i = 0; i < selection.length; i++) {
            var item = selection[i];
            // if we got something
            if (item.row != null) {
                // we need to get the list of jiras
                window.open(groupedData.getValue(item.row, 4), '_blank');
            }
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