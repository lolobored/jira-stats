package org.lolobored.jira.webgraphs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:jira.properties")
@ConfigurationProperties(prefix = "jira")
@Data
public class JiraProperties {

  private String baseurl;
  private String project;
  private String board;
  private String username;
  private String password;
  private String maximum;
}
