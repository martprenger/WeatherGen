package generator;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import util.KeyValuePair;
import util.Log;

public class ClientSelector
implements Runnable
{
	private Selector selector = Selector.open();
	private ConcurrentHashMap<IClient, SocketChannel> clientChannelMap = new ConcurrentHashMap<IClient, SocketChannel>();
	private ConcurrentLinkedQueue<KeyValuePair<SocketChannel, IClient>> pendingConnections = new ConcurrentLinkedQueue<KeyValuePair<SocketChannel, IClient>>();
	private ArrayList<IClientListener> clientListenerList = new ArrayList<IClientListener>();
	public ClientSelector() throws IOException {
		Thread thread = new Thread(this, "ClientSelector-Thread");
		thread.setDaemon(true);
		thread.start();
	}

	public void run() {
		while (true) {
			try {
				this.selector.select(1000L);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			KeyValuePair<SocketChannel, IClient> pendingConnection;
			while ((pendingConnection = this.pendingConnections.poll()) != null) {
				SocketChannel channel = (SocketChannel)pendingConnection.getKey();
				IClient client = (IClient)pendingConnection.getValue();
				this.clientChannelMap.put(client, channel);
				try {
					channel.register(this.selector, 8, client);
				} catch (ClosedChannelException e) {
					Log.DEBUG.printf("Connection closed while connecting: %s", new Object[] { e });
				} 
			} 
			Set<SelectionKey> keys = this.selector.selectedKeys();
			Iterator<SelectionKey> i = keys.iterator();
			while (i.hasNext()) {
				SelectionKey key = i.next();
				i.remove();
				if (!key.isValid()) {
					disconnect(key);
					continue;
				} 
				boolean connecting = false;
				try {
					if (key.isConnectable()) {
						connecting = true;
						handleConnect(key);
						continue;
					} 
					if (key.isWritable()) {
						handleWrite(key);
					}
				}
				catch (IOException e) {
					String cause = null;
					if (connecting) { cause = "connecting"; }
					else { cause = "writing"; }
					Log.ERROR.printf("Error %s to client: %s", new Object[] { cause, e });
					handleError(key);
				} catch (Exception e) {
					String cause = null;
					if (connecting) { cause = "connecting"; }
					else { cause = "writing"; }
					Log.ERROR.printf("Unexpected exception while %s to client: %s", new Object[] { cause, e });
					handleError(key);
				} 
			} 
		} 
	}

	public synchronized void connectClient(IClient client, SocketAddress address) throws IOException {
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		channel.connect(address);
		this.clientChannelMap.put(client, channel);
		this.pendingConnections.add(new KeyValuePair(channel, client));
		this.selector.wakeup();
	}

	public synchronized void disconnectClient(IClient client) {
		SocketChannel channel = this.clientChannelMap.get(client);
		SelectionKey key = channel.keyFor(this.selector);
		if (key == null) {
			boolean removedPendingConnection = this.pendingConnections.remove(channel);
			if (!removedPendingConnection && channel != null) {
				key = channel.keyFor(this.selector);
				if (key != null) {
					disconnect(channel.keyFor(this.selector));
				}
			} 
		} else {
			disconnect(key);
		} 
	}

	public synchronized void setWritable(IClient client) {
		SocketChannel channel = this.clientChannelMap.get(client);
		try {
			SelectionKey key = channel.keyFor(this.selector);
			key.interestOps(key.interestOps() | 0x4);
			this.selector.wakeup();
		} catch (Exception e) {
			Log.ERROR.printf("Error registering client for writing: %s", new Object[] { e });
			if (channel != null) {
				SelectionKey key = channel.keyFor(this.selector);
				if (key != null) {
					handleError(key);
				}
			} 
		} 
	}

	public void addClientListener(IClientListener clientListener) {
		this.clientListenerList.add(clientListener);
	}

	private void handleConnect(SelectionKey key) throws IOException {
		key.interestOps(key.interestOps() & 0xFFFFFFF7);
		SocketChannel channel = (SocketChannel)key.channel();
		boolean connected = channel.finishConnect();
		if (connected) {
			notifyConnected(key);
		} else {
			key.interestOps(key.interestOps() | 0x8);
		} 
	}

	private void handleWrite(SelectionKey key) throws IOException {
		key.interestOps(key.interestOps() & 0xFFFFFFFB);
		SocketChannel channel = (SocketChannel)key.channel();
		IClient client = (IClient)key.attachment();
		ByteBuffer writeBuffer = client.getWriteBuffer();
		int writeSize = channel.write(writeBuffer);
		if (writeSize < 0) {
			handleError(key);
		}
		if (writeBuffer.hasRemaining()) {
			key.interestOps(key.interestOps() | 0x4);
		} else {
			notifyWriteComplete(key);
		} 
	}

	private void handleError(SelectionKey key) {
		notifyError(key);
		disconnect(key);
	}

	private void disconnect(SelectionKey key) {
		key.cancel();
		SocketChannel channel = (SocketChannel)key.channel();
		try {
			if (channel != null) {
				channel.close();
			}
		} catch (IOException iOException) {}
		notifyDisconnected(key);
		key.attach(null);
	}

	private void notifyConnected(SelectionKey key) {
		IClient client = (IClient)key.attachment();
		for (IClientListener clientListener : this.clientListenerList) {
			clientListener.onConnected(client);
		}
	}

	private void notifyWriteComplete(SelectionKey key) {
		IClient client = (IClient)key.attachment();
		for (IClientListener clientListener : this.clientListenerList) {
			clientListener.onWriteComplete(client);
		}
	}

	private void notifyError(SelectionKey key) {
		IClient client = (IClient)key.attachment();
		for (IClientListener clientListener : this.clientListenerList) {
			clientListener.onError(client);
		}
	}

	private void notifyDisconnected(SelectionKey key) {
		IClient client = (IClient)key.attachment();
		for (IClientListener clientListener : this.clientListenerList)
			clientListener.onDisconnected(client); 
	}
}