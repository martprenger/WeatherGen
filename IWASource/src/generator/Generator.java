package generator;
import app.Application;
import businessobject.StationModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;
import java.util.TimeZone;
import timing.AccurateTimer;
import util.KeyValuePair;
import util.Log;
import util.RandomIterator;

public class Generator
implements IGenerator, ActionListener
{
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

	public Generator(ArrayList<StationModel> dataModels) {
		try {
			this.clientSelector = new ClientSelector();
		} catch (IOException e) {
			Log.ERROR.printf("Error initializing client selector: %s", new Object[] { e });
		} 
		initializeClusterList(dataModels);
		initializeClientManager();
		this.clusterClientList = new ArrayList<KeyValuePair<StationCluster, Client>>(this.clusterList.size());
		for (int i = 0; i < this.clusterList.size(); i++) {
			StationCluster cluster = this.clusterList.get(i);
			Client client = this.clientList.get(i);
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
		this.clientList = new ArrayList<Client>(this.clusterList.size());
		for (StationCluster cluster : this.clusterList) {
			Client client = new Client(cluster, Application.getInstance().getSettings().getClient(), this.clientSelector);
			this.clientList.add(client);
		} 
		this.clientManager = new ClientManager(this.clientList);
	}

	private void initializeClusterList(ArrayList<StationModel> dataModels) {
		this.clusterList = createClusterList(dataModels);
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		now.add(14, 2000);
		for (StationModel dataModel : dataModels) {
			dataModel.setStart(now,0);
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
		for (StationCluster cluster : this.clusterList) {
			for (StationModel dataModel : cluster.getStationModels()) {
				dataModel.calculateNext();
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
		RandomIterator<KeyValuePair<StationCluster, Client>> it = new RandomIterator(this.clusterClientList);
		while (it.hasNext()) {
			KeyValuePair<StationCluster, Client> clusterClient = (KeyValuePair<StationCluster, Client>)it.next();
			StationCluster cluster = (StationCluster)clusterClient.getKey();
			Client client = (Client)clusterClient.getValue();
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
		LinkedList<Client> writingClients = new LinkedList<Client>();
		for (Client client : this.clientList) {
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
			LinkedList<Client> writtenClients = new LinkedList<Client>();
			for (Client client : writingClients) {
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