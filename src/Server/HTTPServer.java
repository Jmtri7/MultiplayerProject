package Server;

import java.io.*;
import java.net.*;

import java.util.Arrays;
import java.util.List;

public class HTTPServer {
	static HTTPUser[] user = new HTTPUser[2];

	public static void main(String[] args) throws Exception {
		ServerSocket serverSocket = new ServerSocket(4444);

		List<String> whitelist = Arrays.asList(
			"/127.0.0.1"
		);

		System.out.println();
		System.out.println("===============");
		System.out.println("SERVER STARTED");
		System.out.println("===============");

		while(true) {
			// create a new socket for queued connection
			Socket socket = serverSocket.accept();
			InetAddress ip = socket.getInetAddress();
			boolean allowed = false;

			// check white-list
			for(int i = 0; i < whitelist.size(); i++) {
				if((ip + "").equals(whitelist.get(i))) allowed = true;
			}
			if(!allowed) {
				System.out.println(ip + " blocked!");
				continue;
			}

			// check if already using a socket
			for(int i = 0; i < user.length; i++) {
				if(user[i] != null && !user[i].expired) {
					if(("" + user[i].ip).equals(("" + ip))) allowed = false;
				}
			}
			if(!allowed) {
				//System.out.println(ip + " already connected!");
				continue;
			}

			// allocate thread
			for(int i = 0; i < user.length; i++) {
				if(user[i] == null || user[i].expired) {
					allowed = true;
					InputStream in = socket.getInputStream();
					OutputStream out = socket.getOutputStream();
					user[i] = new HTTPUser(ip, in, out);
					Thread thread = new Thread(user[i]);
					thread.start();
					break;
				}
			}
			if(!allowed) {
				System.out.println(ip + " server full!");
				continue;
			} else {
				System.out.println(ip + " connected!");
			}

        	//socket.close();
		}
	}
}

class HTTPUser implements Runnable  {
	InetAddress ip;
	InputStream in;
	OutputStream out;

	long timeStamp = System.currentTimeMillis();
	boolean expired = false;

	public HTTPUser(InetAddress ip, InputStream in, OutputStream out) {
		this.ip = ip;
		this.in = in;
		this.out = out;
	}

	public void run() {
		while(!expired) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String line;
				while(reader.ready()) {
					line = reader.readLine();
	     			//System.out.println(line);
	     			HandleHTTP(line);
				}

			} catch(IOException e) {
				System.out.println(e);
			}

			if(System.currentTimeMillis() - timeStamp >= 5000) {
				expired = true;
				System.out.println(ip + " expired!");
			}
		}

	}

	private boolean HandleHTTP(String text) {
		if(text == null) return false;

		String[] requestParam = text.split(" ");
		if(requestParam.length > 1 && requestParam[0].equals("GET")) {
			String[] path = requestParam[1].split("/");
			route(path);
		}
		else return false;

		return true;
	}

	private void route(String[] path) {
		if(path.length >= 1) {
			System.out.println("path: " + path[0]);
			if(path.length >= 1) {
				System.out.println("path: " + path[1]);
			}
		}
		else sendPage(out);
	}

	private void sendPage(OutputStream out) {
		try {
			File index = new File("index.html");
			BufferedReader fileReader = new BufferedReader(new FileReader(index));

        	PrintWriter printWriter = new PrintWriter(out, true);
			printWriter.println("HTTP/1.1 200 OK");
			printWriter.println("Content-Type: text/html");
			printWriter.println("Content-Length: " + index.length());
			printWriter.println("\r\n");

			try {
				String line = fileReader.readLine();
				while(line != null) {
    				printWriter.println(line);
    				line = fileReader.readLine();
				}
			} catch(IOException e) {
				System.out.println(e);
			}
			
			printWriter.close();
		} catch(FileNotFoundException e) {
			System.out.println(e);
		}
	
	}

	// REWORK
	private static void sendState(OutputStream out) {
        PrintWriter printWriter = new PrintWriter(out, true);
        printWriter.println("HTTP/1.1 200 OK");
		printWriter.println("Content-Type: text/html");
		printWriter.println("\r\n");
		//for(int i = 0; i < pixels.size(); i++) {
			//printWriter.println(pixels.get(i)[0] + "_" + pixels.get(i)[1]);
		//}
		printWriter.close();
	}
}