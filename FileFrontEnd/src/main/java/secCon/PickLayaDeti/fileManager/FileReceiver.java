package secCon.PickLayaDeti.fileManager;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FileReceiver {
    private static final int DEFAULT_BUFFER = 8000;
    private String path;

    public FileReceiver(String path) {
        this.path = path;
    }

    /**
     * Récupère le fichier envoyer.
     * @param input le stream d'entrée.
     * @param fileName le nom du fichier.
     * @param fileSize la taille du fichier.
     * @return si l'écriture s'est bien faite ou non.
     */
    public boolean receiveFile(InputStream input, String fileName, long fileSize) {
        BufferedOutputStream bosFile = null;

        try {
            byte[] buffer = new byte[DEFAULT_BUFFER];
            bosFile = new BufferedOutputStream(new FileOutputStream(String.format("%s/%s", path, fileName)));
            long currentOffset = 0;

            WriteBytes(input, fileSize, bosFile, buffer, currentOffset);

            bosFile.flush();
            bosFile.close();

            return true;
        } catch(Exception ex) {
            ex.printStackTrace();
            if(bosFile != null) { try { bosFile.close(); } catch(Exception e) { e.printStackTrace();} }
            return false;
        }
    }


    private void WriteBytes(InputStream input, long fileSize, BufferedOutputStream bosFile, byte[] buffer, long currentOffset) throws IOException {
        int bytesReceived;
        while((currentOffset < fileSize) && ((bytesReceived = input.read(buffer)) > 0)) {

            bosFile.write(buffer, 0, bytesReceived);
            currentOffset += bytesReceived;
        }
    }


}