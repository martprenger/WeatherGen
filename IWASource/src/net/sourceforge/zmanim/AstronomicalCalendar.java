package net.sourceforge.zmanim;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import net.sourceforge.zmanim.util.AstronomicalCalculator;
import net.sourceforge.zmanim.util.GeoLocation;
import net.sourceforge.zmanim.util.Zman;
import net.sourceforge.zmanim.util.ZmanimFormatter;

public class AstronomicalCalendar
{
	private static final long serialVersionUID = 1L;
	public static final double GEOMETRIC_ZENITH = 90.0D;
	public static final double CIVIL_ZENITH = 96.0D;
	public static final double NAUTICAL_ZENITH = 102.0D;
	public static final double ASTRONOMICAL_ZENITH = 108.0D;
	static final long MINUTE_MILLIS = 60000L;
	private Calendar calendar;
	private GeoLocation geoLocation;
	private AstronomicalCalculator astronomicalCalculator;

	public Date getSunrise() {
		double sunrise = getUTCSunrise(90.0D);
		if (Double.isNaN(sunrise)) {
			return null;
		}
		sunrise = getOffsetTime(sunrise);
		return getDateFromTime(sunrise);
	}

	public Date getSeaLevelSunrise() {
		double sunrise = getUTCSeaLevelSunrise(90.0D);
		if (Double.isNaN(sunrise)) {
			return null;
		}
		sunrise = getOffsetTime(sunrise);
		return getDateFromTime(sunrise);
	}

	public Date getBeginCivilTwilight() {
		return getSunriseOffsetByDegrees(96.0D);
	}

	public Date getBeginNauticalTwilight() {
		return getSunriseOffsetByDegrees(102.0D);
	}

	public Date getBeginAstronomicalTwilight() {
		return getSunriseOffsetByDegrees(108.0D);
	}

	public Date getSunset() {
		double sunset = getUTCSunset(90.0D);
		if (Double.isNaN(sunset)) {
			return null;
		}
		sunset = getOffsetTime(sunset);
		return getAdjustedSunsetDate(getDateFromTime(sunset), getSunrise());
	}

	private Date getAdjustedSunsetDate(Date sunset, Date sunrise) {
		if (sunset != null && sunrise != null && sunrise.compareTo(sunset) >= 0) {
			Calendar clonedCalendar = (GregorianCalendar)getCalendar().clone();
			clonedCalendar.setTime(sunset);
			clonedCalendar.add(5, 1);
			return clonedCalendar.getTime();
		} 
		return sunset;
	}

	public Date getSeaLevelSunset() {
		double sunset = getUTCSeaLevelSunset(90.0D);
		if (Double.isNaN(sunset)) {
			return null;
		}
		sunset = getOffsetTime(sunset);

		return getAdjustedSunsetDate(getDateFromTime(sunset), getSeaLevelSunrise());
	}

	public Date getEndCivilTwilight() {
		return getSunsetOffsetByDegrees(96.0D);
	}

	public Date getEndNauticalTwilight() {
		return getSunsetOffsetByDegrees(102.0D);
	}

	public Date getEndAstronomicalTwilight() {
		return getSunsetOffsetByDegrees(108.0D);
	}

	public Date getTimeOffset(Date time, double offset) {
		return getTimeOffset(time, (long)offset);
	}

	public Date getTimeOffset(Date time, long offset) {
		if (time == null || offset == Long.MIN_VALUE) {
			return null;
		}
		return new Date(time.getTime() + offset);
	}

	public Date getSunriseOffsetByDegrees(double offsetZenith) {
		double alos = getUTCSunrise(offsetZenith);
		if (Double.isNaN(alos)) {
			return null;
		}
		alos = getOffsetTime(alos);
		return getDateFromTime(alos);
	}

	public Date getSunsetOffsetByDegrees(double offsetZenith) {
		double sunset = getUTCSunset(offsetZenith);
		if (Double.isNaN(sunset)) {
			return null;
		}
		sunset = getOffsetTime(sunset);

		return getAdjustedSunsetDate(getDateFromTime(sunset), getSunriseOffsetByDegrees(offsetZenith));
	}

	public AstronomicalCalendar() {
		this.geoLocation = new GeoLocation();
		this.calendar = Calendar.getInstance(this.geoLocation.getTimeZone());
		this.astronomicalCalculator = AstronomicalCalculator.getDefault();
	}

	public AstronomicalCalendar(GeoLocation geoLocation) {
		this();
		setGeoLocation(geoLocation);
		this.calendar = Calendar.getInstance(geoLocation.getTimeZone());
	}

	public double getUTCSunrise(double zenith) {
		return getAstronomicalCalculator().getUTCSunrise(this, zenith, true);
	}

	public double getUTCSeaLevelSunrise(double zenith) {
		return getAstronomicalCalculator().getUTCSunrise(this, zenith, false);
	}

