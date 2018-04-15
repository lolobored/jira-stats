package org.lolobored.jira.dao.data.json;

import lombok.Data;

import java.util.List;

@Data
public class JSONTable {

  private List<JSONHeader> cols = null;
  private List<JSONDataRow> rows = null;

}
