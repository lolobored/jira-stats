package org.lolobored.jira.dao.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.lolobored.jira.ProcessException;
import org.lolobored.jira.dao.data.json.JSONData;
import org.lolobored.jira.dao.data.json.JSONDataRow;
import org.lolobored.jira.dao.data.json.JSONHeader;
import org.lolobored.jira.dao.data.json.JSONTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class DAOTable extends ArrayList<DAORow> {
  private DAOHeader header = new DAOHeader();


  public String dumpToString(int maxLines) {
    StringBuilder result = new StringBuilder("[");
    int loopOcc = 0;
    for (HeaderColumn headerColumn : header) {
      if (loopOcc != 0) {
        result.append(", ");
      }
      result.append("'");
      result.append(headerColumn.getValue());
      result.append("'");
      loopOcc++;
    }
    result.append("]");

    for (int i = 0; i < maxLines && i < size(); i++) {
      result.append(",\r\n[");
      DAORow line = get(i);
      loopOcc = 0;
      for (HeaderColumn headerColumn : header) {
        if (loopOcc != 0) {
          result.append(", ");
        }
        if (headerColumn.isString()) {
          result.append("'");
        }
        result.append(line.get(headerColumn.getValue()));
        if (headerColumn.isString()) {
          result.append("'");
        }
        loopOcc++;
      }
      result.append("]");
    }
    return result.toString();
  }

  public String toJSON() throws ProcessException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    List data = new ArrayList();

    // build JSONTable
    JSONTable table = new JSONTable();
    List<JSONHeader> jsonHeader = new ArrayList();
    JSONHeader column = new JSONHeader();
    for (HeaderColumn headerColumn : header) {
      column = new JSONHeader();
      column.setLabel(headerColumn.getValue());
      column.setType(headerColumn.getType());
      jsonHeader.add(column);
    }
    table.setCols(jsonHeader);

    List<JSONDataRow> rows = new ArrayList();
    JSONDataRow jsonDataRow;
    List<JSONData> c = new ArrayList();
    JSONData dataJSON;

    for (int i = 0; i < size(); i++) {
      DAORow daoRow = get(i);
      c = new ArrayList();
      jsonDataRow = new JSONDataRow();
      for (HeaderColumn headerColumn : header) {
        dataJSON = new JSONData();
        if (HeaderColumn.STRING_TYPE.equals(headerColumn.getType())) {
          dataJSON.setV(daoRow.get(headerColumn.getValue()));
        } else if (HeaderColumn.NUMBER_TYPE.equals(headerColumn.getType())) {
          dataJSON.setV(Double.valueOf(daoRow.get(headerColumn.getValue())));
        } else if (HeaderColumn.BOOLEAN_TYPE.equals(headerColumn.getType())) {
          dataJSON.setV(Boolean.valueOf(daoRow.get(headerColumn.getValue())));
        } else {
          throw new ProcessException("Type [" + headerColumn.getType() + "] is not handled");
        }
        c.add(dataJSON);
      }
      jsonDataRow.setC(c);
      rows.add(jsonDataRow);
    }
    table.setRows(rows);

    try {
      mapper.writeValue(bout, table);
    } catch (IOException ex) {
      throw new ProcessException("Exception while getting json", ex);
    }
    return bout.toString();
  }
}
