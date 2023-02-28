import Client.Client;

public class ClientTest {
    public static void main(String[] args) {
        Client client = new Client(4444);
        client.start();
    }
}
