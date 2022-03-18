package secCon.PickLayaDeti.fileManager;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class FileReceiverEncrypt {
    private static final int DEFAULT_BUFFER = 8000;
    private String path;

    SecretKeySpec keySpec;
    byte[] IV;
    Cipher cipher;
    GCMParameterSpec gcmParameterSpec;

    public FileReceiverEncrypt(String path, SecretKey key, byte[] IV) {
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

    public boolean receiveFile(InputStream input, String fileName, long fileSize) {
        int bytesReceived = 0;
        BufferedOutputStream bosFile = null;

        try {
            byte[] buffer = new byte[DEFAULT_BUFFER];
            bosFile = new BufferedOutputStream(new FileOutputStream(String.format("%s/%s", path, fileName)));
            long currentOffset = 0;

            byte[] encryptedBytes;
            while((currentOffset < fileSize) && ((bytesReceived = input.read(buffer)) > 0)) {

                //System.out.printf("[FileReceiver] received : %ld / %ld\n", currentOffset, fileSize);

                encryptedBytes = cipher.update(buffer);

                bosFile.write(encryptedBytes, 0, bytesReceived);
                currentOffset += bytesReceived;
            }

            encryptedBytes = cipher.doFinal();
            bosFile.write(encryptedBytes, 0, encryptedBytes.length);

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