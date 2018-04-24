package org.lolobored.jira.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "spring.data.jest")
@Data
public class ApplicationProperties {

  private String uri;
  private String maxindexsearch;
}
