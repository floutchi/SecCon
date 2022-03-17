package secCon.PickLayaDeti.domains.tasks.sbe;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EraseResultTask implements TaskManager {

    ClientHandler clientHandler;
    Matcher matcher;

    public EraseResultTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(ERASE_OK|ERASE_ERROR)$");
        matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

        if(matcher.group(1).equals("ERASE_OK")) {
            clientHandler.sendMessage("REMOVEFILE_OK");

            var connectedUser = clientHandler.getConnectedUser();
            connectedUser.removeFile(clientHandler.getCurrentFileName());

            Program.jsonConfig.updateUsers(clientHandler.getUsers());
            Program.jsonConfig.writeUsers();

        } else {
            clientHandler.sendMessage("REMOVEFILE_ERROR");
        }
    }
}
