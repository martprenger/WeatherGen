package net.sourceforge.zmanim.util;

import java.util.Calendar;
import net.sourceforge.zmanim.AstronomicalCalendar;
import net.sourceforge.zmanim.ZmanimException;

public class JSuntimeCalculator
extends AstronomicalCalculator
{
	public String getCalculatorName() {
		return "US National Oceanic and Atmospheric Administration Algorithm";
	}

	public double getUTCSunrise(AstronomicalCalendar astronomicalCalendar, double zenith, boolean adjustForElevation) {
		if (astronomicalCalendar.getCalendar().get(1) == 2000) {
			throw new ZmanimException("JSuntimeCalculator can not calculate times for the year 2000. Please try a date with a different year.");
		}
		if (adjustForElevation) {
			zenith = adjustZenith(zenith, astronomicalCalendar.getGeoLocation().getElevation());
		} else {
			zenith = adjustZenith(zenith, 0.0D);
		} 
		double timeMins = morningPhenomenon(dateToJulian(astronomicalCalendar.getCalendar()), astronomicalCalendar.getGeoLocation().getLatitude(), -astronomicalCalendar.getGeoLocation().getLongitude(), zenith);
		return timeMins / 60.0D;
	}

	public double getUTCSunset(AstronomicalCalendar astronomicalCalendar, double zenith, boolean adjustForElevation) {
		if (astronomicalCalendar.getCalendar().get(1) == 2000) {
			throw new ZmanimException("JSuntimeCalculator can not calculate times for the year 2000. Please try a date with a different year.");
		}
		if (adjustForElevation) {
			zenith = adjustZenith(zenith, astronomicalCalendar.getGeoLocation().getElevation());
		} else {
			zenith = adjustZenith(zenith, 0.0D);
		} 
		double timeMins = eveningPhenomenon(dateToJulian(astronomicalCalendar.getCalendar()), astronomicalCalendar.getGeoLocation().getLatitude(), -astronomicalCalendar.getGeoLocation().getLongitude(), zenith);
		return timeMins / 60.0D;
	}

	private static double morningPhenomenon(double julian, double latitude, double longitude, double zenithDistance) {
		double t = julianDayToJulianCenturies(julian);
		double eqtime = equationOfTime(t);
		double solarDec = sunDeclination(t);
		double hourangle = hourAngleMorning(latitude, solarDec, zenithDistance);
		double delta = longitude - Math.toDegrees(hourangle);
		double timeDiff = 4.0D * delta;
		double timeUTC = 720.0D + timeDiff - eqtime;
		double newt = julianDayToJulianCenturies(julianCenturiesToJulianDay(t) + timeUTC / 1440.0D);
		eqtime = equationOfTime(newt);
		solarDec = sunDeclination(newt);
		hourangle = hourAngleMorning(latitude, solarDec, zenithDistance);
		delta = longitude - Math.toDegrees(hourangle);
		timeDiff = 4.0D * delta;
		return 720.0D + timeDiff - eqtime;
	}

	private static double eveningPhenomenon(double julian, double latitude, double longitude, double zenithDistance) {
		double t = julianDayToJulianCenturies(julian);
		double eqtime = equationOfTime(t);
		double solarDec = sunDeclination(t);
		double hourangle = hourAngleEvening(latitude, solarDec, zenithDistance);
		double delta = longitude - Math.toDegrees(hourangle);
		double timeDiff = 4.0D * delta;
		double timeUTC = 720.0D + timeDiff - eqtime;
		double newt = julianDayToJulianCenturies(julianCenturiesToJulianDay(t) + timeUTC / 1440.0D);
		eqtime = equationOfTime(newt);
		solarDec = sunDeclination(newt);
		hourangle = hourAngleEvening(latitude, solarDec, zenithDistance);
		delta = longitude - Math.toDegrees(hourangle);
		timeDiff = 4.0D * delta;
		return 720.0D + timeDiff - eqtime;
	}

	private static double dateToJulian(Calendar date) {
		int year = date.get(1);
		int month = date.get(2) + 1;
		int day = date.get(5);
		int hour = date.get(11);
		int minute = date.get(12);
		int second = date.get(13);
		double extra = 100.0D * year + month - 190002.5D;
		return 367.0D * year - Math.floor(7.0D * (year + Math.floor((month + 9.0D) / 12.0D)) / 4.0D) + Math.floor(275.0D * month / 9.0D) + day + (hour + (minute + second / 60.0D) / 60.0D) / 24.0D + 1721013.5D - 0.5D * extra / Math.abs(extra) + 0.5D;
	}

	private static double julianDayToJulianCenturies(double julian) {
		return (julian - 2451545.0D) / 36525.0D;
	}

	private static double julianCenturiesToJulianDay(double t) {
		return t * 36525.0D + 2451545.0D;
	}

	private static double equationOfTime(double t) {
		double epsilon = obliquityCorrection(t);
		double l0 = geomMeanLongSun(t);
		double e = eccentricityOfEarthsOrbit(t);
		double m = geometricMeanAnomalyOfSun(t);
		double y = Math.pow(Math.tan(Math.toRadians(epsilon) / 2.0D), 2.0D);     
		double eTime = y * Math.sin(2.0D * Math.toRadians(l0)) - 2.0D * e * Math.sin(Math.toRadians(m)) + 4.0D * e * y * Math.sin(Math.toRadians(m)) * Math.cos(2.0D * Math.toRadians(l0)) - 0.5D * y * y * Math.sin(4.0D * Math.toRadians(l0)) - 1.25D * e * e * Math.sin(2.0D * Math.toRadians(m));
		return Math.toDegrees(eTime) * 4.0D;
	}

	private static double sunDeclination(double t) {
		double e = obliquityCorrection(t);
		double lambda = sunsApparentLongitude(t);
		double sint = Math.sin(Math.toRadians(e)) * Math.sin(Math.toRadians(lambda));
		return Math.toDegrees(Math.asin(sint));
	}

	private static double hourAngleMorning(double lat, double solarDec, double zenithDistance) {
		return Math.acos(Math.cos(Math.toRadians(zenithDistance)) / Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(solarDec)) - Math.tan(Math.toRadians(lat)) * Math.tan(Math.toRadians(solarDec)));
	}

	private static double hourAngleEvening(double lat, double solarDec, double zenithDistance) {
		return -hourAngleMorning(lat, solarDec, zenithDistance);
	}

	private static double obliquityCorrection(double t) {
		return meanObliquityOfEcliptic(t) + 0.00256D * Math.cos(Math.toRadians(125.04D - 1934.136D * t));
	}

	private static double meanObliquityOfEcliptic(double t) {
		return 23.0D + (26.0D + 21.448D - t * (46.815D + t * (5.9E-4D - t * 0.001813D)) / 60.0D) / 60.0D;
	}

	private static double geomMeanLongSun(double t) {
		double l0 = 280.46646D + t * (36000.76983D + 3.032E-4D * t);
		while (l0 >= 0.0D && l0 <= 360.0D) {
			if (l0 > 360.0D) {
				l0 -= 360.0D;
			}
			if (l0 < 0.0D) {
				l0 += 360.0D;
			}
		} 
		return l0;
	}

	private static double eccentricityOfEarthsOrbit(double t) {
		return 0.016708634D - t * (4.2037E-5D + 1.267E-7D * t);
	}

	private static double geometricMeanAnomalyOfSun(double t) {
		return 357.52911D + t * (35999.05029D - 1.537E-4D * t);
	}

	private static double sunsApparentLongitude(double t) {
		return sunsTrueLongitude(t) - 0.00569D - 0.00478D * Math.sin(Math.toRadians(125.04D - 1934.136D * t));
	}

	private static double sunsTrueLongitude(double t) {
		return geomMeanLongSun(t) + equationOfCentreForSun(t);
	}

	private static double equationOfCentreForSun(double t) {
		double m = geometricMeanAnomalyOfSun(t);
		return Math.sin(Math.toRadians(m)) * (1.914602D - t * (0.004817D + 1.4E-5D * t)) + Math.sin(2.0D * Math.toRadians(m)) * (0.019993D - 1.01E-4D * t) + Math.sin(3.0D * Math.toRadians(m)) * 2.89E-4D;
	}
}
