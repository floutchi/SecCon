package secCon.PickLayaDeti.server;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

// changer ServerSocket en SSL serverSocket
public class Server implements Runnable {

    private final int listeningPort;
    private final StorManager storManager;
    private boolean stop = false;
    private boolean isConnected = false;

    private ClientHandler handler;

    public Server(StorManager storManager) {
        this.storManager = storManager;
        this.listeningPort = Program.UNICAST_PORT;
    }

    @Override
    public void run() {
        try {
            SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(listeningPort);

            //ServerSocket serverSocket = new ServerSocket(Program.UNICAST_PORT);

            while (!stop) {
                Socket client = serverSocket.accept();

                //var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                //System.out.println(readLine(in));

                handler = new ClientHandler(client, storManager);
                storManager.setClientHandler(handler);
                // Démarre le thread.
                (new Thread(handler)).start();

                isConnected = false;
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
