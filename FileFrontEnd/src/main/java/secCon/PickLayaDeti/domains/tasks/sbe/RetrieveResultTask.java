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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe responsable du protocol de récupération d'un fichier.
 * Ce protocol est envoyé par le SBE.
 */
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
            // Récupère le nom du fichier & la taille.
            String fileName = matcher.group(2);
            int size = Integer.parseInt(matcher.group(3));

            String hashFileContent = matcher.group(4);

            var t = clientHandler.getConnectedUser();

            byte[] iv= new byte[12];

            User user = clientHandler.getConnectedUser();

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

    /**
     * Récupère le nom de notre fichier en le comparant avant l'empreinte du Hash.
     * @param fileName le nom du fichier en hashé.
     * @param user l'utilisateur connecté.
     * @return le nom du fichier souhaité.
     * @throws NoSuchAlgorithmException exception pour le hashage.
     */
    private String getFileNameFromList(String fileName, User user) throws NoSuchAlgorithmException {
        var fileList = user.getFilesList();
        String clearName = "";
        for (StoredFiles f: fileList) {
            var hashedName = hasher.clearTextToHash(f.getName());
            if(fileName.equals(hashedName)){
                clearName = f.getName();
            }
        }
        return clearName;
    }

    /**
     * Récupère le fichier souhaité et renvoie le message de réception (ou non).
     * @param fileName le nom du fichier
     * @param size la taille du fichier
     * @param storProcessor le storProcessor s'occupant de la tâche.
     * @param clearName le nom du fichier en clair.
     * @throws IOException Vérifie s'il y a eu un problème dans la réception du fichier.
     */
    private void receiveFileAndSendMessage(String fileName, int size, StorProcessor storProcessor, String clearName) throws IOException {
        FileReceiver fileReceiver = new FileReceiver(Program.PATH);

        if(fileReceiver.receiveFile(storProcessor.getInputStream(), fileName, size)) {
            clientHandler.sendMessage("GETFILE_OK " + clearName + " " + size);
            clientHandler.sendFile(clearName);
        }
    }
}
