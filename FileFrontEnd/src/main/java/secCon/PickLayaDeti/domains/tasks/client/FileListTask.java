package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.StoredFiles;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsable de la réception de la liste des différents fichiers de notre utilisateur qui vient de se connecter.
 */
public class FileListTask implements TaskManager {

    private final ClientHandler clientHandler;

    public FileListTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(FILELIST)$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        // Récupère l'utilisateur connecté
        var currentUser = clientHandler.getConnectedUser();

        // Renvoie sa liste de fichiers
        List<StoredFiles> storedFiles = currentUser.getFilesList();

        // Récupère la liste des fichiers sous un format correct.
        StringBuilder allFiles = getFileListIntoString(storedFiles);

        // Envoie le message au client python.
        clientHandler.sendMessage(allFiles.toString());
    }

    /**
     * Récupère la liste des fichiers et la mets sous une forme acceptée par le client python.
     * @param storedFiles les différents fichiers de notre utilisateur.
     * @return la liste des fichiers sous forme de String
     */
    private StringBuilder getFileListIntoString(List<StoredFiles> storedFiles) {
        StringBuilder sb = new StringBuilder();
        sb.append("FILES ");

        for (StoredFiles f : storedFiles) {
            sb.append(f.getName()).append("!").append(f.getSize()).append(" ");
        }
        return sb;
    }
}
