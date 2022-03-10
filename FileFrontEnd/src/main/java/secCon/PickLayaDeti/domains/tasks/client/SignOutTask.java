package secCon.PickLayaDeti.domains.tasks.client;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignOutTask implements TaskManager {
    public SignOutTask(ClientHandler clientHandler) {
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SIGNOUT)$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        // ciao
    }
}
