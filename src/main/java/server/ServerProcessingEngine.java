package server;


import common.IndexStore;
import common.MessageProtocol;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class ServerProcessingEngine {
    private final IndexStore indexStore;
    private final int numThreads;

    public ServerProcessingEngine(int numThreads) {
        this.indexStore = new IndexStore();
        this.numThreads = numThreads;
    }

    public void handleIndexRequest(Socket socket, MessageProtocol.IndexMessage indexMessage) {
        String term = indexMessage.getWord();
        File file = new File(indexMessage.getDocumentPath());
        int frequency = 1;  // Assuming frequency is always 1 per index request message
        indexStore.update(term, file, frequency);
    }

    public void handleSearchRequest(Socket socket, MessageProtocol.SearchMessage searchMessage) {
        String term = searchMessage.getTerm();
        Map<File, Integer> searchResults = indexStore.lookup(term);

        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(searchResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
