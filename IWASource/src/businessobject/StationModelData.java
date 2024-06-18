package businessobject;

public class StationModelData
extends StationData
{
	public static final int TMAX = 10;
	public static final int TMIN = 11;
	public static final int MXSPD = 12;
	private static final int FIELDS = 13;
	private int[] fhttProbability;

	public StationModelData() {
		super(FIELDS);

		this.fhttProbability = new int[4];
	}

	public int[] getFhttProbability() {
		return this.fhttProbability;
	}
}