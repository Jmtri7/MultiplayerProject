package Client;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client implements Runnable {
	private final int port;
	Socket socket;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	public final List<String> inbox = new ArrayList<>();
	private final Thread thread = new Thread(this);
	private boolean stop = false;
	public Client(int port) {
		this.port = port;
	}
	public void start() {
		this.thread.start();
	}
	public void stop() {
		this.stop = true;
	}
	public void run() {
		if(this.connect()) {
			while(!this.stop) {
				String message = this.receiveMessage();
				if(message != null) this.inbox.add(message);
			}
		}
		this.disconnect();
	}
	public boolean connect() {
		try {
			this.socket = new Socket("localhost", this.port);
			this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	public boolean sendMessage(String message) {
		try {
			this.printWriter.println(message);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	private String receiveMessage() {
		try {
			return this.bufferedReader.readLine();
		} catch(Exception e) {
			return null;
		}
	}
	public String getMessage() {
		if(!this.inbox.isEmpty()) return this.inbox.remove(0);
		else return null;
	}
	public void disconnect() {
		try {
			if(Objects.nonNull(this.bufferedReader)) this.bufferedReader.close();
			if(Objects.nonNull(this.printWriter)) this.printWriter.close();
			if(Objects.nonNull(this.socket)) this.socket.close();
		} catch (Exception ignored) {}
	}
}