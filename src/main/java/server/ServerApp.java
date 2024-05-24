package server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private final ServerProcessingEngine processingEngine;
    private final ExecutorService executorService;
    private final Map<InetSocketAddress, Integer> connectedClients;
    private DispatcherThread dispatcherThread;

    public ServerApp(int port, int numThreads) {
        this.processingEngine = new ServerProcessingEngine(numThreads);
        this.executorService = Executors.newFixedThreadPool(numThreads);
        this.connectedClients = new HashMap<>();
        this.dispatcherThread = new DispatcherThread(port, this);
    }

    public void start() {
        new Thread(dispatcherThread).start();
    }

    public void shutdown() {
        dispatcherThread.shutdown();
        executorService.shutdown();
        System.out.println("Server shutdown complete.");
    }

    public void addClient(InetSocketAddress address, int port) {
        connectedClients.put(address, port);
    }

    public void removeClient(InetSocketAddress address) {
        connectedClients.remove(address);
    }

    public Map<InetSocketAddress, Integer> listClients() {
        return connectedClients;
    }

    public ServerProcessingEngine getProcessingEngine() {
        return processingEngine;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
    
    public static void main(String[] args) {
        // Parse command-line arguments
        int port = 8080; // Default port
        int numThreads = 4; // Default number of threads
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        if (args.length >= 2) {
            numThreads = Integer.parseInt(args[1]);
        }
        
        // Instantiate and start the server
        ServerApp serverApp = new ServerApp(port, numThreads);
        serverApp.start();
        System.out.println("Server started on port " + port);
    }
}
