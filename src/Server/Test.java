package Server;

import Client.Client;

public class Test {
    public static void main(String[] args) {

        int testPort = 4444;

        Server server = new Server(testPort);
        server.addToWhiteList("/127.0.0.1");
        server.start();

        Client client = new Client(testPort);
        client.start();

    }
}
