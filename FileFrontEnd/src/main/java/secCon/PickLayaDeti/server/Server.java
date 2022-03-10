package secCon.PickLayaDeti.server;

import secCon.PickLayaDeti.thread.ClientHandler;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

// changer ServerSocket en SSL serverSocket
public class Server {

    private final int listeningPort;
    private boolean stop = false;
    private boolean isConnected = false;

    public Server() {
        this.listeningPort = 15201;
    }

    public void startListening() {
        try {
            SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(listeningPort);

                while (!stop) {
                    Socket client = serverSocket.accept();

                    //var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    //System.out.println(readLine(in));

                    var handler = new ClientHandler();
                    // Démarre le thread.
                    (new Thread(handler)).start();

                    client.close();
                    isConnected = false;
                }
                serverSocket.close();
            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line != null && line.length() > 2 && line.startsWith("\uFEFF"))
            return line.substring("\uFEFF".length());
        return line;
    }
}
