package net.sourceforge.zmanim;

import java.util.Date;
import net.sourceforge.zmanim.util.GeoLocation;

public class ZmanimCalendar
extends AstronomicalCalendar
{
	private static final long serialVersionUID = 1L;
	protected static final double ZENITH_16_POINT_1 = 106.1D;
	protected static final double ZENITH_8_POINT_5 = 98.5D;
	private double candleLightingOffset = 18.0D;

	public Date getTzais() {
		return getSunsetOffsetByDegrees(98.5D);
	}

	public Date getAlosHashachar() {
		return getSunriseOffsetByDegrees(106.1D);
	}

	public Date getAlos72() {
		return getTimeOffset(getSeaLevelSunrise(), -4320000L);
	}

	public Date getChatzos() {
		return getSunTransit();
	}

	public Date getSofZmanShmaGRA() {
		return getTimeOffset(getSunrise(), getShaahZmanisGra() * 3L);
	}

	public Date getSofZmanShmaMGA() {
		return getTimeOffset(getAlos72(), getShaahZmanisMGA() * 3L);
	}

	public Date getTzais72() {
		return getTimeOffset(getSeaLevelSunset(), 4320000L);
	}

	public Date getCandelLighting() {
		return getTimeOffset(getSunset(), -getCandleLightingOffset() * 60000.0D);
	}

	public Date getSofZmanTfilaGRA() {
		return getTimeOffset(getSunrise(), getShaahZmanisGra() * 4L);
	}

	public Date getSofZmanTfilaMGA() {
		return getTimeOffset(getAlos72(), getShaahZmanisMGA() * 4L);
	}

	public Date getMinchaGedola() {
		return getTimeOffset(getSunrise(), getShaahZmanisGra() * 6.5D);
	}

	public Date getMinchaKetana() {
		return getTimeOffset(getSunrise(), getShaahZmanisGra() * 9.5D);
	}

	public Date getPlagHamincha() {
		return getTimeOffset(getSunrise(), getShaahZmanisGra() * 10.75D);
	}

	public long getShaahZmanisGra() {
		return getTemporalHour();
	}

	public long getShaahZmanisMGA() {
		return getTemporalHour(getAlos72(), getTzais72());
	}

	public ZmanimCalendar(GeoLocation location) {
		super(location);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof ZmanimCalendar)) {
			return false;
		}
		ZmanimCalendar zCal = (ZmanimCalendar)object;
		return (getCalendar().equals(zCal.getCalendar()) && getGeoLocation().equals(zCal.getGeoLocation()) && getAstronomicalCalculator().equals(zCal.getAstronomicalCalculator()));
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + getClass().hashCode();
		result += 37 * result + getCalendar().hashCode();
		result += 37 * result + getGeoLocation().hashCode();
		result += 37 * result + getAstronomicalCalculator().hashCode();
		return result;
	}

	public double getCandleLightingOffset() {
		return this.candleLightingOffset;
	}

	public void setCandleLightingOffset(double candleLightingOffset) {
		this.candleLightingOffset = candleLightingOffset;
	}
}