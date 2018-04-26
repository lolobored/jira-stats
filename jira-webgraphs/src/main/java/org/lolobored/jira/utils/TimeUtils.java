package org.lolobored.jira.utils;

import org.apache.commons.lang.StringUtils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

	public static String returnTimeSpentPerDay(int timeInSecond){
		// a working day is 8h not 24
		int day = (int) TimeUnit.SECONDS.toDays(timeInSecond * 24 / 8);

		long hours = TimeUnit.SECONDS.toHours(timeInSecond - (day * 24 /8));

		hours = new Double((double) hours / 60 * 100).intValue();
		String time = String.format("%d.%s", day, StringUtils.leftPad(String.valueOf(hours), 2, "0"));
		return time;
	}

}
