package generator;

import java.util.ArrayList;

public interface IGenerator {
	void start();

	void stop();

	void setActiveClusters(int paramInt);

	ArrayList<StationCluster> getStationClusters();

	int getActiveClusterCount();

	int getDisabledClusterCount();

	int getErrorClusterCount();

	long getMissingValueCount();

	long getPeakTempCount();

	long getWrittenClusters();
}