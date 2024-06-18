package generator;
import businessobject.StationModel;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import util.ByteUtil;

public class StationClusterWriter
{
	private static ByteBuffer header = createDirectByteBuffer("{\n\"WEATHERDATA\": [ {\"STN\":");
	private static ByteBuffer footer = createDirectByteBuffer("\n}]\n}\n");
	private static ByteBuffer seperator = createDirectByteBuffer("\n},\n{ \"STN\":");
	private static ByteBuffer stnDate = createDirectByteBuffer(",\"DATE\":\"");
	private static ByteBuffer dateTime = createDirectByteBuffer("\", \"TIME\":\"");
	private static ByteBuffer timeTemp = createDirectByteBuffer("\", \"TEMP\":");
	private static ByteBuffer tempDewp = createDirectByteBuffer(", \"DEWP\":");
	private static ByteBuffer dewpStp = createDirectByteBuffer(", \"STP\":");
	private static ByteBuffer stpSlp = createDirectByteBuffer(", \"SLP\":");
	private static ByteBuffer slpVisib = createDirectByteBuffer(", \"VISIB\":");
	private static ByteBuffer visibWdsp = createDirectByteBuffer(", \"WDSP\":");
	private static ByteBuffer wdspPrcp = createDirectByteBuffer(", \"PRCP\":");
	private static ByteBuffer prcpSndp = createDirectByteBuffer(", \"SNDP\":");
	private static ByteBuffer sndpFrshtt = createDirectByteBuffer(", \"FRSHTT\":\"");
	private static ByteBuffer frshttCldc = createDirectByteBuffer("\", \"CLDC\":");
	private static ByteBuffer cldcWddir = createDirectByteBuffer(", \"WNDDIR\":");	
	private static ByteBuffer none = createDirectByteBuffer(" \"None\" ");
	private static ByteBuffer noneString = createDirectByteBuffer("None");

	private static Random random = new Random();

	public static void writeCluster(StationCluster cluster, int targetTempPeaks, int targetMissingNodeValues) {
		ByteBuffer buffer = cluster.getWriteBuffer();
		ArrayList<StationModel> stationModelList = cluster.getStationModels();
		writeHeader(buffer);
		int tempPeaks = 0;
		int missingValues = 0;
		double tempPeakProbability = targetTempPeaks / 10.0D;
		double missingValuesProbability = targetMissingNodeValues / 10.0D;
		for (int i = 0; i < stationModelList.size(); i++) {
			if (i > 0) {
				writeSeperator(buffer);
			}
			boolean doTempPeak = (random.nextDouble() < tempPeakProbability);
			int j = (doTempPeak | (targetTempPeaks - tempPeaks == stationModelList.size() - i)) ? 1 : 0;
			j &= (tempPeaks < targetTempPeaks) ? 1 : 0;
			tempPeaks += (j != 0) ? 1 : 0;
			boolean doMissingValue = (random.nextDouble() < missingValuesProbability);
			int k = (doMissingValue | (targetMissingNodeValues - missingValues == stationModelList.size() - i)) ? 1 : 0;
			k &= (missingValues < targetMissingNodeValues) ? 1 : 0;
			missingValues += (k != 0) ? 1 : 0;
			StationModel stationModel = stationModelList.get(i);
			writeStationData(stationModel, buffer, (j==1), (k==1));
		} 
		writeFooter(buffer);
	}

