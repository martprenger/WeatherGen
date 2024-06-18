package util;

import businessobject.StationModel;
import java.util.Calendar;

public class CalculationDayPart
{
	private Calendar startTime;
	private Calendar endTime;
	private float startTemp;
	private float endTemp;

	public CalculationDayPart(DayPart dayPart, StationModel stationModel) {
		Calendar p1StartTime, p1EndTime, p2StartTime, p2EndTime;
		float p1StartTemp, p1EndTemp, p2StartTemp, p2EndTemp, curTemp = stationModel.curTemp;
		Calendar now = stationModel.getCalendar();
		DataLoader dataLoader = stationModel.getDataLoader();
		int stn = stationModel.getStation().getStn();
		if (dayPart.isRising()) {
			p1StartTime = dayPart.startTime;
			p1EndTime = TimeUtil.fromProgress(0.85F, p1StartTime, dayPart.endTime);
			float p1StartTmin = dataLoader.getModelData(stn, p1StartTime.get(6) - 1).getField(11);
			p1EndTemp = dataLoader.getModelData(stn, p1EndTime.get(6) - 1).getField(11);
			p1StartTemp = p1StartTmin + (p1EndTemp - p1StartTmin) * 0.1F;
			p2StartTime = p1EndTime;
			p2EndTime = dayPart.endTime;
			p2StartTemp = p1EndTemp;
			float p2EndTmax = dataLoader.getModelData(stn, p2EndTime.get(6) - 1).getField(10);
			p2EndTemp = p2EndTmax - (p2EndTmax - dataLoader.getModelData(stn, p1EndTime.get(6) % 366).getField(11)) * 0.1F;
		} else {
			p1StartTime = dayPart.startTime;
			p1EndTime = TimeUtil.fromProgress(0.3F, p1StartTime, dayPart.endTime);
			float p1StartTmax = dataLoader.getModelData(stn, p1StartTime.get(6) - 1).getField(10);
			float p1EndTmin = dataLoader.getModelData(stn, p1StartTime.get(6) % 366).getField(11);
			p1StartTemp = p1StartTmax - (p1StartTmax - p1EndTmin) * 0.1F;
			p1EndTemp = p1StartTmax - (p1StartTmax - p1EndTmin) * 0.9F;
			p2StartTime = p1EndTime;
			p2EndTime = dayPart.endTime;
			p2StartTemp = p1EndTemp;
			p2EndTemp = p1EndTmin;
		} 
		if (p1StartTime.compareTo(now) <= 0 && now.compareTo(p1EndTime) <= 0) {
			this.startTime = p1StartTime;
			this.endTime = p1EndTime;
			this.startTemp = p1StartTemp;
			this.endTemp = p1EndTemp;
		} else {
			this.startTime = p2StartTime;
			this.endTime = p2EndTime;
			this.startTemp = p2StartTemp;
			this.endTemp = p2EndTemp;
		} 
		if (!Float.isNaN(curTemp)) {
			this.startTemp = curTemp;
		}
		this.endTemp *= (float)((Math.random() + 0.5D) / 2.0D);
	}

	public boolean contains(Calendar cal) {
		return (this.startTime.compareTo(cal) <= 0 && cal.compareTo(this.endTime) <= 0);
	}

	public float nextValue(Calendar now) {
		float progress = TimeUtil.getProgress(now, this.startTime, this.endTime);
		float factor = (float)Calc.f(progress);

		return this.startTemp + (this.endTemp - this.startTemp) * factor;
	}

	public float getStartTemp() {
		return this.startTemp;
	}

	public float getEndTemp() {
		return this.endTemp;
	}
}
