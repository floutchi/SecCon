package secCon.PickLayaDeti.fileManager;

import java.io.*;

public class FileSender {
    private static final int DEFAULT_BUFFER=8000;
    private String path;

    public FileSender(String path) { this.path = path; }

    /**
     * Permet d'envoyer un fichier
     * @param filename  Nom du fichier
     * @param out       Output vers le client
     * @return          vrai si le fichier a bien été envoyé, faux sinon
     */
    public boolean sendFile(String filename, OutputStream out) {
        BufferedInputStream bisFile = null;
        int bytesReaded = 0;

        try {
            File f = new File(String.format("%s/%s", path, filename));
            long fileSize = f.length();
            if(f.exists()) {
                byte[] buffer = new byte[DEFAULT_BUFFER];
                bisFile = new BufferedInputStream(new FileInputStream(f));
                long currentOffset = 0;
                while((currentOffset < fileSize) && (bytesReaded = bisFile.read(buffer)) > 0) {

                    //System.out.printf("[FileSender] sent : %ld / %ld\n", currentOffset, fileSize);

                    out.write(buffer, 0, bytesReaded); out.flush();
                    currentOffset+= bytesReaded;
                }
                bisFile.close();
                return true;
            } else
                return false;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
