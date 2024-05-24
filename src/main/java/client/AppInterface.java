package client;
import java.io.IOException;
import java.util.Scanner;

import common.MessageProtocol.SearchMessage;

public class AppInterface {
    private ClientProcessingEngine processingEngine;
    private Scanner scanner;

    public AppInterface(ClientProcessingEngine processingEngine) {
        this.processingEngine = processingEngine;
        this.scanner = new Scanner(System.in);
    }

    public void start() throws ClassNotFoundException, IOException {
        System.out.println("File Retrieval Engine Client");
        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine();
            if (command.equalsIgnoreCase("quit")) {
                processingEngine.quit();
                break;
            } else if (command.startsWith("connect ")) {
                String[] parts = command.split(" ");
                if (parts.length == 3) {
                    String serverIp = parts[1];
                    int port = Integer.parseInt(parts[2]);
                    processingEngine.connect(serverIp, port);
                } else {
                    System.out.println("Invalid command format. Use: connect <server IP> <port>");
                }
            } else if (command.startsWith("index ")) {
                String folderPath = command.substring(6);
                processingEngine.index(folderPath);
            } else if (command.startsWith("search ")) {
                String query = command.substring(7);
            	//String query = command.substring(7);
               // SearchMessage searchMessage = new SearchMessage(query);
                // Pass the SearchMessage instance to the search method
                processingEngine.search(query);
            } else {
                System.out.println("Unknown command.");
            }
        }
    }
}

