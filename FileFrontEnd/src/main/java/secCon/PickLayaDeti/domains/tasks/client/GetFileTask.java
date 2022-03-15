package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.Task;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetFileTask implements TaskManager {

    private final ClientHandler clientHandler;
    private final StorManager storManager;
    private final Hasher hasher;
    private Matcher matcher;

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
        String fileName = matcher.group(2);

        var newTask = new Task("GETFILE", null);
        newTask.setFileName(fileName);

        storManager.addTask(newTask);

    }
}
