package secCon.PickLayaDeti.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {


    public Client(String destination, int port) {

        // Affichage la demande de connexion
        System.out.println("Connexion en cours à " + destination + " sur le port : " + port);
        try (var server = new Socket(destination, port)) {
            // Déclare un message d'écriture pour vérifier la connexion.
            var toServer = new PrintWriter(new OutputStreamWriter(server.getOutputStream(), StandardCharsets.UTF_8), true);
            toServer.print("Connecté au serveur \r\n");
            toServer.flush();


        } catch (IOException ex) {
            System.out.println("Erreur lors de la connexion au serveur : " + ex.getMessage());
        }
    }
}
