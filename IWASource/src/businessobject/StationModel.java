package businessobject;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Random;

import app.Application;
import util.CalculationDayPart;
import util.CalendarComparator;
import util.DataLoader;
import util.DayPart;
import util.RandomWalk;
import util.TimeUtil;

public class StationModel
{
	public static final int DATA_SIZE = 26;
	public static final int TOTAL_DATA_SIZE = 9516;
	public static final int STATIONS = 8000;
	private Station station;
	private DataLoader dataLoader;
	private Random random;
	private Calendar calendar;
	private Calendar lastCalendar;
	private int interval;
	private boolean calculatedNext;
	public float curTemp;
	public float curDewp;
	public float curSlp;
	public float curStp;
	public float curVisib;
	public float curWdsp;
	public float curPrcp;
	public float curSndp;
	public float curCldc;
	public float curWddir;
	public boolean[] curFrshtt;
	private CalculationDayPart calculationDayPart;

	public StationModel(Station station, DataLoader dataLoader) {
		this.station = station;
		this.dataLoader = dataLoader;
		this.curFrshtt = new boolean[6];
		this.random = new Random();
	}

	public void setStart(Calendar now, int interval) {
		this.calendar = (Calendar)now.clone();
		this.interval = interval;
		this.curTemp = Float.NaN;
		this.curDewp = Float.NaN;
		this.calculationDayPart = getCalculationDayPart(now);
		this.calculatedNext = false;
	}

	public void calculateNext(Calendar now) {
		if (now.after(this.calendar)) {
			calculateNext();
			this.calculatedNext = true;
		} 
	}
	
	public boolean getCalculatedNext() {
		return this.calculatedNext;
	}
	
	public void setCalculatedNext(boolean calculatedNext) {
		this.calculatedNext = calculatedNext;
	}
	
	public void calculateNext() {
		this.interval = Application.getInstance().getSettings().getStationUpdateInterval();
		this.calendar.add(Calendar.SECOND, this.interval);
		if (!this.calculationDayPart.contains(this.calendar)) {
			this.calculationDayPart = getCalculationDayPart(this.calendar);
		}

		boolean newDay = false;
		if (this.lastCalendar != null && 
				this.lastCalendar.get(6) != this.calendar.get(6)) {
			newDay = true;
		}

		this.lastCalendar = (Calendar)this.calendar.clone();
		int day = this.calendar.get(6) - 1;
		int nextDay = (day + 1) % 366;
		float dayProgress = TimeUtil.getDayProgress(this.calendar);
		this.curTemp = this.calculationDayPart.nextValue(this.calendar);
		this.curTemp = RandomWalk.step(this.curTemp, Math.abs(this.calculationDayPart.getEndTemp() - this.calculationDayPart.getStartTemp()) / 20.0F);

		if (Float.isNaN(this.curDewp)) {
			doInitialValues(day, dayProgress);
		}

		int stn = this.station.getStn();
		StationModelData modelData = this.dataLoader.getModelData(stn, day);
		StationModelData nextModelData = this.dataLoader.getModelData(stn, nextDay);
		this.curDewp = this.calculationDayPart.nextValue(this.calendar) - modelData.getField(0) + modelData.getField(1);
		float offset = modelData.getField(0) - modelData.getField(1);
		float min = modelData.getField(11) - offset;
		float max = modelData.getField(10) - offset;
		this.curDewp = RandomWalk.step(this.curDewp, Math.abs(max - min) / 20.0F);
		if (this.curDewp >= this.curTemp) {
			this.curDewp = this.curTemp * 0.95F;
		}    
		this.curSlp = RandomWalk.step(this.curSlp, Math.abs(nextModelData.getField(2) - modelData.getField(2)) / 200.0F);
		this.curStp = RandomWalk.step(this.curStp, Math.abs(nextModelData.getField(3) - modelData.getField(3)) / 200.0F);
		this.curVisib = RandomWalk.step(this.curVisib, 0.05F + Math.abs(nextModelData.getField(4) - modelData.getField(4)) / 200.0F);
		if (this.curVisib < 0.0F) {
			this.curVisib = 0.0F;
		}
		min = 0.5F * modelData.getField(5);
		max = modelData.getField(12);
		this.curWdsp = RandomWalk.step(this.curWdsp, (min - max) / 200.0F);
		if (this.curWdsp < 0.0F) {
			this.curWdsp = 0.0F;
		} else if (this.curWdsp > 1.5D * max) {
			this.curWdsp = max * 1.5F;
		} 
		if (newDay) {
			this.curPrcp = 0.0F;
			this.curSndp = 0.0F;
		} 
		this.curPrcp = dayProgress * modelData.getField(6);
		this.curSndp = dayProgress * modelData.getField(7);
		this.curWddir = RandomWalk.step(this.curWddir, 0.33333334F);
		if (this.curWddir > 359.0F) {
			this.curWddir = 359.0F;
		} else if (this.curWddir < 0.0F) {
			this.curWddir = 0.0F;
		}      
		if (newDay) {
			Arrays.fill(this.curFrshtt, false);
		}     
		if (Math.round(this.curPrcp * 100.0F) >= 1) {
			this.curFrshtt[1] = true;
		}
		if (Math.round(this.curSndp * 10.0F) >= 1) {
			this.curFrshtt[2] = true;
		}
		if ((this.curFrshtt[1] || this.curFrshtt[2]) && this.curCldc < 50.0F) {
			this.curCldc = 50.0F;
		}
		this.curCldc = RandomWalk.step(this.curCldc, 0.033333335F);
		if (this.curCldc < 0.0F) {
			this.curCldc = 0.0F;
		} else if (this.curCldc > 99.9F) {
			this.curCldc = 99.9F;
		}
		int[] fhttProbability = modelData.getFhttProbability();
		if (this.curTemp < 0.0F) {
			this.curFrshtt[0] = true;
		}
		if (this.random.nextFloat() <= fhttProbability[1] / 360000.0F) {
			this.curFrshtt[3] = true;
		}
		if (this.random.nextFloat() <= fhttProbability[2] / 360000.0F) {
			this.curFrshtt[4] = true;
		}
		if (this.random.nextFloat() <= fhttProbability[3] / 360000.0F) {
			this.curFrshtt[5] = true;
		}
	}

