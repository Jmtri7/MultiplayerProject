package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client implements Runnable {
	public static void main(String[] args) {
		Client client = new Client(4444);
		client.start();
	}
	static final Scanner scanner = new Scanner(System.in);
	Socket socket;
	private final Thread thread = new Thread(this);
	private final int port;
	private PrintWriter printWriter;
	public Client(int port) {
		this.port = port;
	}
	public void start() {
		try {
			this.socket = new Socket("localhost", 4444);
			System.out.println();
			System.out.println("===============");
			System.out.println("CLIENT STARTED");
			System.out.println("===============");
			this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
			this.thread.start();
		} catch(Exception e) {
			System.out.println("No server started on port: " + this.port);
		}
	}
	public void run() {
		while(true) {

			System.out.print("\nSend a message: ");

			String userInput = Client.scanner.nextLine();

			if(userInput.equals("quit")) break;

			this.messageServer(userInput);
		}

		try {
			this.printWriter.close();
			this.socket.close();
			System.out.println("Socket closed!");
		} catch (Exception e) {
			System.out.println("Socket failed to close at port: " + this.port);
		}
	}
	public void messageServer(String message) {
		try {
			this.printWriter.println(message);
		} catch(Exception e) {
			System.out.println("Failed to send message!");
		}
	}
	private String getServerOutput() {
		// TODO : fix this
		try {
			BufferedReader requestReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println();
			System.out.println("=================");
			System.out.println("IP: " + socket.getInetAddress());
			System.out.println("=================");

			String line;
			while(requestReader.ready()) {
				line = requestReader.readLine();
				System.out.println(line);
			}
			System.out.println("=================");
		} catch (Exception e) {
			System.out.println("Failed to get data from Server!");
		}

		//Thread.sleep(10);
		return "";
	}
}