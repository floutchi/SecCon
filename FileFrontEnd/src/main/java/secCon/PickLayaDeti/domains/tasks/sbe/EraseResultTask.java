package secCon.PickLayaDeti.domains.tasks.sbe;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsable du protocol de suppression du fichier.
 * Ce protocol est envoyé par le SBE.
 */
public class EraseResultTask implements TaskManager {

    ClientHandler clientHandler;
    Matcher matcher;

    public EraseResultTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(ERASE_OK|ERASE_ERROR)$");
        matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

        // Vérification du message reçu par le SBE.
        if(matcher.group(1).equals("ERASE_OK")) {
            // Envoie du message au client.
            clientHandler.sendMessage("REMOVEFILE_OK");

            // Récupération du client connecté et suppression dans l'objet "User".
            var connectedUser = clientHandler.getConnectedUser();
            connectedUser.removeFile(clientHandler.getCurrentFileName());

            // Modification de la liste et écriture des utilisateurs.
            Program.jsonConfig.updateUsers(clientHandler.getUsers());
            Program.jsonConfig.writeUsers();

        } else {
            // Envoie du message d'erreur
            clientHandler.sendMessage("REMOVEFILE_ERROR");
        }
    }
}
