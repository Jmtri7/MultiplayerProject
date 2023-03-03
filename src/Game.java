import Client.Client;

import java.util.Scanner;

public class Game implements Runnable {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
    static final Scanner scanner = new Scanner(System.in);
    private Client client;
    private final Thread thread = new Thread(this);
    private Game() {
    }
    public void start() {
        this.client = new Client(4444);
        this.thread.start();
    }
    public void run() {


        while(true) {

            String serverMessage = "";
            while (serverMessage != null) {
                if(!serverMessage.equals("")) System.out.println(serverMessage);
                serverMessage = this.getServerMessage();
            }

            System.out.print("Send a message: ");
            String message = Game.scanner.nextLine();
            if(message.equals("quit")) {
                break;
            }
        }
        this.client.stop();
    }
}
