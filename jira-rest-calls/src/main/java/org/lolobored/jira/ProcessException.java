package org.lolobored.jira;

public class ProcessException extends Exception {

  public ProcessException(String message){
    super(message);
  }

  public ProcessException(String message, Throwable th){
    super(message, th);
  }

}
