package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SaveFileTask implements TaskManager {

    public SaveFileTask(ClientHandler clientHandler) {
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SAVEFILE) ([a-zA-Z0-9]{5,20}) ([0-9]{1,10})$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

    }
}
