package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.ServerInfo;

import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.fileManager.FileSender;
import secCon.PickLayaDeti.security.Hasher;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

/**
 * Déclare le storProcessor et gère les différents traitements entre le FFE et SBE
 */
public class StorProcessor implements Runnable {
    private final StorManager manager;
    ServerInfo process;
    private boolean stop;
    private boolean isBusy = false;
    Socket server;
    BufferedWriter out;
    BufferedReader in;
    Task t;
    private final Hasher hasher;

    /**
     * Initialise nos différents attributs.
     * @param process les informations du serveur.
     * @param manager le storManager qui va gérer les différents processors.
     * @param hasher la classe responsable du Hashage.
     */
    public StorProcessor(ServerInfo process, StorManager manager, Hasher hasher) {
        this.process = process;
        this.manager = manager;
        this.hasher = hasher;
    }

    /**
     * Retourne les informations du serveur.
     * @return les infos.
     */
    public ServerInfo getServerInfo() {
        return process;
    }


    @Override
    public void run() {
       var ipAddress = process.getDomain();
       var port = process.getPort();

        // Affichage la demande de connexion
        System.out.printf("[StorProcessorRunnable][run] Attempting connection to %s:%d\r\n", ipAddress, port);

        try {
            server = new Socket("127.0.0.1", port);
            stop = false;
            // Déclare une sortie pour envoyer un message qui va vérifier la connexion.
            this.out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream(), StandardCharsets.UTF_8));
            this.in = new BufferedReader(new InputStreamReader(server.getInputStream(), StandardCharsets.UTF_8));

            do {
                Thread.sleep(0);
                if(t != null) {
                    isBusy = true;

                    // nom haché de notre fichier.
                    var hashedName = hasher.clearTextToHash(t.getFileName());

                    checkProtocol(hashedName);
                    this.t = null;
                }

            } while(!stop);

        } catch (IOException ex) {
            System.out.println("Erreur lors de la connexion au serveur : " + ex.getMessage());
            ex.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affecte le bon comportement pour le bon protocol.
     * @param hashedName le nom du fichier haché.
     * @throws IOException un problème lors d'un protocol.
     */
    private void checkProtocol(String hashedName) throws IOException {
        switch (t.getProtocol()) {
            case "SENDFILE":
                sendFile(hashedName);
                break;
            case "REMOVEFILE":
                if (process.getDomain().equals(t.getDestination()) || process.getDomain() == null) {
                    deleteFile();
                }
                break;
            case "GETFILE":
                if (process.getDomain().equals(t.getDestination()) || process.getDomain() == null) {
                    retrieveFile(hashedName);
                }
                break;
        }
    }

    /**
     * Récupère le fichier dont le nom est passé en paramètre.
     * @param hashedName le nom haché du fichier.
     * @throws IOException au cas où la réception ne se fasse pas correctement.
     */
    private void retrieveFile(String hashedName) throws IOException {
        out.write("RETRIEVEFILE " + hashedName + "\n");

        out.flush();
        System.out.println("[StorProcessor][run] sending 'RETRIEVEFILE'");

        String line = in.readLine();
        System.out.println("[StorProcessor][run] received : " + line);
        manager.resultTask(line);
    }

    /**
     * Supprime notre fichier de notre SBE.
     * @throws IOException au cas où la supression ne se fasse pas correctement.
     */
    private void deleteFile() throws IOException {
        out.write("ERASEFILE " + t.getFileName() + "\n");
        out.flush();
        System.out.println("[StorProcessor][run] sending 'ERASEFILE'");

        String line = in.readLine();
        System.out.println("[StorProcessor][run] received : " + line);
        manager.resultTask(line);
    }

    /**
     * Envoie un fichier à notre SBE connecté.
     * @param hashedName le nom du fichier haché.
     * @throws IOException au cas où l'envoie ne se fasse pas correctement.
     */
    private void sendFile(String hashedName) throws IOException {
        out.write("SENDFILE " + hashedName + " " + t.getFileSize() + "\n");
        out.flush();
        System.out.println("[StorProcessor][sendMessage] sending 'SENDFILE'");
        try {
            FileSender fileSender = new FileSender(Program.PATH);
            fileSender.sendFile(t.getFileName(), server.getOutputStream());

            String line = in.readLine();
            System.out.println("[StorProcessor][run] received : " + line);
            manager.resultTask(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si notre processor est occupé (ou non).
     * @return si notre processor est occupé ou non.
     */
    public boolean isBusy() {
        return isBusy;
    }

    /**
     * Défini notre tâche actuelle.
     * @param t la tâche à définir.
     */
    public void setTask(Task t) {
        this.t = t;
    }


    private void stop() {
        this.stop = true;
    }

    /**
     * Récupère l'input de notre serveur.
     * @return l'input du serveur.
     * @throws IOException au cas où la réception ne se fasse pas correctement.
     */
    public InputStream getInputStream() throws IOException {
        return server.getInputStream();
    }
}
