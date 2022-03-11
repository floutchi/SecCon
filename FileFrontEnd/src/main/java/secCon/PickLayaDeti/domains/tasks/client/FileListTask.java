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
    private final User connectedUser;

    public FileListTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.connectedUser = clientHandler.getConnectedUser();
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(FILELIST)$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        List<StoredFiles> storedFiles = connectedUser.getFilesList();
        for (StoredFiles f : storedFiles) {
            clientHandler.sendMessage("FILES " + f.getName() + "!" + f.getSize());
        }
    }
}
