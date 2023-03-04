import Client.Client;

import java.util.Objects;
import java.util.Scanner;

public class ClientTest1 {
    public static void main(String[] args) {
        Client client = new Client(4444);
        client.start();
        Scanner scanner = new Scanner(System.in);
        boolean success = true;
        String message;
        while(success) {

            message = client.getMessage();
            while(Objects.nonNull(message)) {
                System.out.println(message);
                message = client.getMessage();
            }

            System.out.print("Send a message: ");
            message = scanner.nextLine();
            if(message.equals("quit")) {
                break;
            }
            success = client.sendMessage(message);

        }
        System.out.println("Stopping client!");
        client.stop();
    }
}
