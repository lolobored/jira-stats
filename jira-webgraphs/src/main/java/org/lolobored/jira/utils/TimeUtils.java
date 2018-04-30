package org.lolobored.jira.utils;

import org.apache.commons.lang.StringUtils;
import org.lolobored.jira.properties.JiraProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class TimeUtils {

	@Autowired
	JiraProperties jiraProperties;

	public String returnTimeSpentPerDay(int timeInSecond){
		// a working day is 8h not 24
		int day = (int) TimeUnit.SECONDS.toDays(timeInSecond * 24 / Integer.parseInt(jiraProperties.getHoursPerDay()));

		long hours = TimeUnit.SECONDS.toHours(timeInSecond - (day * 24 /Integer.parseInt(jiraProperties.getHoursPerDay())));

		hours = new Double((double) hours / 60 * 100).intValue();
		String time = String.format("%d.%s", day, StringUtils.leftPad(String.valueOf(hours), 2, "0"));
		return time;
	}

}
