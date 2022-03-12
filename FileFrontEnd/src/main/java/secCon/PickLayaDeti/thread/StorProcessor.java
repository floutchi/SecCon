package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.domains.ServerInfo;

import secCon.PickLayaDeti.thread.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class StorProcessor implements Runnable {
    private final StorManager manager;
    ServerInfo process;
    private boolean stop;

    public StorProcessor(ServerInfo process, StorManager manager) {
        this.process = process;
        this.manager = manager;
    }

    @Override
    public void run() {
       var ipAddress = process.getIpAddress();
       var port = process.getPort();

        // Affichage la demande de connexion
        System.out.printf("[StorProcessorRunnable][run] Attempting connection to %s:%d\r\n", ipAddress, port);

        try (var server = new Socket(ipAddress, port)) {
            stop = false;
            // Déclare une sortie pour envoyer un message qui va vérifier la connexion.
            var toServer = new PrintWriter(new OutputStreamWriter(server.getOutputStream(), StandardCharsets.UTF_8), true);

            // Envoie le message pour valider la connexion.
            toServer.flush();

            do {
                manager.askTask();
            } while(!stop);
            // Envoie le fichier
            //FileSender fileSender = new FileSender("C:\\TEMP\\FFE");
            //fileSender.sendFile("aa.png", server.getOutputStream());

        } catch (IOException ex) {
            System.out.println("Erreur lors de la connexion au serveur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void stop() {
        this.stop = true;
    }
}
