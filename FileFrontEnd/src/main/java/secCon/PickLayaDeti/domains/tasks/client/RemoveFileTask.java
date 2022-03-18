package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String fileName = matcher.group(2);
        User currentUser = clientHandler.getConnectedUser();
        var storage = currentUser.getStorageManagerOfFile(fileName);
        if (storage != null) {
            var newTask = new Task("REMOVEFILE", storage);

            newTask.setFileName(fileName);

            clientHandler.setCurrentFileName(fileName);
            storManager.addTask(newTask);
        }

    }
}
