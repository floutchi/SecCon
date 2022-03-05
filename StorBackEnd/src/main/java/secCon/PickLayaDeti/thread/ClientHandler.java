package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.AppController;
import secCon.PickLayaDeti.tasks.interfaces.TaskManager;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {

    private Socket client;
    private AppController controller;
    private boolean stop = false;
    private boolean connected = false;
    private PrintWriter out;
    private BufferedReader in;
    private int state = 0;

    public ClientHandler(Socket client, AppController controller) {
        try {
            this.client = client;
            this.controller = controller;
            this.connected = true;

            this.in = new BufferedReader(new InputStreamReader(client.getInputStream(),
                    StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),
                    StandardCharsets.UTF_8), true);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while(connected && !stop) {
                String line = in.readLine();
                if(line != null) {

                } else {
                    stop = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            try { client.close(); } catch (IOException ex) {}
        }

    }

}