	private void doInitialValues(int day, float dayProgress) {
		int nextDay = (day + 1) % 366;
		StationModelData modelData = this.dataLoader.getModelData(this.station.getStn(), day);
		StationModelData nextModelData = this.dataLoader.getModelData(this.station.getStn(), nextDay);
		this.curDewp = (float)(0.8D + this.random.nextDouble() * 0.2D) * this.curTemp;
		this.curSlp = modelData.getField(2) + (nextModelData.getField(2) - modelData.getField(2)) * dayProgress;
		this.curStp = modelData.getField(3) + (nextModelData.getField(3) - modelData.getField(3)) * dayProgress;
		this.curVisib = modelData.getField(4) + (nextModelData.getField(4) - modelData.getField(4)) * dayProgress;
		this.curWdsp = modelData.getField(5) + (nextModelData.getField(5) - modelData.getField(5)) * dayProgress;
		this.curPrcp = dayProgress * modelData.getField(6);
		this.curSndp = dayProgress * modelData.getField(7);
		this.curCldc = 99.9F * this.random.nextFloat();
		this.curWddir = this.random.nextFloat() * 360.0F;
	}

	private CalculationDayPart getCalculationDayPart(Calendar calendar) {
		this.calendar = (Calendar)calendar.clone();
		this.calendar.set(14, 0);
		Calendar sunrise = this.station.getSunrise(this.calendar);
		Calendar sunset = this.station.getSunset(this.calendar);
		Calendar lastDay = (Calendar)this.calendar.clone();
		lastDay.add(6, -1);
		Calendar lastSunrise = this.station.getSunrise(lastDay);
		Calendar lastSunset = this.station.getSunset(lastDay);
		Calendar nextDay = (Calendar)this.calendar.clone();
		nextDay.add(6, 1);
		Calendar nextSunrise = this.station.getSunrise(nextDay);
		Calendar nextSunset = this.station.getSunset(nextDay);
		Calendar[] sunrises = { lastSunrise, sunrise, nextSunrise };
		Calendar[] sunsets = { lastSunset, sunset, nextSunset };
		CalendarComparator calCmp = new CalendarComparator();
		Calendar[] sunTimes = new Calendar[sunrises.length + sunsets.length];
		System.arraycopy(sunrises, 0, sunTimes, 0, sunrises.length);
		System.arraycopy(sunsets, 0, sunTimes, sunrises.length, sunsets.length);
		Arrays.sort(sunTimes, (Comparator<? super Calendar>)calCmp);
		DayPart curDayPart = null;
		for (int i = 0; i < sunTimes.length; i++) {
			Calendar cal = sunTimes[i];
			if (cal.get(6) == this.calendar.get(6) || cal.get(6) == nextDay.get(6)) {
				Calendar dayPartStart = sunTimes[i - 1];
				int startPartType = (Arrays.binarySearch(sunrises, dayPartStart, (Comparator<? super Calendar>)calCmp) > 0) ? 0 : 1;
				int endPartType = (startPartType == 0) ? 1 : 0;
				DayPart dayPart = new DayPart(dayPartStart, startPartType, cal, endPartType);
				if (dayPart.contains(this.calendar)) {
					curDayPart = dayPart;
					break;
				} 
			} 
		} 
		return new CalculationDayPart(curDayPart, this);
	}

	public DataLoader getDataLoader() {
		return this.dataLoader;
	}

	public Station getStation() {
		return this.station;
	}

	public Calendar getCalendar() {
		return this.calendar;
	}
}