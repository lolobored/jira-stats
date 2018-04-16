package org.lolobored.jira.services.worklog.epic;

import org.lolobored.jira.dao.data.DAOTable;

public interface WorklogEpicService {

  DAOTable getSharedLoggedTimePerEpic();

}
