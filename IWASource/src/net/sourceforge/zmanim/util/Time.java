package net.sourceforge.zmanim.util;

public class Time
{
	private static final int SECOND_MILLIS = 1000;
	private static final int MINUTE_MILLIS = 60000;
	private static final int HOUR_MILLIS = 3600000;
	private int hours = 0;   
	private int minutes = 0;
	private int seconds = 0;
	private int milliseconds = 0;
	private boolean isNegative = false;

	public Time(int hours, int minutes, int seconds, int milliseconds) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.milliseconds = milliseconds;
	}

	public Time(double millis) {
		this((int)millis);
	}

	public Time(int millis) {
		if (millis < 0) {
			this.isNegative = true;
			millis = Math.abs(millis);
		} 
		this.hours = millis / 3600000;
		millis -= this.hours * 3600000;
		this.minutes = millis / 60000;
		millis -= this.minutes * 60000;
		this.seconds = millis / 1000;
		millis -= this.seconds * 1000;
		this.milliseconds = millis;
	}

	public boolean isNegative() {
		return this.isNegative;
	}

	public void setIsNegative(boolean isNegative) {
		this.isNegative = isNegative;
	}

	public int getHours() {
		return this.hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public int getMinutes() {
		return this.minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getSeconds() {
		return this.seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public int getMilliseconds() {
		return this.milliseconds;
	}

	public void setMilliseconds(int milliseconds) {
		this.milliseconds = milliseconds;
	}

	public double getTime() {
		return (this.hours * 3600000 + this.minutes * 60000 + this.seconds * 1000 + this.milliseconds);
	}

	public String toString() {
		return (new ZmanimFormatter()).format(this);
	}
}