	public double getUTCSunset(double zenith) {
		return getAstronomicalCalculator().getUTCSunset(this, zenith, true);
	}

	public double getUTCSeaLevelSunset(double zenith) {
		return getAstronomicalCalculator().getUTCSunset(this, zenith, false);
	}

	private double getOffsetTime(double time) {
		boolean dst = getCalendar().getTimeZone().inDaylightTime(getCalendar().getTime());
		double dstOffset = 0.0D;
		double gmtOffset = (getCalendar().getTimeZone().getRawOffset() / 3600000L);
		if (dst) {
			dstOffset = (getCalendar().getTimeZone().getDSTSavings() / 3600000L);
		}
		return time + gmtOffset + dstOffset;
	}

	public long getTemporalHour() {
		return getTemporalHour(getSunrise(), getSunset());
	}

	public long getTemporalHour(Date sunrise, Date sunset) {
		if (sunrise == null || sunset == null) {
			return Long.MIN_VALUE;
		}
		return (sunset.getTime() - sunrise.getTime()) / 12L;
	}

	public Date getSunTransit() {
		return getTimeOffset(getSunrise(), getTemporalHour() * 6L);
	}

	private Date getDateFromTime(double time) {
		if (Double.isNaN(time)) {
			return null;
		}
		Calendar cal = new GregorianCalendar();
		cal.clear();
		cal.set(1, getCalendar().get(1));
		cal.set(2, getCalendar().get(2));
		cal.set(5, getCalendar().get(5));
		if ((!getCalendar().getTimeZone().inDaylightTime(getCalendar().getTime()) && time < 0.0D) || (getCalendar().getTimeZone().inDaylightTime(getCalendar().getTime()) && time < 1.0D))
		{
			cal.add(5, 1);
		}
		int hours = (int)time;
		boolean prob = false;
		if (hours < 1) {
			prob = true;
		}
		time -= hours;
		int minutes = (int)(time *= 60.0D);
		time -= minutes;
		int seconds = (int)(time *= 60.0D);
		time -= seconds;
		cal.set(11, hours);
		cal.set(12, minutes);
		cal.set(13, seconds);
		cal.set(14, (int)(time * 1000.0D));
		if (prob == true);
		return cal.getTime();
	}

	public String toString() {
		return toXML();
	}

	public boolean equals(Object object) {
		if (this == object)
			return true; 
		if (!(object instanceof AstronomicalCalendar))
			return false; 
		AstronomicalCalendar aCal = (AstronomicalCalendar)object;
		return (getCalendar().equals(aCal.getCalendar()) && getGeoLocation().equals(aCal.getGeoLocation()) && getAstronomicalCalculator().equals(aCal.getAstronomicalCalculator()));
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + getClass().hashCode();
		result += 37 * result + getCalendar().hashCode();
		result += 37 * result + getGeoLocation().hashCode();
		result += 37 * result + getAstronomicalCalculator().hashCode();
		return result;
	}

