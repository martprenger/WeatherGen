package generatorhttp;

import java.util.ArrayList;

public class HTTPSelector {

	private ArrayList<HTTPConnectionManager> connectionManagers;

	public HTTPSelector() {
		this.connectionManagers = new ArrayList<HTTPConnectionManager>();
	}

	public synchronized static HTTPSelector open() {
		return new HTTPSelector();
	}
	
	public synchronized void addConectionManager(HTTPConnectionManager connectionManager) {
		this.connectionManagers.add(connectionManager);
	}
	
	public synchronized ArrayList<HTTPConnectionManager> getConnectionManagers(){
		return (ArrayList<HTTPConnectionManager>) this.connectionManagers.clone();
	}

	public synchronized void remove(HTTPConnectionManager httpConnectionManager) {
		if((httpConnectionManager!=null)&&this.connectionManagers.contains(httpConnectionManager)) {
			this.connectionManagers.remove(httpConnectionManager);			
		}
	}
}
