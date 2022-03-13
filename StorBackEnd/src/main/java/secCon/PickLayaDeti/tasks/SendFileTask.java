package secCon.PickLayaDeti.tasks;

import secCon.PickLayaDeti.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendFileTask implements TaskManager {

    ClientHandler clientHandler;
    Matcher matcher;

    public SendFileTask(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(SENDFILE) ([a-zA-Z0-9]{50,200})(.jpg|.JPG|.gif|.GIF|.doc|.DOC|.pdf|.PDF|.png) ([0-9]{1,10}) ([a-zA-Z0-9]{50,200})$");
        this.matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {

        // Récupération et hachage
        String filename = matcher.group(2) + matcher.group(3);

        // Récupération de la taille
        int size = Integer.parseInt(matcher.group(4));

        clientHandler.receiveFile(filename, size);


    }
}
