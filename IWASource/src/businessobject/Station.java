package businessobject;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.sourceforge.zmanim.AstronomicalCalendar;
import net.sourceforge.zmanim.util.GeoLocation;

public class Station
{
	public static final int INFO_DATA_SIZE = 28;
	private int stn;
	private GeoLocation location;
	private AstronomicalCalendar ac;

	public Station(ByteBuffer buffer) {
		this.stn = buffer.getInt();
		double latitude = buffer.getDouble();
		double longitude = buffer.getDouble();
		double elevation = buffer.getDouble();
		if (elevation < 0.0D) {
			elevation = 0.0D;
		}
		this.location = new GeoLocation(Integer.toString(this.stn), latitude, longitude, elevation, TimeZone.getTimeZone("UTC"));
		this.ac = new AstronomicalCalendar(this.location);
		this.ac.getCalendar().set(14, 0);
		this.ac.getCalendar().setTimeZone(TimeZone.getTimeZone("UTC"));
		//System.out.println("Station:" + stn + " location: " + location);
	}

	public int getStn() {
		return this.stn;
	}
	public void setStn(int stn) {
		this.stn = stn;
	}

	public GeoLocation getLocation() {
		return this.location;
	}

	public Calendar getSunrise(Calendar cal) {
		this.ac.setCalendar((Calendar)cal.clone());
		Date sunriseDate = this.ac.getSunrise();
		long sunrise = 0L;
		if (sunriseDate == null) {
			this.ac.getCalendar().setTimeInMillis(cal.getTimeInMillis());
			this.ac.getCalendar().set(11, 5);
			this.ac.getCalendar().set(12, 0);
			this.ac.getCalendar().set(13, 0);
			sunrise = this.ac.getCalendar().getTimeInMillis();
		} else {
			sunrise = sunriseDate.getTime();
		} 
		this.ac.getCalendar().setTimeInMillis(sunrise);
		this.ac.getCalendar().set(14, 0);
		return (Calendar)this.ac.getCalendar().clone();
	}

	public Calendar getSunset(Calendar cal) {
		this.ac.setCalendar((Calendar)cal.clone());
		Date sunsetDate = this.ac.getSunset();
		long sunset = 0L;
		if (sunsetDate == null) {
			this.ac.getCalendar().setTimeInMillis(cal.getTimeInMillis());
			this.ac.getCalendar().set(11, 16);
			this.ac.getCalendar().set(12, 0);
			this.ac.getCalendar().set(13, 0);
			sunset = this.ac.getCalendar().getTimeInMillis();
		} else {
			sunset = sunsetDate.getTime();
		} 
		this.ac.getCalendar().setTimeInMillis(sunset);
		this.ac.getCalendar().set(14, 0);
		return (Calendar)this.ac.getCalendar().clone();
	}
}