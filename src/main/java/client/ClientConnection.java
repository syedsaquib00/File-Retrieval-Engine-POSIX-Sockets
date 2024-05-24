package client;

import common.MessageProtocol;
import common.SearchResult;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientConnection {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ClientConnection(String serverIp, int port) throws IOException {
        socket = new Socket(serverIp, port);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public void sendIndexMessage(MessageProtocol.IndexMessage indexMessage) throws IOException {
        objectOutputStream.writeObject(indexMessage);
        objectOutputStream.flush();
    }

    public void sendSearchMessage(MessageProtocol.SearchMessage searchMessage) throws IOException {
        objectOutputStream.writeObject(searchMessage);
        objectOutputStream.flush();
    }

    public List<SearchResult> receiveSearchResults() throws IOException, ClassNotFoundException {
        Object response = objectInputStream.readObject();
        if (response instanceof List) {
            return (List<SearchResult>) response;
        }
        return new ArrayList<>();
    }

    public void close() {
        try {
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close resources: " + e.getMessage());
        }
    }

	
}
