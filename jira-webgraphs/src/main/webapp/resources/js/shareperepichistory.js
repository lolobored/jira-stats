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
function drawSharePerEpicHistory(jsonData, dashboard_name_div, chart_div, project_range_div, range_type_div, chart_table_div) {

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
    var epic_type_column = 3;
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
            'width': 1050,
            'height': 510
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

        // push the lines into columns
        var newTable = [];
        var header = [];
        var possibleColumns = [];
        var alreadyExists;
        // copy first 3 columns
        for (var singleColumn = 0; singleColumn < 3; singleColumn++) {
            header.push(filteredData.getColumnLabel(singleColumn));
        }
        // then we'll need to get every single possible columns by browsing
        // values in the 'Issue Type' column
        for (var singleLine = 0; singleLine < filteredData.getNumberOfRows(); singleLine++) {
            var issueType = filteredData.getValue(singleLine, epic_type_column);
            alreadyExists = false;
            for (var alreadyExistingColumn = 0; alreadyExistingColumn < possibleColumns.length; alreadyExistingColumn++) {
                if (possibleColumns[alreadyExistingColumn] === issueType) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                possibleColumns.push(issueType);
            }
        }

        // fill header
        for (var i = 0; i < possibleColumns.length; i++) {
            header.push(possibleColumns[i]);
            // add jira search
            header.push(possibleColumns[i] + " jira search");
        }

        newTable.push(header);

        var savedProcessed = "";
        var newLine = [];

        var savedDetailed;
        // processing new lines and trying to insert it into the right row
        for (var singleLine = 0; singleLine < filteredData.getNumberOfRows(); singleLine++) {
            var issueType = filteredData.getValue(singleLine, epic_type_column);
            var timeSpent = filteredData.getValue(singleLine, time_spent_column);
            var jiraSearch = filteredData.getValue(singleLine, jira_search_column);
            // checking product + range type + range name is the same as the one before
            // if not we will need to insert the row
            var currentProcess = filteredData.getValue(singleLine, range_column) +
                filteredData.getValue(singleLine, range_type_column) +
                filteredData.getValue(singleLine, project_range_column) ;
            if (currentProcess !== savedProcessed) {
                if (singleLine !== 0) {
                    // push the details or standard we saved
                    newLine[newLine.length - 1] = savedDetailed;
                    found = false;
                    for (var lineNb = 0; lineNb < newTable.length; lineNb++) {
                        // if we already have something which as the same product
                        // range
                        // range label
                        if (newLine[0] === newTable[lineNb][0] && newLine[1] === newTable[lineNb][1] && newLine[2] === newTable[lineNb][2] && newLine[newLine.length - 1] === newTable[lineNb][newLine.length - 1]) {
                            found = true;
                            // we can merge the values here
                            for (var j = 4; j < newLine.length; j++) {
                                if (header[j].endsWith(" jira search") && newLine[j] !== "") {
                                    newTable[lineNb][j] = newLine[j];
                                } else if (!header[j].endsWith(" jira search") && newLine[j] !== 0) {
                                    newTable[lineNb][j] = newLine[j];
                                }
                            }
                            break;
                        }
                    }
                    if (!found) {
                        newTable.push(newLine);
                    }
                }
                newLine = [];
                // init new line
                for (var i = 0; i < header.length; i++) {
                    if (header[i].endsWith(" jira search")) {
                        newLine.push('');
                    } else {
                        newLine.push(0);
                    }
                }
                newLine[0] = filteredData.getValue(singleLine, range_column);
                newLine[1] = filteredData.getValue(singleLine, range_type_column);
                newLine[2] = filteredData.getValue(singleLine, project_range_column);

            }

            for (var i = 0; i < header.length; i++) {
                if (header[i] === issueType) {
                    newLine[i] = timeSpent;
                    newLine[i + 1] = jiraSearch;
                    break;
                }
            }

            savedProcessed = currentProcess;
        }

        // we still need to insert the new line
        // push the details or standard we saved
        newLine[newLine.length - 1] = savedDetailed;
        found = false;
        for (var lineNb = 0; lineNb < newTable.length; lineNb++) {
            // if we already have something which as the same product
            // range
            // range label
            if (newLine[0] === newTable[lineNb][0] && newLine[1] === newTable[lineNb][1] && newLine[2] === newTable[lineNb][2] && newLine[newLine.length - 1] === newTable[lineNb][newLine.length - 1]) {
                found = true;
                // we can merge the values here
                for (var j = 4; j < newLine.length; j++) {
                    if (header[j].endsWith(" jira search") && newLine[j] !== "") {
                        newTable[lineNb][j] = newLine[j];
                    } else if (!header[j].endsWith(" jira search") && newLine[j] !== 0) {
                        newTable[lineNb][j] = newLine[j];
                    }
                }
                break;
            }
        }
        if (!found) {
            newTable.push(newLine);
        }

        var viewColumns = [0];
        // we want to get the time spent only
        for (var i = 3; i < header.length - 1; i++) {
            if (!header[i].endsWith(" jira search")) {
                viewColumns.push(i);
            }
        }

        // apply the filter to the view
        var dataTable = google.visualization.arrayToDataTable(newTable);

        var view = new google.visualization.DataView(dataTable);
        view.setColumns(viewColumns);

        // Draw chart
        chart.setDataTable(dataTable);
        chart.setView(view.toJSON());

        chart.draw();
    });

    // Now create the listeners first one is triggered when we select a pie so that we get redirection to the Jira URL
    var selectListener = function() {

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