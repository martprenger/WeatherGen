package util;

import businessobject.Station;
import businessobject.StationModelData;
import net.sourceforge.zmanim.util.GeoLocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import app.Application;

public class DataLoader
implements Runnable
{
	private FileChannel fileChannel;
	private ByteBuffer dataReadBuffer = ByteBuffer.allocateDirect(26);
	private Object backgroundLoadingWaitObj;
	private boolean notified;
	private static final int BUFFER_SIZE = 3;
	private static final int CUR_DAY_OFFSET = 1;
	private int[] stationIdxMap;
	private ArrayList<Station> stationList;
	private StationModelData[][] modelDataSet;
	private int currentDay;
	private BufferedWriter writer;

	public DataLoader(File dataFile) {
		try {
			this.fileChannel = (new FileInputStream(dataFile)).getChannel();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.ERROR.println("Error: StationModel datafile not found.");
		} 
		this.currentDay = 0;
		this.modelDataSet = null;
		this.stationIdxMap = null;
	}

	public void start(int day) {
		this.currentDay = day;
		this.stationList = loadStations();
		initialize();
		this.backgroundLoadingWaitObj = new Object();
		this.notified = false;
		Thread thread = new Thread(this, "DataLoader-Thread");
		thread.setDaemon(true);
		thread.start();
	}

	public void run() {
		while (true) {
			StationModelData[] tempModelDataArray;
			synchronized (this.backgroundLoadingWaitObj) {
				try {
					this.backgroundLoadingWaitObj.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			} 

			synchronized (this.modelDataSet) {
				tempModelDataArray = this.modelDataSet[0];
				for (int i = 0; i < 5; i++) {
					this.modelDataSet[i] = this.modelDataSet[i + 1];
				}
				this.currentDay = (this.currentDay + 1) % 366;
			} 
			this.modelDataSet[5] = tempModelDataArray;
			load(this.currentDay + 6 - 1, 5);
			this.notified = false;
		} 
	}

	private void initialize() {
		this.modelDataSet = new StationModelData[6][8000];
		for (int i = 0; i < 6; i++) {
			for (int ix = 0; ix < 8000; ix++) {
				this.modelDataSet[i][ix] = new StationModelData();
			}
			int day = (this.currentDay + i - 1 + 366) % 366;
			load(day, i);
		} 
	}

	private void load(int day, int dataSetOffset) {
		try {
			int offset = 224000;
			offset += 26 * day;
			for (int i = 0; i < 8000; i++) {
				this.fileChannel.position(offset);
				this.dataReadBuffer.clear();
				while (this.dataReadBuffer.hasRemaining()) {
					this.fileChannel.read(this.dataReadBuffer);
				}
				this.dataReadBuffer.flip();
				StationModelData modelData = this.modelDataSet[dataSetOffset][i];
				fillStationModelData(modelData, this.dataReadBuffer);
				offset += 9516;
			} 
		} catch (IOException e) {
			e.printStackTrace();
			Log.ERROR.printf("Error reading from datafile: %s", new Object[] { e });
		} 
	}

	private void fillStationModelData(StationModelData modelData, ByteBuffer buffer) {
		modelData.setField(0, ByteUtil.getShortAsFloat(buffer, 1));
		modelData.setField(10, ByteUtil.getShortAsFloat(buffer, 1));
		modelData.setField(11, ByteUtil.getShortAsFloat(buffer, 1));
		modelData.setField(1, ByteUtil.getShortAsFloat(buffer, 1));
		modelData.setField(2, ByteUtil.getShortAsFloat(buffer, 1));
		modelData.setField(3, ByteUtil.getShortAsFloat(buffer, 1));
		modelData.setField(4, ByteUtil.getShortAsFloat(buffer, 1));
		modelData.setField(5, ByteUtil.getShortAsFloat(buffer, 1));
		modelData.setField(12, ByteUtil.getShortAsFloat(buffer, 1));
		modelData.setField(6, ByteUtil.getShortAsFloat(buffer, 2));
		modelData.setField(7, ByteUtil.getShortAsFloat(buffer, 1));
		int[] fhttProbability = modelData.getFhttProbability();
		fhttProbability[0] = buffer.get();
		fhttProbability[1] = buffer.get();
		fhttProbability[2] = buffer.get();
		fhttProbability[3] = buffer.get();
	}

	private ArrayList<Station> loadStations() {
		ByteBuffer infoBuffer = ByteBuffer.allocate(224000);
		boolean writeList = Application.getInstance().getSettings().createListEnabled();
		try {
			this.fileChannel.position(0L);
			while (infoBuffer.hasRemaining()) {
				this.fileChannel.read(infoBuffer);
			}
			infoBuffer.flip();
		} catch (IOException e) {
			e.printStackTrace();
			Log.ERROR.printf("Error reading from datafile: %s", new Object[] { e });
		} 
		ArrayList<Station> stationList = new ArrayList<Station>(8000);
		this.stationIdxMap = new int[999999];
		Arrays.fill(this.stationIdxMap, -1);

		ArrayList<Integer> stations = new ArrayList<Integer>();
		stations.add(117870);
		stations.add(117820);
		stations.add(117660);
		stations.add(117740);
		stations.add(117300);
		stations.add(117350);
		stations.add(117480);
		stations.add(117100);
		stations.add(117230);
		stations.add(116790);
		stations.add(116830);
		stations.add(116920);
		stations.add(116930);
		stations.add(116980);
		stations.add(116690);
		stations.add(116590);
		stations.add(116520);
		stations.add(116480);
		stations.add(116430);
		stations.add(116360);
		stations.add(116280);
		stations.add(116240);
		stations.add(116030);
		stations.add(115670);
		stations.add(115380);
		stations.add(115410);
		stations.add(115400);
		stations.add(115200);
		stations.add(115090);
		stations.add(115180);
		stations.add(115020);
		stations.add(114870);
		stations.add(114060);
		stations.add(114140);
		stations.add(114380);
		stations.add(114180);
		stations.add(114230);
		stations.add(114232);
		stations.add(114234);
		stations.add(114570);
		stations.add(114640);
		stations.add(114480);
		stations.add(114500);
		stations.add(125100);

		if (writeList) createListFile();
		for (int i = 0; i < stations.size(); i++) {
			Station station = new Station(infoBuffer);
			station.setStn(stations.get(i));
			stationList.add(station);
			this.stationIdxMap[station.getStn()] = i;
			if (writeList) writeListLine(station, i);
		} 	    
		if (writeList) closeListFile();
		return stationList;
	}

	private void createListFile() {
		try {
			writer = new BufferedWriter(new FileWriter("StationList.txt", true));
		    writer.append("{\"stationdata\": [\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closeListFile() {
	    try {
			writer.append("]\n}\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeListLine(Station station, int index) {
	    try {
			if (index>0) {
				writer.append(",\n");
			}
			String stationtext = toJsonLocation(station.getLocation());
			writer.append(stationtext);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	private String toJsonLocation(GeoLocation location) {
		String jsonLocation = "{";
		jsonLocation += "\"name\": \"" + location.getLocationName() + "\",";
		jsonLocation += "\"long\": " + location.getLongitude() + ",";
		jsonLocation += "\"lat\": " + location.getLatitude() + ",";
		jsonLocation += "\"elev\": " + location.getElevation() + "}";		
		return jsonLocation;
	}
	
	private int getIndexForStation(int stn) {
		return this.stationIdxMap[stn];
	}

	public ArrayList<Station> getStations() {
		return this.stationList;
	}

	public StationModelData getModelData(int stn, int day) {
		synchronized (this.modelDataSet) {
			int stationIdx = getIndexForStation(stn);
			int dayIdx = day - this.currentDay + 1;
			if (dayIdx > 3 && !this.notified) {
				synchronized (this.backgroundLoadingWaitObj) {
					this.backgroundLoadingWaitObj.notify();
					this.notified = true;
				} 
			}
			return this.modelDataSet[dayIdx][stationIdx];
		} 
	}
}
