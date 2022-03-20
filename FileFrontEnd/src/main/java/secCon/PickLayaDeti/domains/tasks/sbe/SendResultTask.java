package secCon.PickLayaDeti.domains.tasks.sbe;

import secCon.PickLayaDeti.domains.StoredFiles;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsable du protocol l'de la réception d'un envoi de fichier.
 * Ce protocol est envoyé par le SBE.
 */
public class SendResultTask implements TaskManager {

    ClientHandler clientHandler;
    String savedSBE;

    public SendResultTask(ClientHandler clientHandler, String domain) {
        this.clientHandler = clientHandler;
        this.savedSBE = domain;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SEND_ERROR|SEND_OK)$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        // Envoie le message.
        clientHandler.sendMessage("SAVEFILE_OK");

        // Récupère l'utilisateur connecté.
        var user = clientHandler.getConnectedUser();

        // Ajoute le fichier à notre utilisateur.
        var currentStoredFile = new StoredFiles(clientHandler.getCurrentFileName(), clientHandler.getCurrentIv(), clientHandler.getCurrentFileSize(), savedSBE);
        user.addFile(currentStoredFile);
    }
}
