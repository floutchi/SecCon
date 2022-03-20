package secCon.PickLayaDeti.domains.tasks.sbe;

import secCon.PickLayaDeti.domains.StoredFiles;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendResultTask implements TaskManager {

    ClientHandler clientHandler;
    String savedSBE;
    Matcher matcher;

    public SendResultTask(ClientHandler clientHandler, String domain) {
        this.clientHandler = clientHandler;
        this.savedSBE = domain;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SEND_ERROR|SEND_OK)$");
        this.matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

        if(matcher.group(1).equals("SEND_OK")) {
            clientHandler.sendMessage("SAVEFILE_OK");
            var user = clientHandler.getConnectedUser();
            user.addFile(new StoredFiles(clientHandler.getCurrentFileName(), clientHandler.getCurrentIv(), clientHandler.getCurrentFileSize(), savedSBE));

        } else {
            clientHandler.sendMessage("SAVEFILE_ERROR");
        }
    }
}
