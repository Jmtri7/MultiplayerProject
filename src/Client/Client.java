import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

	static Scanner scanner = new Scanner(System.in);
	static Boolean isQuit = false;

	public static void main(String[] args) throws Exception {
		System.out.println("");
		System.out.println("===============");
		System.out.println("CLIENT STARTED");
		System.out.println("===============");

		while(!isQuit) {

			try {
				Socket socket = new Socket("localhost", 4444);
			
	        	// send message

	        	System.out.println();
				System.out.println("Send a message: ");
				String message = scanner.nextLine();

				if(message.equals("quit")) isQuit = true;

				PrintWriter outputWriter = new PrintWriter(socket.getOutputStream(), true);
				outputWriter.println(message);
				outputWriter.close();

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
			catch(Exception e) {
				System.out.println("Connection to server refused!");
			}

		}

		socket.close();
	}
}