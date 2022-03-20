package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveFileTask implements TaskManager {
    private final ClientHandler clientHandler;
    private final StorManager storManager;
    private Matcher matcher;

    public SaveFileTask(ClientHandler clientHandler, StorManager storManager) {
        this.clientHandler = clientHandler;
        this.storManager = storManager;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SAVEFILE) ([a-zA-Z0-9].{5,20}) ([0-9]{1,10})$");
        this.matcher = pattern.matcher(message);

        return matcher.matches();
    }

    @Override
    public void execute(String message) {

        try {
            // Récupération et hachage
            String filename = matcher.group(2);

            // Récupération de la taille
            int size = Integer.parseInt(matcher.group(3));

            // Création de la tâche
            Task newTask = createTask(filename, size);

            // Réception du fichier.
            receiveFile(filename, size, newTask);

        } catch (IOException ex) {
            System.out.println("Erreur dans la réception du fichier " + ex.getMessage());
            clientHandler.sendMessage("SEND_ERROR");
        }
    }

    /**
     * Création de la tâche en direction des storProcessors.
     * @param filename nom du fichier pour la tâche.
     * @param size taille du fichier.
     * @return la tâche crée.
     */
    private Task createTask(String filename, int size) {
        var newTask = new Task("SENDFILE", null);
        newTask.setFileName(filename);
        newTask.setFileSize(size);
        return newTask;
    }

    /**
     * Recevoir le fichier et ajout la tâche s'il a bien été reçu. SINON, renvoie un message d'erreur.
     * @param filename le nom du fichier
     * @param size taille du fichier
     * @param newTask notre tâche à ajouter.
     * @throws IOException exception retournée par la réception du fichier.
     */
    private void receiveFile(String filename, int size, Task newTask) throws IOException {
        if (clientHandler.receiveFile(filename, size)) {
            storManager.addTask(newTask);
        } else {
            clientHandler.sendMessage("SEND_ERROR");
        }
    }
}
