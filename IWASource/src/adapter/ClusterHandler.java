package adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;

import app.Application;

public class ClusterHandler implements Runnable {

	private Socket cluster;
	private PostMessage postMessage;
	private int filterCount;
	private final int numberOfLinesPerMessage = 22;
	
	public ClusterHandler(Socket cluster) {
		this.cluster = cluster;
		this.postMessage = new PostMessage();
		this.filterCount = Application.getInstance().getSettings().getStationUpdateInterval();
	}
	
	@Override
	public void run() {
		try {
			BufferedReader weatherData = new BufferedReader(new InputStreamReader(cluster.getInputStream()));
			boolean active = true;
			String message = "";
			Random rand = new Random();
			int messageNr = rand.nextInt(filterCount);
			int numberOfLines = 0;
			while(active) {
				String nextLine = weatherData.readLine();
				numberOfLines++;
				active = (nextLine != null);
				if (active) {
					message = message + nextLine;
					if (numberOfLines==numberOfLinesPerMessage) {
						messageNr++;
						if (messageNr==this.filterCount) {
							this.postMessage.PostJSONMessage(message);
							messageNr=0;
						}
						message = "";
						numberOfLines = 0;
					}						
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
