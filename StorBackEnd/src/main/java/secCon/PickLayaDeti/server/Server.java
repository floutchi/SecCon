package secCon.PickLayaDeti.server;

import secCon.PickLayaDeti.thread.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int listeningPort;
    private boolean stop = false;
    private boolean isConnected = false;

    public Server() {
        this.listeningPort = 15201;
    }

    /**
     * Permet d'attendre la connexion du FFE au SBE
     */
    public void startListening() {
        try {
            try (ServerSocket server = new ServerSocket(listeningPort)) {

                Socket client = server.accept();

                ClientHandler clientHandler = new ClientHandler(client);
                new Thread(clientHandler).start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        stop = true;
    }
}
