package org.lolobored.jira.services.worklog.category;

import org.lolobored.jira.dao.data.DAOTable;

public interface WorklogCategoryService {

  DAOTable getSharedLoggedTimePerCategory();

}
