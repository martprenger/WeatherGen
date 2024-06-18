package util;

import java.util.Calendar;

public class DayPart {
	public static final int TYPE_SUNRISE = 0;
	public static final int TYPE_SUNSET = 1;
	public static final int RISING = 10;
	public static final int SETTING = 11;
	public Calendar startTime;
	public Calendar endTime;
	public int startType;
	public int endType;
	private boolean rising;

	public DayPart(Calendar partStart, int startType, Calendar partEnd, int endType) {
		this.startTime = partStart;
		this.startType = startType;
		this.endTime = partEnd;
		this.endType = endType;
		this.rising = (this.startType == 0);
	}

	public boolean isRising() {
		return this.rising;
	}

	public boolean contains(Calendar cal) {
		return (this.startTime.compareTo(cal) <= 0 && cal.compareTo(this.endTime) <= 0);
	}
}

