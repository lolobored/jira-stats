package org.lolobored.jira.properties;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Component
@Data
public class ProjectMainLabelsProperties {

	private ProjectLabelsList projectLablesList= new ProjectLabelsList();

	@PostConstruct
	public ProjectLabelsList getProjectMainLabelsProperties() throws IOException {
		Resource resource = new ClassPathResource("/jira.properties");
		Properties props = PropertiesLoaderUtils.loadProperties(resource);

		String[] projectNames= props.getProperty("jira.project").split(";");

		for (String projectName: projectNames){
			List<ProjectLabelsList.ProjectLabel> projectLabels = projectLablesList.getLabelsPerProject(projectName);
			for (String singleProperty: props.stringPropertyNames()){
				if (StringUtils.startsWithIgnoreCase(singleProperty, projectName+".label")){
					String labelName= StringUtils.replace(singleProperty, (projectName+".label.").toLowerCase(), "");
					String labelCategory= props.getProperty(singleProperty);
					ProjectLabelsList.ProjectLabel label = new ProjectLabelsList().new ProjectLabel(labelName, labelCategory);
					projectLabels.add(label);
				}
			}
		}
		return projectLablesList;
	}

	public List<ProjectLabelsList.ProjectLabel> getLabelsPerProject(String project) {
		return projectLablesList.getLabelsPerProject(project);
	}


	@Data
	public class ProjectLabelsList {
		private Map<String, List<ProjectLabel>> labels= new HashMap<>();

		public List<ProjectLabel> getLabelsPerProject(String project){
			List<ProjectLabel> result = labels.get(project);
			if (result == null){
				result=new ArrayList<>();
				labels.put(project, result);
			}
			return result;
		}

		@Data
		public class ProjectLabel {
			private final String labelName;
			private final String categoryName;

			public ProjectLabel(String labelName, String categoryName){
				this.labelName= labelName;
				this.categoryName= categoryName;
			}
		}
	}
}
