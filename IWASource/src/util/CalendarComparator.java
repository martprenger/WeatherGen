package util;

import java.util.Calendar;
import java.util.Comparator;

public class CalendarComparator
implements Comparator<Calendar>
{
	public int compare(Calendar o1, Calendar o2) {
		return Math.round(Math.signum(o1.compareTo(o2)));
	}
}
