package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.domains.ServerInfo;

import secCon.PickLayaDeti.thread.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class StorProcessor implements Runnable {

    ServerInfo process;
    StorManager storManager;
    String ipAddress;
    int port;
    String domain;

    public StorProcessor(ServerInfo process, StorManager storManager) {
        this.process = process;
        this.storManager = storManager;
        ipAddress = process.getIpAddress();
        port = process.getPort();
        domain = process.getDomain();
    }

    @Override
    public void run() {
        // Affichage la demande de connexion
        System.out.printf("[StorProcessorRunnable][run] Attempting connection to %s:%d\r\n", ipAddress, port);

        try (var server = new Socket(ipAddress, port)) {
            // Déclare une sortie pour envoyer un message qui va vérifier la connexion.
            var toServer = new PrintWriter(new OutputStreamWriter(server.getOutputStream(), StandardCharsets.UTF_8), true);

            // Envoie le message pour valider la connexion.
            toServer.flush();
            if(!storManager.isSBEAlreadyIn(process)) {
                storManager.addStorage(process);
            }

            // Envoie le fichier
            //FileSender fileSender = new FileSender("C:\\TEMP\\FFE");
            //fileSender.sendFile("aa.png", server.getOutputStream());

        } catch (IOException ex) {
            System.out.println("Erreur lors de la connexion au serveur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
