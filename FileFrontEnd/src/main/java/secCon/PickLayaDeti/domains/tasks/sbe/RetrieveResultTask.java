package secCon.PickLayaDeti.domains.tasks.sbe;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.StoredFiles;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.fileManager.FileReceiverDecrypt;
import secCon.PickLayaDeti.fileManager.FileReceiverEncrypt;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;
import secCon.PickLayaDeti.thread.StorProcessor;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Base64;
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

            byte[] iv= new byte[12];

            User user = clientHandler.getConnectedUser();
            var fileList = user.getFilesList();
            for (StoredFiles f: fileList) {
                if(fileName==f.getName()){
                    iv = Base64.getDecoder().decode(f.getIv());
                }
            }

            byte[] decodedKey = Base64.getDecoder().decode(user.getAesKey());
            SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");


            FileReceiverDecrypt fileReceiver = new FileReceiverDecrypt(Program.PATH, aesKey, iv );
            fileReceiver.receiveFile(storProcessor.getInputStream(), fileName, size);

            clientHandler.sendMessage("GETFILE_OK " + fileName + " " + size);

            clientHandler.sendFile(fileName);

        } catch (IOException e) {
            e.printStackTrace();
            clientHandler.sendMessage("GETFILE_ERROR");
        }
    }
}
