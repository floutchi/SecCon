package secCon.PickLayaDeti.fileManager;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileSenderDecrypt {

    private static final int DEFAULT_BUFFER=8000;
    private String path;

    SecretKeySpec keySpec;
    byte[] IV;
    Cipher cipher;
    GCMParameterSpec gcmParameterSpec;

    public FileSenderDecrypt(String path, SecretKey key, byte[] IV) {
        this.path = path;

        this.keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        this.gcmParameterSpec = new GCMParameterSpec(16 * 8, IV);
        this.IV = IV;

        try {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

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

                byte[] decryptedByte;
                while((currentOffset < fileSize) && (bytesReaded = bisFile.read(buffer)) > 0) {

                    //System.out.printf("[FileSender] sent : %ld / %ld\n", currentOffset, fileSize);

                    decryptedByte = cipher.update(buffer);
                    out.write(decryptedByte, 0, decryptedByte.length);
                    out.flush();
                    currentOffset+= bytesReaded;
                }

                decryptedByte = cipher.doFinal();
                out.write(decryptedByte, 0, decryptedByte.length);
                out.flush();

                bisFile.close();
                return true;
            } else
                return false;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
