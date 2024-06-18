package generatorhttp;
import java.util.ArrayList;
import util.RandomIterator;

public class ClientHTTPManager
{
	private ArrayList<ClientHTTP> clientList;

	public ClientHTTPManager(ArrayList<ClientHTTP> clientList) {
		this.clientList = clientList;
	}

	public void setActiveClients(int amount) {
		int activeClients = getActiveClusterCount();
		amount -= activeClients;
		if (amount < 0) {
			disconnectClients(Math.abs(amount));
		} else if (amount > 0) {
			connectClients(amount);
		}
	}

	public int getActiveClusterCount() {
		int count = 0;
		for (ClientHTTP client : this.clientList) {
			if (client.isActive()) {
				count++;
			}
		} 
		return count;
	}

	public int getDisabledClusterCount() {
		int count = 0;
		for (ClientHTTP client : this.clientList) {
			if (!client.isActive()) {
				count++;
			}
		} 
		return count;
	}

	public int getErrorClusterCount() {
		int count = 0;
		for (ClientHTTP client : this.clientList) {
			if (client.hasError()) {
				count++;
			}
		} 
		return count;
	}

	private void connectClients(int amount) {
		RandomIterator<ClientHTTP> randomIterator = new RandomIterator(this.clientList);
		while (randomIterator.hasNext() && amount > 0) {
			ClientHTTP client = randomIterator.next();
			if (!client.isActive()) {
				client.connect();
				amount--;
			} 
		} 
	}

	private void disconnectClients(int amount) {
		RandomIterator<ClientHTTP> randomIterator = new RandomIterator(this.clientList);
		while (randomIterator.hasNext() && amount > 0) {
			ClientHTTP client = randomIterator.next();
			if (client.isActive()) {
				client.disconnect();
				amount--;
			} 
		} 
	}
}