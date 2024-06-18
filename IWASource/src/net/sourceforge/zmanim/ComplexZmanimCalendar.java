package net.sourceforge.zmanim;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import net.sourceforge.zmanim.util.GeoLocation;

public class ComplexZmanimCalendar
extends ZmanimCalendar
{
	private static final long serialVersionUID = 1L;
	protected static final double ZENITH_5_POINT_95 = 95.95D;
	protected static final double ZENITH_7_POINT_083 = 97.0D;
	protected static final double ZENITH_10_POINT_2 = 100.2D;
	protected static final double ZENITH_11_DEGREES = 101.0D;
	protected static final double ZENITH_11_POINT_5 = 101.5D;
	protected static final double ZENITH_13_DEGREES = 103.0D;
	protected static final double ZENITH_19_POINT_8 = 109.8D;
	protected static final double ZENITH_26_DEGREES = 116.0D;

	public ComplexZmanimCalendar(GeoLocation location) {
		super(location);
	}

	public long getShaahZmanis19Point8Degrees() {
		return getTemporalHour(getAlos19Point8Degrees(), getTzais19Point8Degrees());
	}

	public long getShaahZmanis18Degrees() {
		return getTemporalHour(getAlos18Degrees(), getTzais18Degrees());
	}

	public long getShaahZmanis26Degrees() {
		return getTemporalHour(getAlos26Degrees(), getTzais26Degrees());
	}

	public long getShaahZmanis16Point1Degrees() {
		return getTemporalHour(getAlos16Point1Degrees(), getTzais16Point1Degrees());
	}

	public long getShaahZmanis60Minutes() {
		return getTemporalHour(getAlos60(), getTzais60());
	}

	public long getShaahZmanis72Minutes() {
		return getShaahZmanisMGA();
	}

	public long getShaahZmanis72MinutesZmanis() {
		return getTemporalHour(getAlos72Zmanis(), getTzais72Zmanis());
	}

	public long getShaahZmanis90Minutes() {
		return getTemporalHour(getAlos90(), getTzais90());
	}

	public long getShaahZmanis90MinutesZmanis() {
		return getTemporalHour(getAlos90Zmanis(), getTzais90Zmanis());
	}

	public long getShaahZmanis96MinutesZmanis() {
		return getTemporalHour(getAlos96Zmanis(), getTzais96Zmanis());
	}

	public long getShaahZmanis96Minutes() {
		return getTemporalHour(getAlos96(), getTzais96());
	}

	public long getShaahZmanis120Minutes() {
		return getTemporalHour(getAlos120(), getTzais120());
	}

	public long getShaahZmanis120MinutesZmanis() {
		return getTemporalHour(getAlos120Zmanis(), getTzais120Zmanis());
	}

	public Date getPlagHamincha120MinutesZmanis() {
		return getTimeOffset(getAlos120Zmanis(), getShaahZmanis120MinutesZmanis() * 10.75D);
	}

	public Date getPlagHamincha120Minutes() {
		return getTimeOffset(getAlos120(), getShaahZmanis120Minutes() * 10.75D);
	}

	public Date getAlos60() {
		return getTimeOffset(getSeaLevelSunrise(), -3600000L);
	}

	public Date getAlos72Zmanis() {
		long shaahZmanis = getShaahZmanisGra();
		if (shaahZmanis == Long.MIN_VALUE) {
			return null;
		}
		return getTimeOffset(getSeaLevelSunrise(), (long)(shaahZmanis * -1.2D));
	}

	public Date getAlos96() {
		return getTimeOffset(getSeaLevelSunrise(), -5760000L);
	}

	public Date getAlos90Zmanis() {
		long shaahZmanis = getShaahZmanisGra();
		if (shaahZmanis == Long.MIN_VALUE) {
			return null;
		}
		return getTimeOffset(getSeaLevelSunrise(), (long)(shaahZmanis * -1.5D));
	}

	public Date getAlos96Zmanis() {
		long shaahZmanis = getShaahZmanisGra();
		if (shaahZmanis == Long.MIN_VALUE) {
			return null;
		}
		return getTimeOffset(getSeaLevelSunrise(), (long)(shaahZmanis * -1.6D));
	}

	public Date getAlos90() {
		return getTimeOffset(getSeaLevelSunrise(), -5400000L);
	}

	public Date getAlos120() {
		return getTimeOffset(getSeaLevelSunrise(), -7200000L);
	}

	public Date getAlos120Zmanis() {
		long shaahZmanis = getShaahZmanisGra();
		if (shaahZmanis == Long.MIN_VALUE) {
			return null;
		}
		return getTimeOffset(getSeaLevelSunrise(), shaahZmanis * -2L);
	}

	public Date getAlos26Degrees() {
		return getSunriseOffsetByDegrees(116.0D);
	}

	public Date getAlos18Degrees() {
		return getSunriseOffsetByDegrees(108.0D);
	}

	public Date getAlos19Point8Degrees() {
		return getSunriseOffsetByDegrees(109.8D);
	}

	public Date getAlos16Point1Degrees() {
		return getSunriseOffsetByDegrees(106.1D);
	}

	public Date getMisheyakir11Point5Degrees() {
		return getSunriseOffsetByDegrees(101.5D);
	}

	public Date getMisheyakir11Degrees() {
		return getSunriseOffsetByDegrees(101.0D);
	}

	public Date getMisheyakir10Point2Degrees() {
		return getSunriseOffsetByDegrees(100.2D);
	}

	public Date getSofZmanShmaMGA19Point8Degrees() {
		return getTimeOffset(getAlos19Point8Degrees(), getShaahZmanis19Point8Degrees() * 3L);
	}

	public Date getSofZmanShmaMGA16Point1Degrees() {
		return getTimeOffset(getAlos16Point1Degrees(), getShaahZmanis16Point1Degrees() * 3L);
	}

	public Date getSofZmanShmaMGA72Minutes() {
		return getSofZmanShmaMGA();
	}

	public Date getSofZmanShmaMGA72MinutesZmanis() {
		return getTimeOffset(getAlos72Zmanis(), getShaahZmanis72MinutesZmanis() * 3L);
	}

	public Date getSofZmanShmaMGA90Minutes() {
		return getTimeOffset(getAlos90(), getShaahZmanis90Minutes() * 3L);
	}

	public Date getSofZmanShmaMGA90MinutesZmanis() {
		return getTimeOffset(getAlos90Zmanis(), getShaahZmanis90MinutesZmanis() * 3L);
	}

	public Date getSofZmanShmaMGA96Minutes() {
		return getTimeOffset(getAlos96(), getShaahZmanis96Minutes() * 3L);
	}

	public Date getSofZmanShmaMGA96MinutesZmanis() {
		return getTimeOffset(getAlos96Zmanis(), getShaahZmanis96MinutesZmanis() * 3L);
	}

	public Date getSofZmanShma3HoursBeforeChatzos() {
		return getTimeOffset(getChatzos(), -10800000L);
	}

	public Date getSofZmanShmaMGA120Minutes() {
		return getTimeOffset(getAlos120(), getShaahZmanis120Minutes() * 3L);
	}

	public Date getSofZmanShmaAlos16Point1ToSunset() {
		long shaahZmanis = getTemporalHour(getAlos16Point1Degrees(), getSunset());     
		return getTimeOffset(getAlos16Point1Degrees(), shaahZmanis * 3L);
	}
	public Date getSofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees() {
		long shaahZmanis = getTemporalHour(getAlos16Point1Degrees(), getTzaisGeonim7Point083Degrees());    
		return getTimeOffset(getAlos16Point1Degrees(), shaahZmanis * 3L);
	}

	public Date getSofZmanShmaKolEliyahu() {
		Date chatzos = getFixedLocalChatzos();
		if (chatzos == null || getSunrise() == null) {
			return null;
		}
		long diff = (chatzos.getTime() - getSunrise().getTime()) / 2L;
		return getTimeOffset(chatzos, -diff);
	}
	public Date getSofZmanTfilaMGA19Point8Degrees() {
		return getTimeOffset(getAlos19Point8Degrees(), getShaahZmanis19Point8Degrees() * 4L);
	}
	public Date getSofZmanTfilaMGA16Point1Degrees() {
		return getTimeOffset(getAlos16Point1Degrees(), getShaahZmanis16Point1Degrees() * 4L);
	}

	public Date getSofZmanTfilaMGA72Minutes() {
		return getSofZmanTfilaMGA();
	}

	public Date getSofZmanTfilaMGA72MinutesZmanis() {
		return getTimeOffset(getAlos72Zmanis(), getShaahZmanis72MinutesZmanis() * 4L);
	}

	public Date getSofZmanTfilaMGA90Minutes() {
		return getTimeOffset(getAlos90(), getShaahZmanis90Minutes() * 4L);
	}

	public Date getSofZmanTfilaMGA90MinutesZmanis() {
		return getTimeOffset(getAlos90Zmanis(), getShaahZmanis90MinutesZmanis() * 4L);
	}

	public Date getSofZmanTfilaMGA96Minutes() {
		return getTimeOffset(getAlos96(), getShaahZmanis96Minutes() * 4L);
	}

	public Date getSofZmanTfilaMGA96MinutesZmanis() {
		return getTimeOffset(getAlos96Zmanis(), getShaahZmanis96MinutesZmanis() * 4L);
	}

	public Date getSofZmanTfilaMGA120Minutes() {
		return getTimeOffset(getAlos120(), getShaahZmanis120Minutes() * 4L);
	}

	public Date getSofZmanTfila2HoursBeforeChatzos() {
		return getTimeOffset(getChatzos(), -7200000L);
	}

	public Date getMinchaGedola30Minutes() {
		return getTimeOffset(getChatzos(), 1800000L);
	}

	public Date getMinchaGedola72Minutes() {
		return getTimeOffset(getAlos72(), getShaahZmanis72Minutes() * 6.5D);
	}

	public Date getMinchaGedola16Point1Degrees() {
		return getTimeOffset(getAlos16Point1Degrees(), getShaahZmanis16Point1Degrees() * 6.5D);
	}

	public Date getMinchaGedolaGreaterThan30() {
		if (getMinchaGedola30Minutes() == null || getMinchaGedola() == null) {
			return null;
		}
		return (getMinchaGedola30Minutes().compareTo(getMinchaGedola()) > 0) ? getMinchaGedola30Minutes() : getMinchaGedola();
	}

	public Date getMinchaKetana16Point1Degrees() {
		return getTimeOffset(getAlos16Point1Degrees(), getShaahZmanis16Point1Degrees() * 9.5D);
	}

	public Date getMinchaKetana72Minutes() {
		return getTimeOffset(getAlos72(), getShaahZmanis72Minutes() * 9.5D);
	}

	public Date getPlagHamincha60Minutes() {
		return getTimeOffset(getAlos60(), getShaahZmanis60Minutes() * 10.75D);
	}

	public Date getPlagHamincha72Minutes() {
		return getTimeOffset(getAlos72(), getShaahZmanis72Minutes() * 10.75D);
	}

	public Date getPlagHamincha90Minutes() {
		return getTimeOffset(getAlos90(), getShaahZmanis90Minutes() * 10.75D);
	}

	public Date getPlagHamincha96Minutes() {
		return getTimeOffset(getAlos96(), getShaahZmanis96Minutes() * 10.75D);
	}

	public Date getPlagHamincha96MinutesZmanis() {
		return getTimeOffset(getAlos96Zmanis(), getShaahZmanis96MinutesZmanis() * 10.75D);
	}

	public Date getPlagHamincha90MinutesZmanis() {
		return getTimeOffset(getAlos90Zmanis(), getShaahZmanis90MinutesZmanis() * 10.75D);
	}

	public Date getPlagHamincha72MinutesZmanis() {
		return getTimeOffset(getAlos72Zmanis(), getShaahZmanis72MinutesZmanis() * 10.75D);
	}

	public Date getPlagHamincha16Point1Degrees() {
		return getTimeOffset(getAlos16Point1Degrees(), getShaahZmanis16Point1Degrees() * 10.75D);
	}

	public Date getPlagHamincha19Point8Degrees() {
		return getTimeOffset(getAlos19Point8Degrees(), getShaahZmanis19Point8Degrees() * 10.75D);
	}

	public Date getPlagHamincha26Degrees() {
		return getTimeOffset(getAlos26Degrees(), getShaahZmanis26Degrees() * 10.75D);
	}

	public Date getPlagHamincha18Degrees() {
		return getTimeOffset(getAlos18Degrees(), getShaahZmanis18Degrees() * 10.75D);
	}

	public Date getPlagAlosToSunset() {
		long shaahZmanis = getTemporalHour(getAlos16Point1Degrees(), getSunset());

		return getTimeOffset(getAlos16Point1Degrees(), shaahZmanis * 10.75D);
	}

	public Date getPlagAlos16Point1ToTzaisGeonim7Point083Degrees() {
		long shaahZmanis = getTemporalHour(getAlos16Point1Degrees(), getTzaisGeonim7Point083Degrees());
		return getTimeOffset(getAlos16Point1Degrees(), shaahZmanis * 10.75D);
	}

	public Date getBainHasmashosRT13Degrees() {
		return getSunsetOffsetByDegrees(103.0D);
	}

	public Date getBainHasmashosRT58Point5Minutes() {
		return getTimeOffset(getSeaLevelSunset(), 3510000.0D);
	}

	public Date getBainHasmashosRT13Point5MinutesBefore7Point083Degrees() {
		return getTimeOffset(getSunsetOffsetByDegrees(97.0D), -810000.0D);
	}

	public Date getBainHasmashosRT2Stars() {
		Date alos19Point8 = getAlos19Point8Degrees();
		Date sunrise = getSunrise();
		if (alos19Point8 == null || sunrise == null) {
			return null;
		}
		return getTimeOffset(getSunset(), (sunrise.getTime() - alos19Point8.getTime()) * 0.2777777777777778D);
	}

	public Date getTzaisGeonim5Point95Degrees() {
		return getSunsetOffsetByDegrees(95.95D);
	}

	public Date getTzaisGeonim7Point083Degrees() {
		return getSunsetOffsetByDegrees(97.0D);
	}

	public Date getTzaisGeonim8Point5Degrees() {
		return getSunsetOffsetByDegrees(98.5D);
	}

	public Date getTzais60() {
		return getTimeOffset(getSeaLevelSunset(), 3600000L);
	}

	public Date getTzais72Zmanis() {
		long shaahZmanis = getShaahZmanisGra();
		if (shaahZmanis == Long.MIN_VALUE) {
			return null;
		}
		return getTimeOffset(getSeaLevelSunset(), shaahZmanis * 1.2D);
	}

	public Date getTzais90Zmanis() {
		long shaahZmanis = getShaahZmanisGra();
		if (shaahZmanis == Long.MIN_VALUE) {
			return null;
		}
		return getTimeOffset(getSeaLevelSunset(), shaahZmanis * 1.5D);
	}

	public Date getTzais96Zmanis() {
		long shaahZmanis = getShaahZmanisGra();
		if (shaahZmanis == Long.MIN_VALUE) {
			return null;
		}
		return getTimeOffset(getSeaLevelSunset(), shaahZmanis * 1.6D);
	}

	public Date getTzais90() {
		return getTimeOffset(getSeaLevelSunset(), 5400000L);
	}

	public Date getTzais120() {
		return getTimeOffset(getSeaLevelSunset(), 7200000L);
	}

	public Date getTzais120Zmanis() {
		long shaahZmanis = getShaahZmanisGra();
		if (shaahZmanis == Long.MIN_VALUE) {
			return null;
		}
		return getTimeOffset(getSeaLevelSunset(), shaahZmanis * 2.0D);
	}

	public Date getTzais16Point1Degrees() {
		return getSunsetOffsetByDegrees(106.1D);
	}

	public Date getTzais26Degrees() {
		return getSunsetOffsetByDegrees(116.0D);
	}

	public Date getTzais18Degrees() {
		return getSunsetOffsetByDegrees(108.0D);
	}

	public Date getTzais19Point8Degrees() {
		return getSunsetOffsetByDegrees(109.8D);
	}

	public Date getTzais96() {
		return getTimeOffset(getSeaLevelSunset(), 5760000L);
	}

	public Date getFixedLocalChatzos() {
		Calendar noonCal = new GregorianCalendar(getCalendar().get(1), getCalendar().get(2), getCalendar().get(5), 12, 0);
		noonCal.setTimeInMillis(noonCal.getTimeInMillis() + getLocalTimeOffset());
		return noonCal.getTime();
	}

	public long getLocalTimeOffset() {
		double longitude = getGeoLocation().getLongitude();
		double nextLong = ((int)(longitude / 15.0D) * 15);
		if (longitude < 0.0D) {
			nextLong -= 15.0D;
		}
		return (long)((nextLong - longitude) * 4.0D * 60.0D * 1000.0D);
	}

	public Date getSofZmanShmaFixedLocal() {
		return getTimeOffset(getFixedLocalChatzos(), -10800000L);
	}

	public Date getSofZmanTfilaFixedLocal() {
		return getTimeOffset(getFixedLocalChatzos(), -7200000L);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof ComplexZmanimCalendar)) {
			return false;
		}
		ComplexZmanimCalendar cCal = (ComplexZmanimCalendar)object;
		return (getCalendar().equals(cCal.getCalendar()) && getGeoLocation().equals(cCal.getGeoLocation()) && getAstronomicalCalculator().equals(cCal.getAstronomicalCalculator()));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + getClass().hashCode();
		result += 37 * result + getCalendar().hashCode();
		result += 37 * result + getGeoLocation().hashCode();
		result += 37 * result + getAstronomicalCalculator().hashCode();
		return result;
	}

	public static void main(String[] args) {
		String locationName = "Lakewood, NJ";
		double latitude = 40.095965D;
		double longitude = -74.22213D;
		latitude = 34.052187D;
		longitude = -118.243425D;
		latitude = 79.997168D;
		longitude = -69.257812D;
		TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
		timeZone = TimeZone.getTimeZone("GMT");
		GeoLocation location = new GeoLocation(locationName, latitude, longitude, timeZone);
		ComplexZmanimCalendar czc = new ComplexZmanimCalendar(location);
		System.out.println(czc);
	}
}