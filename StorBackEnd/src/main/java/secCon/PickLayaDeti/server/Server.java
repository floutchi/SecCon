package secCon.PickLayaDeti.server;

import secCon.PickLayaDeti.AppController;
import secCon.PickLayaDeti.fileManager.FileReceiver;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int listeningPort;
    private boolean stop = false;
    private boolean isConnected = false;

    public Server(AppController controller) {
        this.listeningPort = 15201;
    }

    public void startListening() {
        try {
            try (ServerSocket server = new ServerSocket(listeningPort)) {

                Socket client = server.accept();

                ClientHandler clientHandler = new ClientHandler(client, null);
                new Thread(clientHandler).start();

                //server.close();
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

    public void stop() {
        stop = true;
    }
}
