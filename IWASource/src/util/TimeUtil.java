package util;

import java.util.Calendar;

public class TimeUtil
{
	public static final int DAYS_OF_YEAR = 366;
	public static final int HOURS_OF_DAY = 24;
	public static final int MINUTES_OF_HOUR = 60;
	public static final int SECONDS_OF_MINUTE = 60;
	public static final int SECONDS_OF_HOUR = 3600;
	public static final int SECONDS_OF_DAY = 86400;

	private static void setMax(Calendar calendar, int field) {
		calendar.set(field, calendar.getActualMaximum(field));
	}

	private static void setMin(Calendar cal, int field) {
		cal.set(field, cal.getActualMinimum(field));
	}

	private static void setDayStart(Calendar cal) {
		setMin(cal, 11);
		setMin(cal, 12);
		setMin(cal, 13);
		setMin(cal, 14);
	}

	private static void setDayEnd(Calendar cal) {
		setMax(cal, 11);
		setMax(cal, 12);
		setMax(cal, 13);
		setMax(cal, 14);
	}

	public static long getDayStartTime(Calendar calendar) {
		Calendar cal = (Calendar)calendar.clone();
		setDayStart(cal);
		return cal.getTimeInMillis();
	}

	public static long getDayEndTime(Calendar calendar) {
		Calendar cal = (Calendar)calendar.clone();
		setDayEnd(cal);
		return cal.getTimeInMillis();
	}

	public static float getProgress(Calendar now, Calendar start, Calendar end) {
		float progress = (float)(now.getTimeInMillis() - start.getTimeInMillis()) / (float)(end.getTimeInMillis() - start.getTimeInMillis());
		return progress;
	}

	public static float getDayProgress(Calendar now) {
		now.getTimeInMillis();
		Calendar start = (Calendar)now.clone();
		setDayStart(start);
		Calendar end = (Calendar)now.clone();
		setDayEnd(end);
		return getProgress(now, start, end);
	}

	public static Calendar fromProgress(float progress, Calendar start, Calendar end) {
		Calendar now = (Calendar)start.clone();
		now.setTimeInMillis(start.getTimeInMillis() + (long)((end.getTimeInMillis() - start.getTimeInMillis()) * progress));
		return now;
	}
}
