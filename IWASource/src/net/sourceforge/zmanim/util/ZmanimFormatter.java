package net.sourceforge.zmanim.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ZmanimFormatter
{
	private boolean prependZeroHours;
	private boolean useSeconds;
	private boolean useMillis;
	boolean useDecimal;
	private static DecimalFormat minuteSecondNF = new DecimalFormat("00");
	private DecimalFormat hourNF;
	private static DecimalFormat milliNF = new DecimalFormat("000");
	private SimpleDateFormat dateFormat;
	public static final int SEXAGESIMAL_XSD_FORMAT = 0;
	private int timeFormat = 0;
	public static final int DECIMAL_FORMAT = 1;
	public static final int SEXAGESIMAL_FORMAT = 2;
	public static final int SEXAGESIMAL_SECONDS_FORMAT = 3;
	public static final int SEXAGESIMAL_MILLIS_FORMAT = 4;
	public static final int XSD_DURATION_FORMAT = 5;

	public ZmanimFormatter() {
		this(0, new SimpleDateFormat("h:mm:ss"));
	}

	public ZmanimFormatter(int format, SimpleDateFormat dateFormat) {
		String hourFormat = "0";
		if (this.prependZeroHours) {
			hourFormat = "00";
		}
		this.hourNF = new DecimalFormat(hourFormat);
		setTimeFormat(format);
		setDateFormat(dateFormat);
	}

	public void setTimeFormat(int format) {
		this.timeFormat = format;
		switch (format) {
		case 0:
			setSettings(true, true, true);
			return;
		case 2:
			setSettings(false, false, false);
			return;
		case 3:
			setSettings(false, true, false);
			return;
		case 4:
			setSettings(false, true, true);
			return;
		} 
		this.useDecimal = true;
	}

	public void setDateFormat(SimpleDateFormat sdf) {
		this.dateFormat = sdf;
	}

	public SimpleDateFormat getDateFormat() {
		return this.dateFormat;
	}

	private void setSettings(boolean prependZeroHours, boolean useSeconds, boolean useMillis) {
		this.prependZeroHours = prependZeroHours;
		this.useSeconds = useSeconds;
		this.useMillis = useMillis;
	}

	public String format(double milliseconds) {
		return format((int)milliseconds);
	}

	public String format(int millis) {
		return format(new Time(millis));
	}

	public String format(Time time) {
		if (this.timeFormat == 5) {
			return formatXSDDurationTime(time);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(this.hourNF.format(time.getHours()));
		sb.append(":");
		sb.append(minuteSecondNF.format(time.getMinutes()));
		if (this.useSeconds) {
			sb.append(":");
			sb.append(minuteSecondNF.format(time.getSeconds()));
		} 
		if (this.useMillis) {
			sb.append(".");
			sb.append(milliNF.format(time.getMilliseconds()));
		} 
		return sb.toString();
	}

	public String formatDateTime(Date dateTime, Calendar calendar) {
		this.dateFormat.setCalendar(calendar);
		if (this.dateFormat.toPattern().equals("yyyy-MM-dd'T'HH:mm:ss")) {
			return getXSDateTime(dateTime, calendar);
		}
		return this.dateFormat.format(dateTime);
	}

	public String getXSDateTime(Date dateTime, Calendar cal) {
		String xsdDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(xsdDateTimeFormat);
		StringBuffer buff = new StringBuffer(dateFormat.format(dateTime));
		int offset = cal.get(15) + cal.get(16);
		if (offset == 0) {
			buff.append("Z");
		} else {
			int hrs = offset / 3600000;
			int min = offset % 3600000;
			char posneg = (hrs < 0) ? '-' : '+';
			buff.append(posneg + formatDigits(hrs) + ':' + formatDigits(min));
		} 
		return buff.toString();
	}

	private static String formatDigits(int digits) {
		String dd = String.valueOf(Math.abs(digits));
		return (dd.length() == 1) ? ('0' + dd) : dd;
	}

	public String formatXSDDurationTime(long millis) {
		return formatXSDDurationTime(new Time(millis));
	}

	public String formatXSDDurationTime(Time time) {
		StringBuffer duration = new StringBuffer();
		duration.append("P");
		if (time.getHours() != 0 || time.getMinutes() != 0 || time.getSeconds() != 0 || time.getMilliseconds() != 0) {
			duration.append("T");
			if (time.getHours() != 0) {
				duration.append(time.getHours() + "H");
			}
			if (time.getMinutes() != 0) {
				duration.append(time.getMinutes() + "M");
			}
			if (time.getSeconds() != 0 || time.getMilliseconds() != 0) {
				duration.append(time.getSeconds() + "." + milliNF.format(time.getMilliseconds()));
				duration.append("S");
			} 
			if (duration.length() == 1)
				duration.append("T0S"); 
			if (time.isNegative())
				duration.insert(0, "-"); 
		} 
		return duration.toString();
	}
}
