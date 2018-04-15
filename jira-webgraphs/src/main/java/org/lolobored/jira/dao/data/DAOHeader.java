package org.lolobored.jira.dao.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class DAOHeader extends ArrayList<HeaderColumn> {

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  public static String STRING_TYPE = HeaderColumn.STRING_TYPE;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  public static String NUMBER_TYPE = HeaderColumn.NUMBER_TYPE;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  public static String BOOLEAN_TYPE = HeaderColumn.BOOLEAN_TYPE;

  public DAOHeader() {
    super();
  }

  public void addHeader(String type, String name) {
    HeaderColumn obj = new HeaderColumn(type, name);
    this.add(obj);
  }
}
