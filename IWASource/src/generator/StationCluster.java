package generator;
import businessobject.StationModel;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class StationCluster
implements IClient
{
	private static final int BUFFER_SIZE = 4096;
	private static ArrayList<Integer> idList = new ArrayList<Integer>();
	private static Random idRandom = new Random();
	private ArrayList<StationModel> stationModelList;
	private String name;
	private int id;
	private ByteBuffer buffer;

	public StationCluster(String name, ArrayList<StationModel> stationModelList) {
		this.stationModelList = stationModelList;
		this.name = name;
		this.id = getUid();
		this.buffer = ByteBuffer.allocateDirect(4096);
	}

	public int getId() {
		return this.id;
	}

	public ByteBuffer getWriteBuffer() {
		return this.buffer;
	}

	public ArrayList<StationModel> getStationModels() {
		return this.stationModelList;
	}

	public String getName() {
		return this.name;
	}

	public synchronized void prepareWriteBuffer(int tempPeaks, int missingNodeValues) {
		this.buffer.clear();
		StationClusterWriter.writeCluster(this, tempPeaks, missingNodeValues);
		this.buffer.flip();
	}

	public synchronized boolean checkNextWrite() {
		boolean nextWrite = true;
		for (StationModel station : this.stationModelList) {
			nextWrite = nextWrite & station.getCalculatedNext();
		}
		return nextWrite;		
	}
	
	public synchronized void clearNextWrite() {
		for (StationModel station : this.stationModelList) {
			station.setCalculatedNext(false);
		}
	}
	
	private static int getUid() {
		int uid;
		synchronized (idList) { do {  }
		while (idList.contains(Integer.valueOf(uid = idRandom.nextInt(2147483647))));
		idList.add(Integer.valueOf(uid)); }
		return uid;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IClient)) {
			return false;
		}
		IClient other = (IClient)obj;
		return (other.getId() == getId());
	}

	public int hashCode() {
		return getId();
	}
}