import Server.Server;

public class ServerTest {
    public static void main(String[] args) {
        Server server = new Server(4444);
        server.addToWhiteList("/127.0.0.1");
        server.start();
    }
}
