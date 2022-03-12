package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetFileTask implements TaskManager {

    public GetFileTask(ClientHandler clientHandler, StorManager storManager) {

    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(GETFILE) ([a-zA-Z0-9]{50,200})$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

    }
}
