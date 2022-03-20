package secCon.PickLayaDeti.tasks;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.thread.ClientHandler;

import java.io.File;
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
        Pattern pattern = Pattern.compile("^(SENDFILE) ([a-zA-Z0-9].{50,200}) ([0-9]{1,10}) ([a-zA-Z0-9].{50,200})$");
        this.matcher = pattern.matcher(message);
        return matcher.matches();
    }

    /**
     * Va recevoir un fichier
     * @param message   Message reçu et valide
     */
    @Override
    public void execute(String message) {

        // Récupération et hachage
        String filename = matcher.group(2);

        // Récupération de la taille
        int size = Integer.parseInt(matcher.group(3));

        clientHandler.receiveFile(filename, size);

        String receivedHashFile = matcher.group(4);

        String hashedFileContent = new Hasher().clearFileToHash(new File(String.format("%s/%s", Program.PATH, filename)));


        if(receivedHashFile.equals(hashedFileContent)) {
            clientHandler.sendMessage("SEND_OK");
        } else {
            clientHandler.sendMessage("SEND_ERROR");
        }
    }
}
