package org.lolobored.jira.services.teamlog;

import org.lolobored.jira.dao.data.DAOTable;


public interface TeamlogService {

  DAOTable getLoggedTimePerTeamMember();

}
