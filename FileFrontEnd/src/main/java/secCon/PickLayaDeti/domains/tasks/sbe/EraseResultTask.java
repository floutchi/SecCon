package secCon.PickLayaDeti.domains.tasks.sbe;

import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EraseResultTask implements TaskManager {

    ClientHandler clientHandler;

    public EraseResultTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(ERASE_OK|ERASE_ERROR)$");
        Matcher matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        clientHandler.sendMessage("REMOVEFILE_OK");
    }
}
