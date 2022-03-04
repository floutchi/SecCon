package secCon.PickLayaDeti.server;

import secCon.PickLayaDeti.AppController;
import secCon.PickLayaDeti.fileManager.FileReceiver;

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

                while (!stop) {
                    Socket client = server.accept();

                    //Recoit le fichier
                    FileReceiver fileReceiver = new FileReceiver("C:\\TEMP\\SBE");
                    fileReceiver.receiveFile(client.getInputStream(), "aa.png", 154727);
                    client.close();
                    isConnected = false;
                }

                server.close();
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
