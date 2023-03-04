package Server;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
	private final int port;
	private final List<String> whiteList = new ArrayList<>();
	private final User[] users = new User[2];
	private final Thread thread = new Thread(this);
	private ServerSocket serverSocket;
	public Server(int port) {
		this.port = port;
	}
	public void start() {
		try {
			serverSocket = new ServerSocket(this.port);
			System.out.println();
			System.out.println("===============");
			System.out.println("SERVER STARTED");
			System.out.println("===============\n");
			this.thread.start();
		} catch (Exception e) {
			System.out.println("Failed to open server socket with port: " + this.port);
		}
	}
	public void run() {
		while (true) {

			User user;
			try {
				user = new User(serverSocket.accept());
			} catch (Exception e) {
				System.out.println("Failed to find a queued connection!");
				continue;
			}

			String ip = user.getIP();
			System.out.println(ip + " connecting!");

			if (!isWhiteListed(ip)) {
				System.out.println(ip + " is not white-listed!");
				continue;
			}
			if (isAlreadyConnected(ip)) {
				System.out.println(ip + " is already connected!");
				//continue;
			}

			int index = getAvailableIndex();
			if(index == -1) {
				System.out.println(ip + " server full!\n");
				try {
					user.socket.close();
				} catch (IOException e) {
					System.out.println("Failed to close socket for: " + ip);
				}
			} else {
				try {
					users[index] = user;
					user.messageUser("Welcome to the server!");
					user.messageUser("Blah!");
					user.thread.start();
					System.out.println(ip + " connected!\n");
				} catch (Exception e) {
					System.out.println("Failed to create user for " + ip);
				}
			}
		}
	}
	public void addToWhiteList(String ip) {
		this.whiteList.add(ip);
	}
	public boolean isAlreadyConnected(String ip) {
		for (User user : this.users) {
			if (user != null && !user.expired && (user.getIP()).equals((ip))) {
				return true;
			}
		}
		return false;
	}
	public boolean isWhiteListed(String ip) {
		for (String whiteListedAddress : this.whiteList) {
			if ((ip).equals(whiteListedAddress)) {
				return true;
			}
		}
		return false;
	}
	public int getAvailableIndex() {
		for (int i = 0; i < users.length; i++) {
			if (users[i] == null || users[i].expired) {
				return i;
			}
		}
		return -1;
	}
}
class User implements Runnable  {
	final Socket socket;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	final Thread thread = new Thread(this);
	boolean expired = false;
	private final long timeStamp = System.currentTimeMillis();
	public User(Socket socket) {
		this.socket = socket;
		try {
			this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (Exception e) {
			System.out.println("Failed to create user for: " + this.getIP());
			this.expired = true;
		}
	}
	public void run() {
		while(!this.expired) {
			this.updateTimestamp();
			this.printUserInput();
		}
		this.disconnect();
	}
	public void disconnect() {
		try {
			this.socket.close();
			this.printWriter.close();
			this.bufferedReader.close();
			System.out.println(this.getIP() + " was dis-connected!\n");
		} catch (Exception e) {
			System.out.println(this.getIP() + " failed to dis-connect!\n");
		}
	}
	public void messageUser(String message) {
		try {
			this.printWriter.println(message);
		} catch(Exception e) {
			System.out.println("Failed to send message to user: " + this.getIP());
		}
	}
	private void printUserInput() {
		try {
			if(this.bufferedReader.ready()) {
				System.out.print(this.getIP() + " says: ");
				while (this.bufferedReader.ready()) {
					System.out.println(this.bufferedReader.readLine());
				}
				System.out.println();
			}
		} catch(Exception e) {
			this.expired = true;
			System.out.println("Failed to print input from: " + this.getIP());
		}
	}
	private void updateTimestamp() {
		if (System.currentTimeMillis() - this.timeStamp >= 1 * 60 * 1000) {
			this.expired = true;
			System.out.println(this.getIP() + " expired!");
		}
	}
	public String getIP() {
		return String.valueOf(this.socket.getInetAddress());
	}
}