package net.sourceforge.zmanim.util;
import java.util.TimeZone;

public class GeoLocation
{
	private double latitude;
	private double longitude;
	private String locationName;
	private TimeZone timeZone;
	private double elevation;

	public double getElevation() {
		return this.elevation;
	}

	public void setElevation(double elevation) {
		if (elevation < 0.0D) {
			throw new IllegalArgumentException("Elevation can not be negative");
		}
		this.elevation = elevation;
	}

	public GeoLocation(String name, double latitude, double longitude, TimeZone timeZone) {
		this(name, latitude, longitude, 0.0D, timeZone);
		setLocationName(name);
		setLatitude(latitude);
		setLongitude(longitude);
		setTimeZone(timeZone);
	}

	public GeoLocation(String name, double latitude, double longitude, double elevation, TimeZone timeZone) {
		setLocationName(name);
		setLatitude(latitude);
		setLongitude(longitude);
		setElevation(elevation);
		setTimeZone(timeZone);
	}

	public GeoLocation() {
		setLocationName("Greenwich, England");
		setLongitude(0.0D);
		setLatitude(51.4772D);
		setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public void setLatitude(double latitude) {
		if (latitude > 90.0D || latitude < -90.0D) {
			throw new IllegalArgumentException("Latitude must be between -90 and  90");
		}    
		this.latitude = latitude;
	}

	public void setLatitude(int degrees, int minutes, double seconds, String direction) {
		double tempLat = degrees + (minutes + seconds / 60.0D) / 60.0D;
		if (tempLat > 90.0D || tempLat < 0.0D) {
			throw new IllegalArgumentException("Latitude must be between 0 and  90. Use direction of S instead of negative.");
		}

		if (direction.equals("S")) {
			tempLat *= -1.0D;
		} else if (!direction.equals("N")) {
			throw new IllegalArgumentException("Latitude direction must be N or S");
		} 

		this.latitude = tempLat;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLongitude(double longitude) {
		if (longitude > 180.0D || longitude < -180.0D) {
			throw new IllegalArgumentException("Longitude must be between -180 and  180");
		}     
		this.longitude = longitude;
	}

	public void setLongitude(int degrees, int minutes, double seconds, String direction) {
		double longTemp = degrees + (minutes + seconds / 60.0D) / 60.0D;
		if (longTemp > 180.0D || this.longitude < 0.0D) {
			throw new IllegalArgumentException("Longitude must be between 0 and  180. Use the ");
		}

		if (direction.equals("W")) {
			longTemp *= -1.0D;
		} else if (!direction.equals("E")) {
			throw new IllegalArgumentException("Longitude direction must be E or W");
		} 

		this.longitude = longTemp;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(String name) {
		this.locationName = name;
	}

	public TimeZone getTimeZone() {
		return this.timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public String toXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<GeoLocation>\n");
		sb.append("\t<LocationName>").append(getLocationName()).append("</LocationName>\n");
		sb.append("\t<Latitude>").append(getLatitude()).append("&deg;").append("</Latitude>\n");
		sb.append("\t<Longitude>").append(getLongitude()).append("&deg;").append("</Longitude>\n");
		sb.append("\t<Elevation>").append(getElevation()).append(" Meters").append("</Elevation>\n");
		sb.append("\t<TimezoneName>").append(getTimeZone().getID()).append("</TimezoneName>\n");
		sb.append("\t<TimezoneGMTOffset>").append(getTimeZone().getRawOffset() / 3600000).append("</TimezoneGMTOffset>\n");
		sb.append("\t<TimezoneDSTOffset>").append(getTimeZone().getDSTSavings() / 3600000).append("</TimezoneDSTOffset>\n");
		sb.append("</GeoLocation>");
		return sb.toString();
	}

	public boolean equals(Object object) {
		if (this == object)
			return true; 
		if (!(object instanceof GeoLocation))
			return false; 
		GeoLocation geo = (GeoLocation)object;
		return (Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(geo.latitude) && Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(geo.longitude) && this.elevation == geo.elevation && ((this.locationName == null) ? (geo.locationName == null) : this.locationName.equals(geo.locationName)) && ((this.timeZone == null) ? (geo.timeZone == null) : this.timeZone.equals(geo.timeZone)));
	}

	public int hashCode() {
		int result = 17;
		long latLong = Double.doubleToLongBits(this.latitude);
		long lonLong = Double.doubleToLongBits(this.longitude);
		long elevLong = Double.doubleToLongBits(this.elevation);
		int latInt = (int)(latLong ^ latLong >>> 32L);
		int lonInt = (int)(lonLong ^ lonLong >>> 32L);
		int elevInt = (int)(elevLong ^ elevLong >>> 32L);
		result = 37 * result + getClass().hashCode();
		result += 37 * result + latInt;
		result += 37 * result + lonInt;
		result += 37 * result + elevInt;
		result += 37 * result + ((this.locationName == null) ? 0 : this.locationName.hashCode());
		result += 37 * result + ((this.timeZone == null) ? 0 : this.timeZone.hashCode());
		return result;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\nLocation Name:\t\t\t").append(getLocationName());
		sb.append("\nLatitude:\t\t\t").append(getLatitude()).append("&deg;");
		sb.append("\nLongitude:\t\t\t").append(getLongitude()).append("&deg;");
		sb.append("\nElevation:\t\t\t").append(getElevation()).append(" Meters");
		sb.append("\nTimezone Name:\t\t\t").append(getTimeZone().getID());
		sb.append("\nTimezone GMT Offset:\t\t").append(getTimeZone().getRawOffset() / 3600000);
		sb.append("\nTimezone DST Offset:\t\t").append(getTimeZone().getDSTSavings() / 3600000);
		return sb.toString();
	}
}