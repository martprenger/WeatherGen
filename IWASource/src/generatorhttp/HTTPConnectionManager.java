package generatorhttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

import generator.IClient;

public class HTTPConnectionManager {

	private HTTPSelector selector;
	private IClient client;
	private URL url;
	private HttpURLConnection httpConnection;
	private boolean isConnected;
	private boolean isValid;
	private boolean isWritable;
	
	public HTTPConnectionManager() {
		
	}

	public void register(HTTPSelector selector, IClient client) {
		this.selector = selector;
		this.client = client;
		this.isValid = true;
		this.isConnected = false;
		this.isWritable = false;
		this.selector.addConectionManager(this);
	}

	public boolean isValid() {
		return isValid;
	}

	public void setIsConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public void setIsWritable(boolean isWritable) {
		this.isWritable = isWritable;
	}
	
	public void cancel() {
		this.isValid = false;
		this.isConnected = false;
		this.selector.remove(this);
	}

	public IClient attachment() {
		return client;
	}

	public void attach(Object object) {
		if (object != null) {
			this.client = (IClient)object;			
		} else {
			this.client = null;
		}
	}

	public boolean isConnectable() {
		return ((this.client!=null) && (!this.isConnected));
	}

	public boolean isWritable() {
		return this.isWritable;
	}

	public void setConnectionURL(URL url) {
		this.url = url;
	}
	
	public HttpURLConnection getConnection() {
		return this.httpConnection;
	}
	
	public boolean openConnection() throws IOException  {
		this.httpConnection = (HttpURLConnection)this.url.openConnection();
		this.httpConnection.setRequestMethod("POST");
		this.httpConnection.setRequestProperty("Content-Type", "application/json; utf-8");
		this.httpConnection.setRequestProperty("Accept", "application/json");
		this.httpConnection.setDoOutput(true);
		return true;
	}

	public int sendPost(ByteBuffer writeBuffer) {
		this.isWritable = false;
		int written = 0;
		try(OutputStream os = this.httpConnection.getOutputStream()){
			String message = "";
			while (writeBuffer.hasRemaining()) {
				message += (char)writeBuffer.get();
			}
			byte[] input = message.getBytes();
			written = input.length;
			os.write(input, 0, written);
			this.httpConnection.getResponseCode();
			this.isConnected=false;
		} catch (IOException e) {
			written = -1;			
		} catch(Exception e) {
			written = -1;			
		}
		return written;
	}

	private void getResponse() throws IOException {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(this.httpConnection.getInputStream(), "utf-8"))){
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
		}		
	}
}
