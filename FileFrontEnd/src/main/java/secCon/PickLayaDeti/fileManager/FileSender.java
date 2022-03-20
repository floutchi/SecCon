package secCon.PickLayaDeti.fileManager;

import java.io.*;

public class FileSender {
    private static final int DEFAULT_BUFFER=8000;
    private String path;

    public FileSender(String path) {
        this.path = path;
    }

    public boolean sendFile(String filename, OutputStream out) {
        BufferedInputStream bisFile;

        try {
            File f = new File(String.format("%s/%s", path, filename));
            long fileSize = f.length();
            if(f.exists()) {
                byte[] buffer = new byte[DEFAULT_BUFFER];
                bisFile = new BufferedInputStream(new FileInputStream(f));
                long currentOffset = 0;
                writeBytes(out, bisFile, fileSize, buffer, currentOffset);
                bisFile.close();
                return true;
            } else
                return false;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void writeBytes(OutputStream out, BufferedInputStream bisFile, long fileSize, byte[] buffer, long currentOffset) throws IOException {
        int bytesReaded;
        while((currentOffset < fileSize) && (bytesReaded = bisFile.read(buffer)) > 0) {
            out.write(buffer, 0, bytesReaded); out.flush();
            currentOffset += bytesReaded;
        }
    }

}
