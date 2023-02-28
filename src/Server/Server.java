package Server;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
	public static void main(String[] args) {
		Server server = new Server(4444);
		server.addToWhiteList("/127.0.0.1");
		server.start();
	}
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
				continue;
			}

			int index = getAvailableIndex();
			if(index == -1) {
				System.out.println(ip + " server full!");
				try {
					user.socket.close();
				} catch (IOException e) {
					System.out.println("Failed to close socket for: " + ip);
				}
			} else {
				try {
					users[index] = user;
					user.thread.start();
					System.out.println(ip + " connected!\n");
					break;
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
	private BufferedReader reader;
	final Thread thread = new Thread(this);
	boolean expired = false;
	private final long timeStamp = System.currentTimeMillis();
	public User(Socket socket) {
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception e) {
			System.out.println("Failed to create user for: " + this.getIP());
			this.expired = true;
		}
	}
	public void run() {
		while(!this.expired) {
			this.updateTimestamp();
			this.printInput();
		}
		try {
			this.socket.close();
		} catch (Exception e) {
			System.out.println("Failed to expire and close socket for " + this.getIP());
		}
	}
	private void printInput() {
		try {
			if(this.reader.ready()) {
				System.out.print(this.getIP() + " says: ");
				while (this.reader.ready()) {
					System.out.println(this.reader.readLine());
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