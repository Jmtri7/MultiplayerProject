package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

	static Scanner scanner = new Scanner(System.in);
	static Socket socket = null;
	static PrintWriter printWriter = null;
	static String userInput = null;

	public static void main(String[] args) throws Exception {
		System.out.println();
		System.out.println("===============");
		System.out.println("CLIENT STARTED");
		System.out.println("===============");

		connect();

		while(true) {

			System.out.print("\nSend a message: ");
			userInput = scanner.nextLine();

			if(userInput.equals("quit")) break;

			if(socket == null) connect();

			if(printWriter != null) {

				messageServer(userInput);

				// print server input

				InputStream input = socket.getInputStream();
				BufferedReader requestReader = new BufferedReader(new InputStreamReader(input));
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

				//Thread.sleep(10);
			}
		}

		if(socket != null) socket.close();
	}
	public static void connect() {
		try {
			socket = new Socket("localhost", 4444);
			printWriter = new PrintWriter(socket.getOutputStream(), true);
		} catch(Exception e) {
			System.out.println("\nConnection to server refused!");
		}
	}
	public static void messageServer(String message) {
		printWriter.println(message);
		printWriter.close();
	}
}