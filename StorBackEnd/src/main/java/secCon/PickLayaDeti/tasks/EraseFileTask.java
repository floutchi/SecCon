package secCon.PickLayaDeti.tasks;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.io.File;
import java.time.chrono.Era;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EraseFileTask implements TaskManager {

    Matcher matcher;
    ClientHandler clientHandler;

    public EraseFileTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }


    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(ERASEFILE) ([a-zA-Z0-9]{5,20})$");
        matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        String filePath = Program.PATH + "\\" + matcher.group(2);
        File f = new File(filePath);
        f.delete();

        clientHandler.sendMessage("ERASE_OK");

    }
}
