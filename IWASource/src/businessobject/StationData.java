package businessobject;

public class StationData
{
	public static final int TEMP = 0;
	public static final int DEWP = 1;
	public static final int SLP = 2;
	public static final int STP = 3;
	public static final int VISIB = 4;
	public static final int WDSP = 5;
	public static final int PRCP = 6;
	public static final int SNDP = 7;
	public static final int CLDC = 8;
	public static final int WDDIR = 9;
	protected static final int FIELDS = 10;
	private float[] fields;
	private boolean[] frshtt;

	public StationData() {
		this(10);
	}

	protected StationData(int fields) {
		this.fields = new float[fields];
		this.frshtt = new boolean[6];
	}

	public float getField(int field) {
		return this.fields[field];
	}

	public void setField(int field, float value) {
		this.fields[field] = value;
	}

	public boolean[] getFrshtt() {
		return this.frshtt;
	}
}