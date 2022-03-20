package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsable de la réception d'un fichier.
 * Le protocol est envoyé par le client python.
 */
public class GetFileTask implements TaskManager {

    private final ClientHandler clientHandler;
    private final StorManager storManager;
    private final Hasher hasher;
    private Matcher matcher;

    /**
     * Constructeur qui contient plusieurs paramètres afin de pouvoir envoyer efficacement le protocol aux différents storProcessor (surtout
     * celui approprié dans notre cas).
     * @param clientHandler
     * @param storManager
     */
    public GetFileTask(ClientHandler clientHandler, StorManager storManager) {
        this.clientHandler = clientHandler;
        this.storManager = storManager;
        this.hasher = new Hasher();
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(GETFILE) ([a-zA-Z0-9].{5,20})$");
        matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        // Récupération du nom du fichier depuis le protocol.
        String fileName = matcher.group(2);

        // Récupération du domaine de destination.
        String destination = defineDestination(fileName);

        // Création de la tâche.
        createTaskForStorProcessors(fileName, destination);
    }

    /**
     * Défini le domaine de destination pour notre tâche.
     * @param fileName le nom du fichier
     * @return le domaine de desination s'il est trouvé, null dans le cas contraire.
     */
    private String defineDestination(String fileName) {
        var currentUser = clientHandler.getConnectedUser();
        String destination = null;
        try {
            destination = currentUser.getStorageManagerOfFile(hasher.clearTextToHash(fileName), hasher);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return destination;
    }

    /**
     * Crée notre tâche afin qu'elle soit réceptionnée par nos storProcessors.
     * @param fileName le nom du fichier
     * @param destination le domaine de destination
     */
    private void createTaskForStorProcessors(String fileName, String destination) {
        if (destination != null) {
            var newTask = new Task("GETFILE", destination);
            newTask.setFileName(fileName);

            storManager.addTask(newTask);
        }
    }
}
