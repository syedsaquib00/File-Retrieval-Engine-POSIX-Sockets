package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Map;

public class AppInterface {
    private final ServerApp serverApp;

    public AppInterface(ServerApp serverApp) {
        this.serverApp = serverApp;
    }

    public void start() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String command;
            while ((command = reader.readLine()) != null) {
                if (command.equalsIgnoreCase("quit")) {
                    serverApp.shutdown();
                    break;
                } else if (command.equalsIgnoreCase("list")) {
                    Map<InetSocketAddress, Integer> clients = serverApp.listClients();
                    clients.forEach((address, port) -> System.out.println("Client: " + address + " Port: " + port));
                } else {
                    System.out.println("Unknown command");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
