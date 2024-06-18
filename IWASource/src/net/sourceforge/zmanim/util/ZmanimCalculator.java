package net.sourceforge.zmanim.util;

import net.sourceforge.zmanim.AstronomicalCalendar;

public class ZmanimCalculator
extends AstronomicalCalculator
{

	public String getCalculatorName() {
		return "US Naval Almanac Algorithm";
	}

	public double getUTCSunrise(AstronomicalCalendar astronomicalCalendar, double zenith, boolean adjustForElevation) {
		if (adjustForElevation) {
			zenith = adjustZenith(zenith, astronomicalCalendar.getGeoLocation().getElevation());
		} else {
			zenith = adjustZenith(zenith, 0.0D);
		} 
		double lngHour = astronomicalCalendar.getGeoLocation().getLongitude() / 15.0D;
		double t = astronomicalCalendar.getCalendar().get(6) + (6.0D - lngHour) / 24.0D;
		double m = 0.9856D * t - 3.289D;
		double l = m + 1.916D * Math.sin(Math.toRadians(m)) + 0.02D * Math.sin(Math.toRadians(2.0D * m)) + 282.634D;
		while (l < 0.0D) {
			double Lx = l + 360.0D;
			l = Lx;
		} 
		while (l >= 360.0D) {
			double Lx = l - 360.0D;
			l = Lx;
		} 
		double RA = Math.toDegrees(Math.atan(0.91764D * Math.tan(Math.toRadians(l))));
		while (RA < 0.0D) {
			double RAx = RA + 360.0D;
			RA = RAx;
		} 
		while (RA >= 360.0D) {
			double RAx = RA - 360.0D;
			RA = RAx;
		} 
		double lQuadrant = Math.floor(l / 90.0D) * 90.0D;
		double raQuadrant = Math.floor(RA / 90.0D) * 90.0D;
		RA += lQuadrant - raQuadrant;
		RA /= 15.0D;
		double sinDec = 0.39782D * Math.sin(Math.toRadians(l));
		double cosDec = Math.cos(Math.asin(sinDec));
		double cosH = (Math.cos(Math.toRadians(zenith)) - sinDec * Math.sin(Math.toRadians(astronomicalCalendar.getGeoLocation().getLatitude()))) / cosDec * Math.cos(Math.toRadians(astronomicalCalendar.getGeoLocation().getLatitude()));
		double H = 360.0D - Math.toDegrees(Math.acos(cosH));
		H /= 15.0D;
		double T = H + RA - 0.06571D * t - 6.622D;
		double UT = T - lngHour;
		while (UT < 0.0D) {
			double UTx = UT + 24.0D;
			UT = UTx;
		} 
		while (UT >= 24.0D) {
			double UTx = UT - 24.0D;
			UT = UTx;
		} 
		return UT;
	}

	public double getUTCSunset(AstronomicalCalendar astronomicalCalendar, double zenith, boolean adjustForElevation) {
		if (adjustForElevation) {
			zenith = adjustZenith(zenith, astronomicalCalendar.getGeoLocation().getElevation());
		} else {
			zenith = adjustZenith(zenith, 0.0D);
		} 
		int N = astronomicalCalendar.getCalendar().get(6);
		double lngHour = astronomicalCalendar.getGeoLocation().getLongitude() / 15.0D;
		double t = N + (18.0D - lngHour) / 24.0D;
		double M = 0.9856D * t - 3.289D;
		double L = M + 1.916D * Math.sin(Math.toRadians(M)) + 0.02D * Math.sin(Math.toRadians(2.0D * M)) + 282.634D;
		while (L < 0.0D) {
			double Lx = L + 360.0D;
			L = Lx;
		} 
		while (L >= 360.0D) {
			double Lx = L - 360.0D;
			L = Lx;
		} 
		double RA = Math.toDegrees(Math.atan(0.91764D * Math.tan(Math.toRadians(L))));
		while (RA < 0.0D) {
			double RAx = RA + 360.0D;
			RA = RAx;
		} 
		while (RA >= 360.0D) {
			double RAx = RA - 360.0D;
			RA = RAx;
		} 
		double Lquadrant = Math.floor(L / 90.0D) * 90.0D;
		double RAquadrant = Math.floor(RA / 90.0D) * 90.0D;
		RA += Lquadrant - RAquadrant;
		RA /= 15.0D;
		double sinDec = 0.39782D * Math.sin(Math.toRadians(L));
		double cosDec = Math.cos(Math.asin(sinDec));
		double cosH = (Math.cos(Math.toRadians(zenith)) - sinDec * Math.sin(Math.toRadians(astronomicalCalendar.getGeoLocation().getLatitude()))) / cosDec * Math.cos(Math.toRadians(astronomicalCalendar.getGeoLocation().getLatitude()));
		double H = Math.toDegrees(Math.acos(cosH));
		H /= 15.0D;
		double T = H + RA - 0.06571D * t - 6.622D;
		double UT = T - lngHour;
		while (UT < 0.0D) {
			double UTx = UT + 24.0D;
			UT = UTx;
		} 
		while (UT >= 24.0D) {
			double UTx = UT - 24.0D;
			UT = UTx;
		} 
		return UT;
	}
}
