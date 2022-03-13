package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.ServerInfo;

import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.fileManager.FileSender;
import secCon.PickLayaDeti.thread.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class StorProcessor implements Runnable {
    private final StorManager manager;
    ServerInfo process;
    private boolean stop;

    Socket server;

    PrintWriter out;
    BufferedReader in;

    public StorProcessor(ServerInfo process, StorManager manager) {
        this.process = process;
        this.manager = manager;
    }

    public ServerInfo getServerInfo() {
        return process;
    }


    @Override
    public void run() {
       var ipAddress = process.getIpAddress();
       var port = process.getPort();

        // Affichage la demande de connexion
        System.out.printf("[StorProcessorRunnable][run] Attempting connection to %s:%d\r\n", ipAddress, port);

        try {
            server = new Socket("127.0.0.1", port);
            stop = false;
            // Déclare une sortie pour envoyer un message qui va vérifier la connexion.
            this.out = new PrintWriter(new OutputStreamWriter(server.getOutputStream(), StandardCharsets.UTF_8), true);
            this.in = new BufferedReader(new InputStreamReader(server.getInputStream(), StandardCharsets.UTF_8));

            do {
                Task t = manager.askTask();

                if(t != null) {
                    if(t.getProtocol().equals("SENDFILE")) {
                        out.print("SENDFILE " + "jsp.png" + " " + 10);
                        System.out.println("[StorProcessor][sendMessage] sending 'SENDFILE'");
                        try {
                            FileSender fileSender = new FileSender(Program.PATH);
                            fileSender.sendFile("jsp.png", server.getOutputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    t = null;
                }
            } while(!stop);

        } catch (IOException ex) {
            System.out.println("Erreur lors de la connexion au serveur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }



    private void stop() {
        this.stop = true;
    }
}
