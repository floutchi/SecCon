package secCon.PickLayaDeti.fileManager;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;


public class FileReceiver {
    private static final int DEFAULT_BUFFER = 8000;
    private String path;

    public FileReceiver(String path) { this.path = path; }

    public boolean receiveFile(InputStream input, String fileName, long fileSize) {
        int bytesReceived = 0;
        BufferedOutputStream bosFile = null;

        try {
            byte[] buffer = new byte[DEFAULT_BUFFER];
            bosFile = new BufferedOutputStream(new FileOutputStream(String.format("%s/%s", path, fileName)));
            long currentOffset = 0;

            while((currentOffset < fileSize) && ((bytesReceived = input.read(buffer)) > 0)) {

                //System.out.printf("[FileReceiver] received : %ld / %ld\n", currentOffset, fileSize);
                bosFile.write(buffer, 0, bytesReceived);
                currentOffset += bytesReceived;
            }
            bosFile.flush();
            bosFile.close();

            return true;
        } catch(Exception ex) {
            ex.printStackTrace();
            if(bosFile != null) { try { bosFile.close(); } catch(Exception e) { e.printStackTrace();} }
            return false;
        }
    }

}
