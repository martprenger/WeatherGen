package net.sourceforge.zmanim.util;

import net.sourceforge.zmanim.AstronomicalCalendar;

public abstract class AstronomicalCalculator
{
	private double refraction = 0.5666666666666667D;
	private double solarRadius = 0.26666666666666666D;

	public static AstronomicalCalculator getDefault() {
		return new SunTimesCalculator();
	}

	public abstract String getCalculatorName();
	public abstract double getUTCSunrise(AstronomicalCalendar paramAstronomicalCalendar, double paramDouble, boolean paramBoolean);
	public abstract double getUTCSunset(AstronomicalCalendar paramAstronomicalCalendar, double paramDouble, boolean paramBoolean);   

	double getElevationAdjustment(double elevation) {
		double earthRadius = 6356.9D;
		double elevationAdjustment = Math.toDegrees(Math.acos(earthRadius / (earthRadius + elevation / 1000.0D)));     
		return elevationAdjustment;
	}

	double adjustZenith(double zenith, double elevation) {
		if (zenith == 90.0D) {
			zenith += getSolarRadius() + getRefraction() + getElevationAdjustment(elevation);
		}
		return zenith;
	}

	double getRefraction() {
		return this.refraction;
	}

	public void setRefraction(double refraction) {
		this.refraction = refraction;
	}

	double getSolarRadius() {
		return this.solarRadius;
	}

	public void setSolarRadius(double solarRadius) {
		this.solarRadius = solarRadius;
	}
}
