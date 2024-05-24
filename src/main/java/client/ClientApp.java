package client;

import java.io.IOException;

public class ClientApp {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        ClientProcessingEngine processingEngine = new ClientProcessingEngine();
        AppInterface appInterface = new AppInterface(processingEngine);
        appInterface.start();
    }
}
