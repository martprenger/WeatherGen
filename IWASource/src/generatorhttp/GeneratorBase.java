package generatorhttp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import app.Application;
import generator.Client;
import generator.ClientManager;
import generator.ClientSelector;
import generator.IGenerator;
import generator.StationCluster;
import timing.AccurateTimer;
import util.KeyValuePair;

public abstract class GeneratorBase implements IGenerator, ActionListener {
	public static final int TOTAL_CLUSTERS = 800;
	public static final int CLUSTER_SIZE = 10;
	private static final long TIMER_INITIAL_WAIT = 2000L;
	private static final long TIMER_INTERVAL = 1000L;
	private AccurateTimer timer;
	private ClientSelector clientSelector;
	private ClientManager clientManager;
	private ArrayList<StationCluster> clusterList;
	private ArrayList<Client> clientList;
	private ArrayList<KeyValuePair<StationCluster, Client>> clusterClientList;
	private int requestedClusters;
	private Random random;
	private long peakTempCount;
	private long missingValueCount;
	private long writtenClusters;
	
	@Override
	public void start() {
		setActiveClusters(this.requestedClusters);
	}

	@Override
	public void stop() {
		int tmpRequestedClusters = this.requestedClusters;
		setActiveClusters(0);
		this.requestedClusters = tmpRequestedClusters;
	}

	@Override
	public void setActiveClusters(int amount) {
		this.requestedClusters = amount;
		this.clientManager.setActiveClients(amount);
	}

	@Override
	public ArrayList<StationCluster> getStationClusters() {
		return this.clusterList;
	}

	@Override
	public int getActiveClusterCount() {
		return this.clientManager.getActiveClusterCount();
	}

	@Override
	public int getDisabledClusterCount() {
		return this.clientManager.getDisabledClusterCount();
	}

	@Override
	public int getErrorClusterCount() {
		return this.clientManager.getDisabledClusterCount();
	}

	@Override
	public long getMissingValueCount() {
		return this.missingValueCount;
	}

	@Override
	public long getPeakTempCount() {
		return this.peakTempCount;
	}

	@Override
	public long getWrittenClusters() {
		return this.writtenClusters;
	}

//	public void actionPerformed(ActionEvent e) {
//		boolean logging = Application.getInstance().getSettings().loggingEnabled();
//		long totalStartTime = System.currentTimeMillis();
//		if (e.getSource() != this.timer) {
//			return;
//		}
//		long startTime = System.currentTimeMillis();
//		int writingCount = writeData();
//		long writeTime = System.currentTimeMillis() - startTime;
//		startTime = System.currentTimeMillis();
//		calculateNext();
//		long calcTime = System.currentTimeMillis() - startTime ;
//		startTime = System.currentTimeMillis();
//		waitForWriting(writingCount);
//		long waitTime = System.currentTimeMillis() - startTime;
//		long lastTime = this.timer.getTime();
//		if (lastTime + TIMER_INTERVAL > System.currentTimeMillis()) {
//			this.timer = new AccurateTimer(lastTime + TIMER_INTERVAL, this);
//		} else {
//			this.timer = new AccurateTimer(System.currentTimeMillis(), this);
//		} 
//		long totalEndTime = System.currentTimeMillis();
//		if (logging) {
//			System.out.println(System.currentTimeMillis());
//			System.out.println(String.format("Writing clients: %d (%s)", new Object[] { Integer.valueOf(writingCount), Long.valueOf(writeTime) }));
//			System.out.println("Calculation: " + (calcTime));
//			System.out.println("waitForWriting: " + (waitTime));
//			System.out.println("Total: " + (totalEndTime - totalStartTime));
//		}
//	}

}
