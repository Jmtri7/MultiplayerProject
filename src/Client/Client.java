package Client;

import java.io.*;
import java.net.*;

public class Client {
	private final int port;
	Socket socket;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	public Client(int port) {
		this.port = port;
	}
	public void connect() {
		try {
			this.socket = new Socket("localhost", this.port);
			this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch(Exception e) {
			System.out.println("No server started on port: " + this.port + "\n");
		}
	}
	public boolean sendMessageToServer(String message) {
		try {
			this.printWriter.println(message);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	private String getServerMessage() {
		try {
			if(this.bufferedReader.ready()) return this.bufferedReader.readLine();
			else return "";
		} catch(Exception e) {
			return null;
		}
	}
	public void disconnect() {
		try {
			this.bufferedReader.close();
			this.printWriter.close();
			this.socket.close();
			System.out.println("Client stopped!");
		} catch (Exception e) {
			System.out.println("Failed to stop client!");
		}
	}
}