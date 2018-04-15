package org.lolobored.jira.services.worklog;

import org.lolobored.jira.dao.data.DAOTable;

public interface WorklogService {

  DAOTable getSharedLoggedTimePerComponent();

}