	private static void writeStationData(StationModel stationModel, ByteBuffer buffer, boolean doTempPeak, boolean doMissingData) {
		ByteUtil.writeAsString(buffer, stationModel.getStation().getStn(), 6, 0, false);
		buffer.put(stnDate);
		stnDate.clear();
		Calendar cal = stationModel.getCalendar();
		ByteUtil.writeAsString(buffer, cal.get(1), 4, 0, true);
		buffer.put((byte)45);
		ByteUtil.writeAsString(buffer, (cal.get(2) + 1), 2, 0, true);
		buffer.put((byte)45);
		ByteUtil.writeAsString(buffer, cal.get(5), 2, 0, true);
		buffer.put(dateTime);
		dateTime.clear();
		ByteUtil.writeAsString(buffer, cal.get(11), 2, 0, true);
		buffer.put((byte)58);
		ByteUtil.writeAsString(buffer, cal.get(12), 2, 0, true);
		buffer.put((byte)58);
		ByteUtil.writeAsString(buffer, cal.get(13), 2, 0, true);
		buffer.put(timeTemp);
		timeTemp.clear();
		if (doTempPeak) {
			float increase = 0.25F + random.nextFloat() * 0.15F;
			ByteUtil.writeAsString(buffer, stationModel.curTemp * increase, 5, 1, false);
		} else {
			ByteUtil.writeAsString(buffer, stationModel.curTemp, 5, 1, false);
		} 
		buffer.put(tempDewp);
		tempDewp.clear();
		int missingDataIdx = doMissingData ? random.nextInt(10) : -1;
		if (missingDataIdx != 0)
			ByteUtil.writeAsString(buffer, stationModel.curDewp, 5, 1, false); 
		else {
			buffer.put(none);
			none.clear();
		}
		buffer.put(dewpStp);
		dewpStp.clear();
		if (missingDataIdx != 1)
			ByteUtil.writeAsString(buffer, stationModel.curStp, 5, 1, false);
		else {
			buffer.put(none);
			none.clear();
		}
		buffer.put(stpSlp);
		stpSlp.clear();
		if (missingDataIdx != 2)
			ByteUtil.writeAsString(buffer, stationModel.curSlp, 5, 1, false); 
		else {
			buffer.put(none);
			none.clear();
		}
		buffer.put(slpVisib);
		slpVisib.clear();
		if (missingDataIdx != 3)
			ByteUtil.writeAsString(buffer, stationModel.curVisib, 4, 1, false); 
		else {
			buffer.put(none);
			none.clear();
		}
		buffer.put(visibWdsp);
		visibWdsp.clear();
		if (missingDataIdx != 4)
			ByteUtil.writeAsString(buffer, stationModel.curWdsp, 4, 1, false); 
		else {
			buffer.put(none);
			none.clear();
		}
		buffer.put(wdspPrcp);
		wdspPrcp.clear();
		if (missingDataIdx != 5)
			ByteUtil.writeAsString(buffer, stationModel.curPrcp, 5, 2, false); 
		else {
			buffer.put(none);
			none.clear();
		}
		buffer.put(prcpSndp);
		prcpSndp.clear();
		if (missingDataIdx != 6)
			ByteUtil.writeAsString(buffer, stationModel.curSndp, 5, 1, false); 
		else {
			buffer.put(none);
			none.clear();
		}
		buffer.put(sndpFrshtt);
		sndpFrshtt.clear();
		if (missingDataIdx != 7) {
			for (int i = 0; i < stationModel.curFrshtt.length; i++) {
				buffer.put((byte)(stationModel.curFrshtt[i] ? 49 : 48));
			}
		}
		else {
			buffer.put(noneString);
			none.clear();
		}
		buffer.put(frshttCldc);
		frshttCldc.clear();
		if (missingDataIdx != 8)
			ByteUtil.writeAsString(buffer, stationModel.curCldc, 3, 1, false); 
		else {
			buffer.put(none);
			none.clear();
		}
		buffer.put(cldcWddir);
		cldcWddir.clear();
		if (missingDataIdx != 9)
			ByteUtil.writeAsString(buffer, stationModel.curWddir, 3, 0, false); 
		else {
			buffer.put(none);
			none.clear();
		}
	}

	private static void writeHeader(ByteBuffer buffer) {
		buffer.put(header);
		header.clear();
	}

	private static void writeFooter(ByteBuffer buffer) {
		buffer.put(footer);
		footer.clear();
	}

	private static void writeSeperator(ByteBuffer buffer) {
		buffer.put(seperator);
		seperator.clear();
	}

	private static ByteBuffer createDirectByteBuffer(String data) {
		byte[] bytes = data.getBytes();
		ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
		buffer.put(bytes);
		buffer.clear();
		return buffer;
	}
}