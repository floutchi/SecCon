package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.StoredFiles;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (storedFiles.isEmpty()) clientHandler.sendMessage("FILES");
        for (StoredFiles f : storedFiles) {
            clientHandler.sendMessage("FILES " + f.getName() + "!" + f.getSize());
        }
    }
}
