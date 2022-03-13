package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.fileManager.FileReceiver;
import secCon.PickLayaDeti.fileManager.FileSender;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;

import javax.xml.crypto.dsig.DigestMethod;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveFileTask implements TaskManager {
    private final ClientHandler clientHandler;
    private final StorManager storManager;
    private final Hasher hasher;
    private Matcher matcher;

    public SaveFileTask(ClientHandler clientHandler, StorManager storManager) {
        this.clientHandler = clientHandler;
        this.storManager = storManager;
        this.hasher = new Hasher();
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SAVEFILE) ([a-zA-Z0-9]{5,20})(.jpg|.JPG|.gif|.GIF|.doc|.DOC|.pdf|.PDF|.png) ([0-9]{1,10})$");
        this.matcher = pattern.matcher(message);

        return matcher.matches();
    }

    @Override
    public void execute(String message) {

        try {
            // Récupération et hachage
            String filename = matcher.group(2) + matcher.group(3);

            // Récupération de la taille
            int size = Integer.parseInt(matcher.group(4));

            var newTask = new Task("SENDFILE", null);
            if (clientHandler.receiveFile(filename, size)) {
                storManager.addTask(newTask);
            }

        } catch (IOException ex) {
            System.out.println("Erreur dans la réception du fichier " + ex.getMessage());
        }
    }
}
