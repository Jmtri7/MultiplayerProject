package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client implements Runnable {
	static final Scanner scanner = new Scanner(System.in);
	private final int port;
	Socket socket;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	private final Thread thread = new Thread(this);
	private boolean quit = false;
	public Client(int port) {
		this.port = port;
	}
	public void start() {
		try {
			this.socket = new Socket("localhost", 4444);
			this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			System.out.println("===============");
			System.out.println("CLIENT STARTED");
			System.out.println("===============\n");
			this.thread.start();
		} catch(Exception e) {
			System.out.println("No server started on port: " + this.port + "\n");
		}
	}
	public void run() {
		while (!this.quit) {
			System.out.print("Send a message: ");
			String message = Client.scanner.nextLine();
			if(message.equals("quit")) {
				this.quit = true;
				return;
			}
			this.sendMessageToServer(message);
		}

		try {
			this.bufferedReader.close();
			this.printWriter.close();
			this.socket.close();
			System.out.println("Socket closed!");
		} catch (Exception e) {
			System.out.println("Socket failed to close at port: " + this.port);
		}
	}
	public void sendMessageToServer(String message) {
		try {
			this.printWriter.println(message);
		} catch(Exception e) {
			System.out.println("Failed to send message!");
		}
		System.out.println();
	}
	private String getServerMessage() {
		try {
			return this.bufferedReader.readLine();
		} catch(Exception e) {
			System.out.println("Failed to read server input");
			return "";
		}
	}
	public String getIP() {
		return String.valueOf(this.socket.getInetAddress());
	}
}