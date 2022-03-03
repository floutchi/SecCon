package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.AppController;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientRunnable implements Runnable {

    private Socket client;
    private AppController controller;
    private boolean stop = false;
    private boolean connected = false;
    private PrintWriter out;
    private BufferedReader in;

    public ClientRunnable(Socket client, AppController controller) {
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

    public void sendMessage(String message) {
        if(connected) {
            out.print(String.format("%s\r\n", message));
            out.flush();
        }
    }

    @Override
    public void run() {
        // TODO: 1. Lire le message du client
        // TODO: 2. Propager le message à tous les autres clients
        // TODO: RECOMMENCER

        try {
            while(connected && !stop) {
                String line = in.readLine();
                if(line != null) {

                } else {
                    stop = true;
                }

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }



    }
}