	public GeoLocation getGeoLocation() {
		return this.geoLocation;
	}

	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
		getCalendar().setTimeZone(geoLocation.getTimeZone());
	}

	public AstronomicalCalculator getAstronomicalCalculator() {
		return this.astronomicalCalculator;
	}

	public void setAstronomicalCalculator(AstronomicalCalculator astronomicalCalculator) {
		this.astronomicalCalculator = astronomicalCalculator;
	}

	public Calendar getCalendar() {
		return this.calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public String toXML() {
		ZmanimFormatter formatter = new ZmanimFormatter(5, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String output = "<";
		if (getClass().getName().endsWith("AstronomicalCalendar")) {
			output = output + "AstronomicalTimes";
		} else if (getClass().getName().endsWith("ZmanimCalendar")) {
			output = output + "Zmanim";
		} 
		output = output + " date=\"" + df.format(getCalendar().getTime()) + "\"";
		output = output + " type=\"" + getClass().getName() + "\"";
		output = output + " algorithm=\"" + getAstronomicalCalculator().getCalculatorName() + "\"";
		output = output + " location=\"" + getGeoLocation().getLocationName() + "\"";
		output = output + " latitude=\"" + getGeoLocation().getLatitude() + "\"";
		output = output + " longitude=\"" + getGeoLocation().getLongitude() + "\"";
		output = output + " elevation=\"" + getGeoLocation().getElevation() + "\"";
		output = output + " timeZoneName=\"" + getGeoLocation().getTimeZone().getDisplayName() + "\"";
		output = output + " timeZoneID=\"" + getGeoLocation().getTimeZone().getID() + "\"";
		output = output + " timeZoneOffset=\"" + (getGeoLocation().getTimeZone().getOffset(getCalendar().getTimeInMillis()) / 3600000) + "\"";
		output = output + ">\n";
		Method[] theMethods = getClass().getMethods();
		String tagName = "";
		Object value = null;
		List dateList = new ArrayList();
		List durationList = new ArrayList();
		for (int i = 0; i < theMethods.length; i++) {
			if (includeMethod(theMethods[i])) {
				tagName = theMethods[i].getName().substring(3);
				try {
					value = theMethods[i].invoke(this, (Object[])null);
					if (value instanceof Date) {
						dateList.add(new Zman((Date)value, tagName));
					}
					else if (value instanceof Long) {
						durationList.add(new Zman((int)((Long)value).longValue(), tagName));
					} else {
						output = output + "<" + tagName + ">" + value + "</" + tagName + ">\n";
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			} 
		} 
		Collections.sort(dateList, Zman.DATE_ORDER); int j;
		for (j = 0; j < dateList.size(); j++) {
			Zman zman = (Zman)dateList.get(j);
			output = output + "\t<" + zman.getZmanLabel();
			output = output + ">";
			output = output + formatter.formatDateTime(zman.getZman(), getCalendar()) + "</" + zman.getZmanLabel() + ">\n";
		} 
		for (j = 0; j < durationList.size(); j++) {
			Zman zman = (Zman)durationList.get(j);
			output = output + "\t<" + zman.getZmanLabel();
			output = output + ">";
			output = output + formatter.format((int)zman.getDuration()) + "</" + zman.getZmanLabel() + ">\n";
		} 
		if (getClass().getName().endsWith("AstronomicalCalendar")) {
			output = output + "</AstronomicalTimes>";
		} else if (getClass().getName().endsWith("ZmanimCalendar")) {
			output = output + "</Zmanim>";
		} 
		return output;
	}

	public String toXML2() {
		ZmanimFormatter formatter = new ZmanimFormatter(5, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String output = "<";
		if (getClass().getName().endsWith("AstronomicalCalendar")) {
			output = output + "AstronomicalTimes";
		} else if (getClass().getName().endsWith("ZmanimCalendar")) {
			output = output + "Zmanim";
		} 
		output = output + " date=\"" + df.format(getCalendar().getTime()) + "\"";
		output = output + " type=\"" + getClass().getName() + "\"";
		output = output + " algorithm=\"" + getAstronomicalCalculator().getCalculatorName() + "\"";

		output = output + " location=\"" + getGeoLocation().getLocationName() + "\"";
		output = output + " latitude=\"" + getGeoLocation().getLatitude() + "\"";
		output = output + " longitude=\"" + getGeoLocation().getLongitude() + "\"";
		output = output + " elevation=\"" + getGeoLocation().getElevation() + "\"";
		output = output + " timeZoneName=\"" + getGeoLocation().getTimeZone().getDisplayName() + "\"";
		output = output + " timeZoneID=\"" + getGeoLocation().getTimeZone().getID() + "\"";
		output = output + " timeZoneOffset=\"" + (getGeoLocation().getTimeZone().getOffset(getCalendar().getTimeInMillis()) / 3600000) + "\"";
		output = output + ">\n";
		Method[] theMethods = getClass().getMethods();
		String tagName = "";
		Object value = null;
		for (int i = 0; i < theMethods.length; i++) {
			if (includeMethod(theMethods[i])) {
				tagName = theMethods[i].getName().substring(3);
				try {
					value = theMethods[i].invoke(this, (Object[])null);
					if (value instanceof Date) {
						output = output + "\t<" + tagName;
						output = output + ">";
						output = output + formatter.formatDateTime((Date)value, getCalendar()) + "</" + tagName + ">\n";
					}
					else if (value instanceof Long) {
						output = output + "\t<" + tagName;
						output = output + ">" + formatter.format((int)((Long)value).longValue()) + "</" + tagName + ">\n";
					}
					else {
						output = output + "<" + tagName + ">" + value + "</" + tagName + ">\n";
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			} 
		} 
		if (getClass().getName().endsWith("AstronomicalCalendar")) {
			output = output + "</AstronomicalTimes>";
		} else if (getClass().getName().endsWith("ZmanimCalendar")) {
			output = output + "</Zmanim>";
		} 
		return output;
	}

	private static boolean includeMethod(Method method) {
		List methodWhiteList = new ArrayList();
		List methodBlackList = new ArrayList();
		if (methodWhiteList.contains(method.getName()))
			return true; 
		if (methodBlackList.contains(method.getName())) {
			return false;
		}
		if ((method.getParameterTypes()).length > 0)
			return false; 
		if (!method.getName().startsWith("get")) {
			return false;
		}
		if (method.getReturnType().getName().endsWith("Date") || method.getReturnType().getName().endsWith("long"))
		{
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		String locationName = "Lakewood, NJ";
		double latitude = 40.095965D;
		double longitude = -74.22213D;
		latitude = 34.052187D;
		longitude = -118.243425D;
		TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
		timeZone = TimeZone.getTimeZone("GMT");
		GeoLocation location = new GeoLocation(locationName, latitude, longitude, timeZone);
		AstronomicalCalendar ac = new AstronomicalCalendar(location);
		System.out.println(ac);
	}
}