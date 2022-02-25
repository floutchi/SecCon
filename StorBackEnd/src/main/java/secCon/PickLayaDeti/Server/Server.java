package secCon.PickLayaDeti.Server;

import secCon.PickLayaDeti.AppController;
import secCon.PickLayaDeti.Thread.ClientRunnable;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class Server {
    private final int listeningPort;
    private boolean stop = false;
    private boolean isConnected = false;

    public Server(AppController controller) {
        this.listeningPort = 15201;
    }

    public void startListening() {
        try {
            ServerSocket server = new ServerSocket(listeningPort);

            while(!stop) {
                Socket client = server.accept();


                BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream(), Charset.forName("UTF-8")));
                PrintWriter toClient = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), Charset.forName("UTF-8")), true);
                String ligne = readLine(fromClient);
                System.out.println("[Server][listen] new connection, starting thread");
                toClient.flush();
                client.close();
                isConnected = false;
            }

            server.close();
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

