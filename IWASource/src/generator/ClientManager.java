package generator;
import java.util.ArrayList;
import util.RandomIterator;

public class ClientManager
{
	private ArrayList<Client> clientList;

	public ClientManager(ArrayList<Client> clientList) {
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
		for (Client client : this.clientList) {
			if (client.isActive()) {
				count++;
			}
		} 
		return count;
	}

	public int getDisabledClusterCount() {
		int count = 0;
		for (Client client : this.clientList) {
			if (!client.isActive()) {
				count++;
			}
		} 
		return count;
	}

	public int getErrorClusterCount() {
		int count = 0;
		for (Client client : this.clientList) {
			if (client.hasError()) {
				count++;
			}
		} 
		return count;
	}

	private void connectClients(int amount) {
		RandomIterator<Client> randomIterator = new RandomIterator(this.clientList);
		while (randomIterator.hasNext() && amount > 0) {
			Client client = randomIterator.next();
			if (!client.isActive()) {
				client.connect();
				amount--;
			} 
		} 
	}

	private void disconnectClients(int amount) {
		RandomIterator<Client> randomIterator = new RandomIterator(this.clientList);
		while (randomIterator.hasNext() && amount > 0) {
			Client client = randomIterator.next();
			if (client.isActive()) {
				client.disconnect();
				amount--;
			} 
		} 
	}
}