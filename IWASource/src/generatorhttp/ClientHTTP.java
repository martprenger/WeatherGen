package generatorhttp;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;

import generator.IClient;
import generator.IClientListener;
import util.Log;

public class ClientHTTP
implements IClient, IClientListener
{
	private IClient client;
	private URL url;
	private ClientHTTPSelector selector;
	private boolean active;
	private boolean connected;
	private boolean writable;
	private boolean error;

	public ClientHTTP(IClient client, URL url, ClientHTTPSelector selector) {
		this.client = client;
		this.url = url;
		this.selector = selector;
		this.active = false;
		this.connected = false;
		this.writable = false;
		this.error = false;
		selector.addClientListener(this);
	}

	public boolean isActive() {
		return this.active;
	}

	public boolean isConnected() {
		return this.connected;
	}

	public boolean isWritable() {
		return this.writable;
	}

	public boolean isWriting() {
		return (this.active && this.connected && !this.writable);
	}

	public boolean hasError() {
		return this.error;
	}

	public void connect() {
		if (!this.active) {
			this.active = true;
			this.error = false;
			try {
				this.selector.connectClient(this, this.url);
			} catch (IOException e) {
				this.active = false;
				this.error = true;
				Log.ERROR.printf("Error registering client for connecting: %s", new Object[] { e });
			} 
		} 
	}

	public boolean write() {
		boolean writing = false;
		if (isWritable()) {
			this.writable = false;
			this.selector.setWritable(this);
			writing = true;
		} 
		return writing;
	}

	public void disconnect() {
		if (this.active) {
			this.selector.disconnectClient(this);
		}
	}

	public int getId() {
		return this.client.getId();
	}

	public ByteBuffer getWriteBuffer() {
		return this.client.getWriteBuffer();
	}

	public void onConnected(IClient client) {
		if (client != this) {
			return;
		}
		this.connected = true;
		this.writable = true;
		this.error = false;
	}

	public void onDisconnected(IClient client) {
		if (client != this) {
			return;
		}
		this.active = false;
		this.connected = false;
		this.writable = false;
	}

	public void onError(IClient client) {
		if (client != this) {
			return;
		}
		this.error = true;
	}

	public void onWriteComplete(IClient client) {
		if (client != this) {
			return;
		}
		this.writable = true;
	}

	public boolean equals(Object obj) {
		return this.client.equals(obj);
	}

	public int hashCode() {
		return this.client.hashCode();
	}
}