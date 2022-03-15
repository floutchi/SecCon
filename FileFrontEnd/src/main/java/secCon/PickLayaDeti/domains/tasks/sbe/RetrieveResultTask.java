package secCon.PickLayaDeti.domains.tasks.sbe;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.fileManager.FileReceiver;
import secCon.PickLayaDeti.fileManager.FileSender;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;
import secCon.PickLayaDeti.thread.StorProcessor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrieveResultTask implements TaskManager {

    private final ClientHandler clientHandler;
    private final StorManager storManager;
    private Matcher matcher;

    public RetrieveResultTask(ClientHandler clientHandler, StorManager storManager) {
        this.clientHandler = clientHandler;
        this.storManager = storManager;
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(RETRIEVE_OK|RETRIEVE_ERROR) ([a-zA-Z0-9].{5,20}) ([0-9]{1,10})$");
        this.matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        try {
            String fileName = matcher.group(2);

            StorProcessor storProcessor = storManager.getStorProcessor(fileName);

            int size = Integer.parseInt(matcher.group(3));

            FileReceiver fileReceiver = new FileReceiver(Program.PATH);
            fileReceiver.receiveFile(storProcessor.getInputStream(), fileName, size);

            clientHandler.sendMessage("GETFILE_OK " + fileName + size);

            clientHandler.sendFile(fileName);

        } catch (IOException e) {
            e.printStackTrace();
            clientHandler.sendMessage("GETFILE_ERROR");
        }
    }
}
