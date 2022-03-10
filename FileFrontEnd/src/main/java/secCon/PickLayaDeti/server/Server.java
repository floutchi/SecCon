package secCon.PickLayaDeti.server;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.thread.ClientHandler;

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
    private boolean stop = false;
    private boolean isConnected = false;

    public Server() {
        this.listeningPort = Program.UNICAST_PORT;
    }

    private String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line != null && line.length() > 2 && line.startsWith("\uFEFF"))
            return line.substring("\uFEFF".length());
        return line;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            SSLServerSocketFactory factory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(listeningPort);

            while (!stop) {
                Socket client = serverSocket.accept();

                //var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                //System.out.println(readLine(in));

                var handler = new ClientHandler(client);
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
