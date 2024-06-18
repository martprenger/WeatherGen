package net.sourceforge.zmanim.util;

import java.util.Comparator;
import java.util.Date;

public class Zman
{
	private String zmanLabel;
	private Date zman;
	private long duration;
	private Date zmanDescription;

	public Zman(Date date, String label) {
		this.zmanLabel = label;
		this.zman = date;
	}

	public Zman(long duration, String label) {
		this.zmanLabel = label;
		this.duration = duration;
	}

	public Date getZman() {
		return this.zman;
	}

	public void setZman(Date date) {
		this.zman = date;
	}

	public long getDuration() {
		return this.duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getZmanLabel() {
		return this.zmanLabel;
	}

	public void setZmanLabel(String label) {
		this.zmanLabel = label;
	}

	public static final Comparator DATE_ORDER = new Comparator() {
		public int compare(Object o1, Object o2) {
			Zman z1 = (Zman)o1;
			Zman z2 = (Zman)o2;
			return z1.getZman().compareTo(z2.getZman());
		}
	};

	public static final Comparator NAME_ORDER = new Comparator() {
		public int compare(Object o1, Object o2) {
			Zman z1 = (Zman)o1;
			Zman z2 = (Zman)o2;
			return z1.getZmanLabel().compareTo(z2.getZmanLabel());
		}
	};

	public Date getZmanDescription() {
		return this.zmanDescription;
	}

	public void setZmanDescription(Date zmanDescription) {
		this.zmanDescription = zmanDescription;
	}
}
