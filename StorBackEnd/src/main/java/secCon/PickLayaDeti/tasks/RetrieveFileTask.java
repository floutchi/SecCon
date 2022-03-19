package secCon.PickLayaDeti.tasks;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.fileManager.FileReceiver;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrieveFileTask implements TaskManager {

    private final ClientHandler clientHandler;
    private Matcher matcher;

    public RetrieveFileTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(RETRIEVEFILE) ([a-zA-Z0-9].{50,200})$");
        matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

        String fileName = matcher.group(2);

        File f = new File(String.format("%s/%s", Program.PATH, fileName));

        if(f.exists()) {

            Hasher h = new Hasher();
            String hashedFile = h.clearFileToHash(f);

            clientHandler.sendMessage("RETRIEVE_OK " + fileName + " " + f.length() + " " + hashedFile);

            clientHandler.sendFile(fileName);
        }


    }
}
