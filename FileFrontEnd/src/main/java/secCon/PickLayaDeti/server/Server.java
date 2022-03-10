package secCon.PickLayaDeti.server;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{

    private final int listeningPort;
    private boolean stop = false;
    private boolean isConnected = false;

    public Server() {
        this.listeningPort = Program.UNICAST_PORT;
    }

    public void startListening() {
        try {
            try (ServerSocket server = new ServerSocket(listeningPort)) {

                while (!stop) {
                    Socket client = server.accept();

                    var handler = new ClientHandler(client);
                    // Démarre le thread.
                    (new Thread(handler)).start();

                    isConnected = false;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line != null && line.length() > 2 && line.startsWith("\uFEFF"))
            return line.substring("\uFEFF".length());
        return line;
    }

    @Override
    public void run() {
        startListening();
    }
}
