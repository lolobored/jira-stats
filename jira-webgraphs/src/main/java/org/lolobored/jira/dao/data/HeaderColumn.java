package org.lolobored.jira.dao.data;

import lombok.Data;

@Data
public class HeaderColumn {

  private final String type;
  private final String label;
  public final static String STRING_TYPE = "string";
  public final static String NUMBER_TYPE = "number";
  public final static String BOOLEAN_TYPE = "boolean";

  public HeaderColumn(String type, String label) {
    this.type = type;
    this.label = label;
  }

  public String getType() {
    return type;
  }

  public String getValue() {
    return label;
  }

  public boolean isString() {
    return STRING_TYPE.equals(type);
  }

}
