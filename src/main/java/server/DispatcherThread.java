package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DispatcherThread implements Runnable {
    private final int port;
    private final ServerApp serverApp;
    private ServerSocket serverSocket;
    private boolean running;

    public DispatcherThread(int port, ServerApp serverApp) {
        this.port = port;
        this.serverApp = serverApp;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                WorkerThread worker = new WorkerThread(clientSocket, serverApp);
                serverApp.getExecutorService().submit(worker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

