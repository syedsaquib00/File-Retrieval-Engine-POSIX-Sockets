package server;
import common.MessageProtocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.InetSocketAddress;

public class WorkerThread implements Runnable {
    private final Socket clientSocket;
    private final ServerApp serverApp;

    public WorkerThread(Socket clientSocket, ServerApp serverApp) {
        this.clientSocket = clientSocket;
        this.serverApp = serverApp;
    }

    @Override
    public void run() {
        InetSocketAddress clientAddress = (InetSocketAddress) clientSocket.getRemoteSocketAddress();
        serverApp.addClient(clientAddress, clientSocket.getPort());
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            while (!clientSocket.isClosed()) {
                Object message = in.readObject();
                if (message instanceof MessageProtocol.IndexMessage) {
                    serverApp.getProcessingEngine().handleIndexRequest(clientSocket, (MessageProtocol.IndexMessage) message);
                } else if (message instanceof MessageProtocol.SearchMessage) {
                    serverApp.getProcessingEngine().handleSearchRequest(clientSocket, (MessageProtocol.SearchMessage) message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            serverApp.removeClient(clientAddress);
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
