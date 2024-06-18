package adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import app.Application;
import app.Settings;

public class PostMessage {
	
	private URL url;
	private HttpURLConnection con;
	private boolean	debug;
	
	public PostMessage() {
		Settings settings = Application.getInstance().getSettings(); 
    	String host = settings.getHTTPHost();
    	int port = settings.getHTTPPort();
    	String path = settings.getHTTPPath();
    	this.debug = settings.httpLoggingEnabled();
		String urlString = "http://" + host + ":" + port +"/" + path;
		try {
			this.url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void PostJSONMessage (String message){
		try {
			con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);		
			try(OutputStream os = con.getOutputStream()){
				byte[] input = message.getBytes("utf-8");
				os.write(input, 0, input.length);			
				getResponse();
			} 
		} catch (IOException e) {
			System.out.println("Unable to send:" + message);
		}
	}

	private void getResponse() throws IOException {
		int code = con.getResponseCode();
		System.out.println(code);
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))){
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			System.out.println(response.toString());
		}		
	}
}
