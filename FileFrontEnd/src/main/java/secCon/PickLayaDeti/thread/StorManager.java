package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.domains.ServerInfo;
import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.domains.tasks.sbe.EraseResultTask;
import secCon.PickLayaDeti.domains.tasks.sbe.RetrieveResultTask;
import secCon.PickLayaDeti.domains.tasks.sbe.SendResultTask;
import secCon.PickLayaDeti.security.Hasher;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Notre storManager qui va délégué les responsabilités aux bons SBE.
 */
public class StorManager {
    List<StorProcessor> servers;

    private ClientHandler clientHandler;

    private StorProcessor currentStorProcessor;

    private final List<Task> tasks;

    /**
     * Défini notre liste de tâche vide et de serveur vides.
     */
    public StorManager() {
        this.tasks = new ArrayList<>();
        this.servers = new ArrayList<>();

    }

    /**
     * Crée notre storProcessor pour les informations passées en paramètre.
     * @param infos les infos du serveur sur lequel se connecté.
     */
    public void createProcessor(ServerInfo infos) {

        for (StorProcessor cprocessor : servers) {
            if (!cprocessor.getServerInfo().getDomain().equals(infos.getDomain())) {
                var processor = new StorProcessor(infos, this, new Hasher());
                servers.add(processor);
                new Thread(processor).start();
            }
        }

        if(servers.isEmpty()) {
            var processor = new StorProcessor(infos, this, new Hasher());
            servers.add(processor);
            new Thread(processor).start();
        }
    }

    /**
     * Défini notre clienthandler
     * @param clientHandler celui à définir.
     */
    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * Ajoute une tâche à notre la liste.
     * @param t la tâche à ajouter.
     */
    public void addTask(Task t) {
        this.tasks.add(t);

        for (StorProcessor sp : servers) {
            if(!sp.isBusy()) {
                sp.setTask(t);
                currentStorProcessor = sp;
            }
        }

    }

    /**
     * Vérifie notre message avec nos différents tâches.
     * @param message  le message à vérifier.
     */
    public void resultTask(String message) {
        var tasks = getTasks();
        for (var currentTask:
             tasks) {
            if(currentTask.check(message)) {
                currentTask.execute(message);
            }
        }
    }

    /**
     * Récupère nos listes de tâches.
     * @return la liste de tâche.
     */
    private List<TaskManager> getTasks() {
        List<TaskManager> tasks = new ArrayList<>();
        tasks.add(new SendResultTask(clientHandler, currentStorProcessor.getServerInfo().getDomain()));
        tasks.add(new EraseResultTask(clientHandler));
        tasks.add(new RetrieveResultTask(clientHandler, this));
        return tasks;
    }

    /**
     * Récupère notre storProcessor pour un fichier donné.
     * @param fileName le nom du fichier.
     * @return le différent StorProcessor.
     */
    public StorProcessor getStorProcessor(String fileName) {
        String domain = clientHandler.getStorProcessorOfUser(fileName);
        for (StorProcessor s : servers) {
            var currentName = s.getServerInfo().getDomain();
            new Hasher().clearTextToHash(currentName);
            if (s.getServerInfo().getDomain().equals(domain)) {
                return s;
            }
        }
        return null;
    }

}
