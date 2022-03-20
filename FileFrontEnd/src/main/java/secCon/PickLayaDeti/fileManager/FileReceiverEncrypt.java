package secCon.PickLayaDeti.fileManager;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class FileReceiverEncrypt {
    private static final int DEFAULT_BUFFER = 8000;
    private final String path;

    SecretKeySpec keySpec;
    byte[] IV;
    Cipher cipher;
    GCMParameterSpec gcmParameterSpec;

    /**
     * Déclare le constructeur de notre classe.
     * @param path le chemin.
     * @param key la clé AES.
     * @param IV sous forme de tableau de bytes.
     */
    public FileReceiverEncrypt(String path, SecretKey key, byte[] IV) {
        this.path = path;
        this.keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        this.gcmParameterSpec = new GCMParameterSpec(16 * 8, IV);
        this.IV = IV;

        try {
            initCipher();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private void initCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
    }

    /**
     * Recois le fichier et le chiffre.
     * @param input le stream d'entrée.
     * @param fileName le nom du fichier.
     * @param fileSize la taille du fichier.
     * @return si la réception s'est faite correctement.
     */
    public boolean receiveFile(InputStream input, String fileName, long fileSize) {
        BufferedOutputStream bosFile = null;

        try {
            byte[] buffer = new byte[DEFAULT_BUFFER];
            bosFile = new BufferedOutputStream(new FileOutputStream(String.format("%s/%s", path, fileName)));
            long currentOffset = 0;

            byte[] encryptedBytes;
            writeBytes(input, fileSize, bosFile, buffer, currentOffset);

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

    private void writeBytes(InputStream input, long fileSize, BufferedOutputStream bosFile, byte[] buffer, long currentOffset) throws IOException {
        byte[] encryptedBytes;
        int bytesReceived;
        while((currentOffset < fileSize) && ((bytesReceived = input.read(buffer)) > 0)) {

            encryptedBytes = cipher.update(buffer);

            bosFile.write(encryptedBytes, 0, bytesReceived);
            currentOffset += bytesReceived;
        }
    }


}