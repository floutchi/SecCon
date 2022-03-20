package secCon.PickLayaDeti.thread;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.StoredFiles;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.Users;
import secCon.PickLayaDeti.domains.tasks.client.*;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.fileManager.FileReceiverEncrypt;
import secCon.PickLayaDeti.fileManager.FileSenderDecrypt;
import secCon.PickLayaDeti.security.Hasher;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Défini notre ClientHandler. Cette classe implémente runnable puisque c'est un thread. Elle va gérer la totalité du traitement entre
 * le client et le FFE
 */
public class ClientHandler implements Runnable {
    private final Users users;
    private final StorManager storManager;
    private Socket client;
    private boolean stop = false;
    private boolean connected = false;
    private PrintWriter out;
    private BufferedReader in;
    private User connectedUser;

    private String currentFileName;
    private int currentFileSize;

    private String currentIv;

    /**
     * déclare le constructeur du ClientHandler.
     * @param client le socket du client.
     * @param storManager notre storManager qui va contenir nos différents storProcessor connectés.
     */
    public ClientHandler(Socket client, StorManager storManager) {
        this.storManager = storManager;
        this.users = new Users();
        try {
            this.client = client;
            this.connected = true;

            this.in = new BufferedReader(new InputStreamReader(client.getInputStream(),
                    StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(),
                    StandardCharsets.UTF_8), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        var tasks = createTask();
        try {
            while (connected && !stop) {
                String line = in.readLine();
                if (line != null) {
                    if (line.length() < 100) {
                        //System.out.println("[ClientHandler][run] received: " + line);
                    }
                    executeTask(line, tasks);
                }
            }
        } catch (IOException e) {
            System.out.println("[ClientHandler][run] Client déconnecté");
            try {
                client.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Program.jsonConfig.setUserList(users.getUserList());
        Program.jsonConfig.writeUsers();

    }

    /**
     * Execute la bonne tâche en vérifiant que la regex soit bien comprise et vérifiable.
     * @param message notre protocole envoyé par le client.
     * @param tasks la liste des différentes tâches.
     */
    private void executeTask(String message, List<TaskManager> tasks) {
        if (connectedUser == null) if (setUpConnectedUser(message)) return;
        for (var currentTask : tasks) {
            if (currentTask.check(message)) currentTask.execute(message);
        }
    }

    /**
     * Défini l'utilisateur connecté. Cette méthode va vérifier dans nos différentes tâches propres à la réception de fichier de connexion
     * @param message le protocole envoyé.
     * @return si la connexion s'est fait correctement (ou non).
     */
    private boolean setUpConnectedUser(String message) {
        var initialTasks = connexionTasks();
        for (var currentTask : initialTasks) {
            if (currentTask.check(message)) {
                currentTask.execute(message);
            }
        }

        return isConnected();
    }

    /**
     * Défini si notre utilisateur s'est bien connecté et envoie un message d'erreur dans le cas contraire.
     * @return vrai si le client est connecté, faux dans le cas contraire.
     */
    private boolean isConnected() {
        if (connectedUser == null) {
            sendMessage("SIGN_ERROR");
            return true;
        } else {
            sendMessage("SIGN_OK");
        }
        return false;
    }

    /**
     * Envoie un message depuis le clientHandler.
     * @param message le message à envoyer.
     */
    public void sendMessage(String message) {
        if (connected) {
            System.out.println("[ClientHandler][sendMessage] " + message);
            out.print(String.format("%s\r\n", message));
            out.flush();
        }
    }

    /**
     * Récupère la liste de tous nos utilisateurs.
     * @return la liste d'utilisateur.
     */
    public Users getUsers() {
        return users;
    }

    /**
     * Défini notre utilisateur actuellement connecté.
     * @param user
     */
    public void setCurrentUser(User user) {
        this.connectedUser = user;
    }

    /**
     * Récupère l'utilisateur actuellement connecté.
     * @return l'utilisateur connecté.
     */
    public User getConnectedUser() {
        return connectedUser;
    }

    /**
     * Stop la connexion, se déconnecte et écrase l'utilisateur actuel.
     */
    public void disconnectAndStopConnexion() {
        stop = true;
        connected = false;
        connectedUser = null;
    }

    /**
     * Défini les différentes tâches propres à la connexion.
     * @return la liste des tâches.
     */
    private List<TaskManager> connexionTasks() {
        var tasks = new ArrayList<TaskManager>();
        tasks.add(new SignUpTask(this));
        tasks.add(new SignInTask(this));
        return tasks;
    }

    /**
     * Crée notre liste de tâche pour leur affecté un comportement si la regex se voit être validée.
     * @return la liste des tâches.
     */
    private List<TaskManager> createTask() {
        var tasks = new ArrayList<TaskManager>();
        // region Ajouts Tâches
        tasks.add(new SignOutTask(this));
        tasks.add(new SaveFileTask(this, storManager));
        tasks.add(new RemoveFileTask(this, storManager));
        tasks.add(new GetFileTask(this, storManager));
        tasks.add(new FileListTask(this));
        // endregion
        return tasks;
    }

    public boolean receiveFile(String name, int size) throws IOException {

        this.currentFileName = name;
        this.currentFileSize = size;

        // Generate key
        byte[] decodedKey = Base64.getDecoder().decode(this.connectedUser.getAesKey());
        SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        byte[] IV = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        this.currentIv = Base64.getEncoder().encodeToString(IV);
        FileReceiverEncrypt receiver = new FileReceiverEncrypt(Program.PATH, aesKey, IV);
        return receiver.receiveFile(client.getInputStream(), name, size);
    }

    /**
     * Envoie un fichier en le chiffrant avec la clé AES pour notre client connecté.
     * @param name nom du fichier
     * @throws IOException erreur lors de l'envoi du fichier.
     */
    public void sendFile(String name) throws IOException {
        this.currentFileName = name;
        Hasher h = new Hasher();
        String hashedName = "";
        try {
            hashedName = h.clearTextToHash(name);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] decodedKey = Base64.getDecoder().decode(this.connectedUser.getAesKey());
        SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        byte[] IV = new byte[12];
        var fileList = connectedUser.getFilesList();
        for (StoredFiles f : fileList) {
            if(f.getName().equals(name)) {
                IV = Base64.getDecoder().decode(f.getIv());
            }
        }

        FileSenderDecrypt sender = new FileSenderDecrypt(Program.PATH, aesKey, IV);
        sender.sendFile(hashedName, client.getOutputStream());
    }

    /**
     * Récupère le vecteur d'initialisation
     * @return le VI
     */
    public String getCurrentIv() {
        return currentIv;
    }

    /**
     * Récupère le nom actuellement stocké du fichier.
     * @return le nom du fichier.
     */
    public String getCurrentFileName() {
        return currentFileName;
    }

    /**
     * Récupère le storProcessor de l'utilisateur pour écrire le fichier au bon endroit.
     * @param fileName le nom du fichier.
     * @return le storProcessor pour notre fichier.
     */
    public String getStorProcessorOfUser(String fileName) {
        return connectedUser.getStorageManagerOfFile(fileName, new Hasher());
    }

    /**
     * Récupère la taille de notre fichier actuel.
     * @return la taille.
     */
    public int getCurrentFileSize() {
        return currentFileSize;
    }

    /**
     * Défini le nom actuel du fichier dans notre handler.
     * @param name le nom du fichier.
     */
    public void setCurrentFileName(String name) {
        this.currentFileName = name;
    }
}
