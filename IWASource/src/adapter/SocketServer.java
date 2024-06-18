package adapter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import app.Application;

public class SocketServer implements Runnable{

	private ServerSocket serverSocket;
	
	public void startReceivers() {
        try {
        	int serverport = Application.getInstance().getSettings().getClient().getPort();
			this.serverSocket = new ServerSocket(serverport);
			while(true) {
				Socket newSocket = serverSocket.accept();
				new Thread(new ClusterHandler(newSocket)).start();
			}
        } catch (IOException e) {
//			e.printStackTrace();
		}		
	}
	
	public void stopReceivers() {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		startReceivers();
	}
}
