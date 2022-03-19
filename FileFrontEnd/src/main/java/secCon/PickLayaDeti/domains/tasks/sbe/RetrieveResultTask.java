package secCon.PickLayaDeti.domains.tasks.sbe;

import secCon.PickLayaDeti.Program;
import secCon.PickLayaDeti.domains.StoredFiles;
import secCon.PickLayaDeti.domains.User;
import secCon.PickLayaDeti.domains.tasks.interfaces.TaskManager;
import secCon.PickLayaDeti.fileManager.FileReceiver;
import secCon.PickLayaDeti.security.Hasher;
import secCon.PickLayaDeti.thread.ClientHandler;
import secCon.PickLayaDeti.thread.StorManager;
import secCon.PickLayaDeti.thread.StorProcessor;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrieveResultTask implements TaskManager {

    private final ClientHandler clientHandler;
    private final StorManager storManager;
    private final Hasher hasher;
    private Matcher matcher;

    public RetrieveResultTask(ClientHandler clientHandler, StorManager storManager) {
        this.clientHandler = clientHandler;
        this.storManager = storManager;
        this.hasher = new Hasher();
    }

    @Override
    public boolean check(String message) {
        Pattern pattern = Pattern.compile("^(RETRIEVE_OK|RETRIEVE_ERROR) ([a-zA-Z0-9].{50,200}) ([0-9]{1,10}) ([a-zA-Z0-9].{50,200})$");
        this.matcher = pattern.matcher(message);
        return matcher.matches();
    }

    @Override
    public void execute(String message) {
        try {
            String fileName = matcher.group(2);

            StorProcessor storProcessor = storManager.getStorProcessor(fileName);

            int size = Integer.parseInt(matcher.group(3));

            String hashFileContent = matcher.group(4);

            var t = clientHandler.getConnectedUser();

            byte[] iv= new byte[12];

            User user = clientHandler.getConnectedUser();
            var fileList = user.getFilesList();
            String clearName = "";
            for (StoredFiles f: fileList) {
                var hashedName = hasher.clearTextToHash(f.getName());
                if(fileName.equals(hashedName)){
                    iv = Base64.getDecoder().decode(f.getIv());
                    clearName = f.getName();
                }
            }

            byte[] decodedKey = Base64.getDecoder().decode(user.getAesKey());
            SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");


            FileReceiver fileReceiver = new FileReceiver(Program.PATH);
            fileReceiver.receiveFile(storProcessor.getInputStream(), fileName, size);

            File receivedFile = new File(String.format("%s/%s", Program.PATH, fileName));
            String hashReceivedFile = new Hasher().clearFileToHash(receivedFile);

            if(hashFileContent.equals(hashReceivedFile)) {
                clientHandler.sendMessage("GETFILE_OK " + clearName + " " + size);
            } else {
                clientHandler.sendMessage("GETFILE_ERROR");
            }



            clientHandler.sendFile(clearName);

        } catch (IOException e) {
            e.printStackTrace();
            clientHandler.sendMessage("GETFILE_ERROR");
        }
    }
}
