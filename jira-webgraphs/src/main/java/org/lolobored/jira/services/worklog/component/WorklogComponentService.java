package org.lolobored.jira.services.worklog.component;

import org.lolobored.jira.dao.data.DAOTable;

public interface WorklogComponentService {

  DAOTable getSharedLoggedTimePerComponent();

}
