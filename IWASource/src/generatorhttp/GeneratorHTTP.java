package generatorhttp;
import app.Application;
import businessobject.StationModel;
import generator.IGenerator;
import generator.StationCluster;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;
import java.util.TimeZone;
import timing.AccurateTimer;
import util.KeyValuePair;
import util.RandomIterator;

public class GeneratorHTTP
implements IGenerator, ActionListener
{
	public static final int TOTAL_CLUSTERS = 800;
	public static final int CLUSTER_SIZE = 10;
	private static final long TIMER_INITIAL_WAIT = 2000L;
	private static final long TIMER_INTERVAL = 1000L;
	private AccurateTimer timer;
	private int timer_interval;
	private ClientHTTPSelector clientSelector;
	private ClientHTTPManager clientManager;
	private ArrayList<StationCluster> clusterList;
	private ArrayList<ClientHTTP> clientList;
	private ArrayList<KeyValuePair<StationCluster, ClientHTTP>> clusterClientList;
	private int requestedClusters;
	private Random random;
	private long peakTempCount;
	private long missingValueCount;
	private long writtenClusters;

	public GeneratorHTTP(ArrayList<StationModel> dataModels) {
		this.clientSelector = new ClientHTTPSelector();
		initializeClusterList(dataModels);
		initializeClientManager();
		this.clusterClientList = new ArrayList<KeyValuePair<StationCluster, ClientHTTP>>(this.clusterList.size());
		for (int i = 0; i < this.clusterList.size(); i++) {
			StationCluster cluster = this.clusterList.get(i);
			ClientHTTP client = this.clientList.get(i);
			this.clusterClientList.add(new KeyValuePair(cluster, client));
		} 
		calculateNext();
		this.random = new Random();
		this.peakTempCount = 0L;
		this.missingValueCount = 0L;
		this.requestedClusters = 800;
		this.writtenClusters = 0L;
		this.timer = new AccurateTimer(System.currentTimeMillis() + TIMER_INITIAL_WAIT, this);
	}
	
	private void initializeClientManager() {
		this.clientList = new ArrayList<ClientHTTP>(this.clusterList.size());
		for (StationCluster cluster : this.clusterList) {
			ClientHTTP client = new ClientHTTP(cluster, Application.getInstance().getSettings().getHTTPClient(), this.clientSelector);
			this.clientList.add(client);
		} 
		this.clientManager = new ClientHTTPManager(this.clientList);
	}

	private void initializeClusterList(ArrayList<StationModel> dataModels) {
		this.clusterList = createClusterList(dataModels);
		this.timer_interval = Application.getInstance().getSettings().getStationUpdateInterval();
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		now.add(14, (int) this.TIMER_INITIAL_WAIT);
		Random r = new Random();
		for (StationCluster cluster : this.clusterList) {
			Calendar stationstart = (Calendar) now.clone();
			int step = r.nextInt(this.timer_interval)*1000;
			stationstart.add(14, step);
			for (StationModel dataModel : cluster.getStationModels()) {
				dataModel.setStart(stationstart, this.timer_interval);
			}			
		}
	}

	private ArrayList<StationCluster> createClusterList(ArrayList<StationModel> stationModels) {
		ArrayList<StationCluster> clusterList = new ArrayList<StationCluster>();
		for (int i = 0; i < stationModels.size(); i += 4) {
			ArrayList<StationModel> clusterModelList = new ArrayList<StationModel>(stationModels.subList(i, Math.min(i + 4, stationModels.size())));
			String clusterName = String.format("Cluster-%03d", i / 4 + 1);
			clusterList.add(new StationCluster(clusterName, clusterModelList));
		}
		return clusterList;
	}

	public void setActiveClusters(int amount) {
		this.requestedClusters = amount;
		this.clientManager.setActiveClients(amount);
	}

	public void start() {
		setActiveClusters(this.requestedClusters);
	}

	public void stop() {
		int tmpRequestedClusters = this.requestedClusters;
		setActiveClusters(0);
		this.requestedClusters = tmpRequestedClusters;
	}

	private void calculateNext() {
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		for (StationCluster cluster : this.clusterList) {
			for (StationModel dataModel : cluster.getStationModels()) {
				dataModel.calculateNext(now);
			}
		}
	}

	private int writeData() {
		int peakTempProbability = Application.getInstance().getSettings().getPeakTempProbability();
		int missingDataProbability = Application.getInstance().getSettings().getMissingDataProbability();
		int minimalPeakTempCount = Application.getInstance().getSettings().getMinimalPeakTempCount();
		int minimalMissingDataCount = Application.getInstance().getSettings().getMinimalMissingDataCount();
		double peakProbability = peakTempProbability / 100.0D * 10.0D;
		if (peakProbability > 1.0D) peakProbability = 1.0D;
		double missingProbability = missingDataProbability / 100.0D * 10.0D;
		if (missingProbability > 1.0D) missingProbability = 1.0D;
		int peakTemps = 0;
		int missingValues = 0;
		int writingCount = 0;
		RandomIterator<KeyValuePair<StationCluster, ClientHTTP>> it = new RandomIterator(this.clusterClientList);
		while (it.hasNext()) {
			KeyValuePair<StationCluster, ClientHTTP> clusterClient = (KeyValuePair<StationCluster, ClientHTTP>)it.next();
			StationCluster cluster = (StationCluster)clusterClient.getKey();
			if (cluster.checkNextWrite()) {
				ClientHTTP client = (ClientHTTP)clusterClient.getValue();
				boolean doPeakTemp = (this.random.nextDouble() < peakProbability);
				int i = (doPeakTemp | (peakTemps < minimalPeakTempCount)) ? 1 : 0;
				boolean doMissingValue = (this.random.nextDouble() < missingProbability);
				int j = (doMissingValue | (missingValues < minimalMissingDataCount)) ? 1 : 0;
				if (client.isWritable()) {
					int peakTempAmount = (i != 0) ? 1 : 0;
					int missingValueAmount = (j != 0) ? 1 : 0;
					cluster.prepareWriteBuffer(peakTempAmount, missingValueAmount);
					boolean writing = client.write();
					if (writing) {
						writingCount++;
						peakTemps += peakTempAmount;
						this.peakTempCount += peakTempAmount;
						missingValues += missingValueAmount;
						this.missingValueCount += missingValueAmount;
					} 
				}
				cluster.clearNextWrite();
			}
		} 
		return writingCount;
	}

	public void actionPerformed(ActionEvent e) {
		boolean logging = Application.getInstance().getSettings().loggingEnabled();
		long totalStartTime = System.currentTimeMillis();
		if (e.getSource() != this.timer) {
			return;
		}
		long startTime = System.currentTimeMillis();
		int writingCount = writeData();
		long writeTime = System.currentTimeMillis() - startTime;
		startTime = System.currentTimeMillis();
		calculateNext();
		long calcTime = System.currentTimeMillis() - startTime ;
		startTime = System.currentTimeMillis();
		waitForWriting(writingCount);
		long waitTime = System.currentTimeMillis() - startTime;
		long lastTime = this.timer.getTime();
		if (lastTime + TIMER_INTERVAL > System.currentTimeMillis()) {
			this.timer = new AccurateTimer(lastTime + TIMER_INTERVAL, this);
		} else {
			this.timer = new AccurateTimer(System.currentTimeMillis(), this);
		} 
		long totalEndTime = System.currentTimeMillis();
		if (logging) {
			System.out.println(System.currentTimeMillis());
			System.out.println(String.format("Writing clients: %d (%s)", new Object[] { Integer.valueOf(writingCount), Long.valueOf(writeTime) }));
			System.out.println("Calculation: " + (calcTime));
			System.out.println("waitForWriting: " + (waitTime));
			System.out.println("Total: " + (totalEndTime - totalStartTime));
		}
	}
	
	private void waitForWriting(int writingClientCount) {
		LinkedList<ClientHTTP> writingClients = new LinkedList<ClientHTTP>();
		for (ClientHTTP client : this.clientList) {
			if (client.isWriting()) {
				writingClients.add(client);
			}
		} 
		this.writtenClusters += (writingClientCount - writingClients.size());
		while (!writingClients.isEmpty()) {
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			LinkedList<ClientHTTP> writtenClients = new LinkedList<ClientHTTP>();
			for (ClientHTTP client : writingClients) {
				if (!client.isWriting()) {
					writtenClients.add(client);
					this.writtenClusters++;
				} 
			} 
			writingClients.removeAll(writtenClients);
			writtenClients.clear();
		} 
	}

	public ArrayList<StationCluster> getStationClusters() {
		return this.clusterList;
	}

	public int getActiveClusterCount() {
		return this.clientManager.getActiveClusterCount();
	}

	public int getDisabledClusterCount() {
		return this.clientManager.getDisabledClusterCount();
	}

	public int getErrorClusterCount() {
		return this.clientManager.getErrorClusterCount();
	}

	public long getMissingValueCount() {
		return this.missingValueCount;
	}

	public long getPeakTempCount() {
		return this.peakTempCount;
	}

	public long getWrittenClusters() {
		return this.writtenClusters;
	}
}