package net.sourceforge.zmanim.util;

import net.sourceforge.zmanim.AstronomicalCalendar;

public class SunTimesCalculator
extends AstronomicalCalculator
{
	public static final double ZENITH = 90.83333333333333D;
	private static final int TYPE_SUNRISE = 0;
	private static final int TYPE_SUNSET = 1;
	private static final double DEG_PER_HOUR = 15.0D;

	public String getCalculatorName() {
		return "US Naval Almanac Algorithm";
	}

	public double getUTCSunrise(AstronomicalCalendar astronomicalCalendar, double zenith, boolean adjustForElevation) {
		double doubleTime = Double.NaN;
		if (adjustForElevation) {
			zenith = adjustZenith(zenith, astronomicalCalendar.getGeoLocation().getElevation());
		} else {
			zenith = adjustZenith(zenith, 0.0D);
		} 
		doubleTime = getTimeUTC(astronomicalCalendar.getCalendar().get(1), astronomicalCalendar.getCalendar().get(2) + 1, astronomicalCalendar.getCalendar().get(5), astronomicalCalendar.getGeoLocation().getLongitude(), astronomicalCalendar.getGeoLocation().getLatitude(), zenith, 0);
		return doubleTime;
	}

	public double getUTCSunset(AstronomicalCalendar astronomicalCalendar, double zenith, boolean adjustForElevation) {
		double doubleTime = Double.NaN;
		if (adjustForElevation) {
			zenith = adjustZenith(zenith, astronomicalCalendar.getGeoLocation().getElevation());
		} else {
			zenith = adjustZenith(zenith, 0.0D);
		} 
		doubleTime = getTimeUTC(astronomicalCalendar.getCalendar().get(1), astronomicalCalendar.getCalendar().get(2) + 1, astronomicalCalendar.getCalendar().get(5), astronomicalCalendar.getGeoLocation().getLongitude(), astronomicalCalendar.getGeoLocation().getLatitude(), zenith, 1);
		return doubleTime;
	}

	private static double sinDeg(double deg) {
		return Math.sin(deg * 2.0D * Math.PI / 360.0D);
	}

	private static double acosDeg(double x) {
		return Math.acos(x) * 360.0D / 6.283185307179586D;
	}

	private static double asinDeg(double x) {
		return Math.asin(x) * 360.0D / 6.283185307179586D;
	}

	private static double tanDeg(double deg) {
		return Math.tan(deg * 2.0D * Math.PI / 360.0D);
	}

	private static double cosDeg(double deg) {
		return Math.cos(deg * 2.0D * Math.PI / 360.0D);
	}

	private static int getDayOfYear(int year, int month, int day) {
		int n1 = 275 * month / 9;
		int n2 = (month + 9) / 12;
		int n3 = 1 + (year - 4 * year / 4 + 2) / 3;
		int n = n1 - n2 * n3 + day - 30;
		return n;
	}

	private static double getHoursFromMeridian(double longitude) {
		return longitude / 15.0D;
	}

	private static double getApproxTimeDays(int dayOfYear, double hoursFromMeridian, int type) {
		if (type == 0) {
			return dayOfYear + (6.0D - hoursFromMeridian) / 24.0D;
		}
		return dayOfYear + (18.0D - hoursFromMeridian) / 24.0D;
	}

	private static double getMeanAnomaly(int dayOfYear, double longitude, int type) {
		return 0.9856D * getApproxTimeDays(dayOfYear, getHoursFromMeridian(longitude), type) - 3.289D;
	}

	private static double getSunTrueLongitude(double sunMeanAnomaly) {
		double l = sunMeanAnomaly + 1.916D * sinDeg(sunMeanAnomaly) + 0.02D * sinDeg(2.0D * sunMeanAnomaly) + 282.634D;
		if (l >= 360.0D) {
			l -= 360.0D;
		}
		if (l < 0.0D) {
			l += 360.0D;
		}
		return l;
	}

	private static double getSunRightAscensionHours(double sunTrueLongitude) {
		double a = 0.91764D * tanDeg(sunTrueLongitude);
		double ra = 57.29577951308232D * Math.atan(a);
		double lQuadrant = Math.floor(sunTrueLongitude / 90.0D) * 90.0D;
		double raQuadrant = Math.floor(ra / 90.0D) * 90.0D;
		ra += lQuadrant - raQuadrant;
		return ra / 15.0D;
	}

	private static double getCosLocalHourAngle(double sunTrueLongitude, double latitude, double zenith) {
		double sinDec = 0.39782D * sinDeg(sunTrueLongitude);
		double cosDec = cosDeg(asinDeg(sinDec));
		double cosH = (cosDeg(zenith) - sinDec * sinDeg(latitude)) / cosDec * cosDeg(latitude);
		return cosH;
	}

	private static double getCosLocalHourAngle(double sunTrueLongitude, double latitude) {
		return getCosLocalHourAngle(sunTrueLongitude, latitude, 90.83333333333333D);
	}

	private static double getLocalMeanTime(double localHour, double sunRightAscensionHours, double approxTimeDays) {
		return localHour + sunRightAscensionHours - 0.06571D * approxTimeDays - 6.622D;
	}

	private static double getTimeUTC(int year, int month, int day, double longitude, double latitude, double zenith, int type) {
		int dayOfYear = getDayOfYear(year, month, day);
		double sunMeanAnomaly = getMeanAnomaly(dayOfYear, longitude, type);
		double sunTrueLong = getSunTrueLongitude(sunMeanAnomaly);
		double sunRightAscensionHours = getSunRightAscensionHours(sunTrueLong);
		double cosLocalHourAngle = getCosLocalHourAngle(sunTrueLong, latitude, zenith);
		double localHourAngle = 0.0D;
		if (type == 0) {
			if (cosLocalHourAngle > 1.0D);
			localHourAngle = 360.0D - acosDeg(cosLocalHourAngle);
		} else {
			if (cosLocalHourAngle < -1.0D);
			localHourAngle = acosDeg(cosLocalHourAngle);
		} 
		double localHour = localHourAngle / 15.0D;
		double localMeanTime = getLocalMeanTime(localHour, sunRightAscensionHours, getApproxTimeDays(dayOfYear, getHoursFromMeridian(longitude), type));
		double pocessedTime = localMeanTime - getHoursFromMeridian(longitude);
		while (pocessedTime < 0.0D) {
			pocessedTime += 24.0D;
		}
		while (pocessedTime >= 24.0D) {
			pocessedTime -= 24.0D;
		}
		return pocessedTime;
	}
}
