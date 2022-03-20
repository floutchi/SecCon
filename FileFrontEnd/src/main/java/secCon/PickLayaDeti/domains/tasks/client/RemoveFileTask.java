package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsable du protocol de suppression du fichier.
 * Ce protocol est envoyé par le client python.
 */
public class RemoveFileTask implements TaskManager {

    Matcher matcher;
    ClientHandler clientHandler;
    StorManager storManager;

    public RemoveFileTask(ClientHandler clientHandler, StorManager storManager) {
        this.clientHandler = clientHandler;
        this.storManager = storManager;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(REMOVEFILE) ([a-zA-Z0-9].{5,20})$");
        matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        // Récupère le nom du fichier à supprimer.
        String fileName = matcher.group(2);

        // Récupère le nom haché du fichier.
        String hashedFileName = getHashedFileName(fileName);

        User currentUser = clientHandler.getConnectedUser();
        var storage = currentUser.getStorageManagerOfFile(hashedFileName, new Hasher());

        addTaskToStorProcessor(fileName, hashedFileName, storage);

    }

    /**
     * Vérifie si le domaine de la tâche n'est pas nulle, si oui, il ajoute la tâche pour nos différents storProcessors.
     * @param fileName le nom du fichier.
     * @param hashedFileName le nom du fichier haché.
     * @param storage le nom du storage.
     */
    private void addTaskToStorProcessor(String fileName, String hashedFileName, String storage) {
        if (storage != null) {
            var newTask = new Task("REMOVEFILE", storage);

            newTask.setFileName(hashedFileName);

            clientHandler.setCurrentFileName(fileName);
            storManager.addTask(newTask);
        }
    }

    /**
     * Récupère le nom haché de notre fichié.
     * @param fileName le nom du fichier en clair.
     * @return le nom haché.
     */
    private String getHashedFileName(String fileName) {
        return new Hasher().clearTextToHash(fileName);
    }
}
