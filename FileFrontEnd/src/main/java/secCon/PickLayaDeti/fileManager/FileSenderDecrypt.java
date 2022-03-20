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
    private final String path;

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
            initCipher();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise Cipher pour le chiffrement.
     * @throws NoSuchAlgorithmException pas d'algorithme trouvé.
     * @throws NoSuchPaddingException problème lors de la détermination du Padding.
     * @throws InvalidKeyException clé invalide.
     * @throws InvalidAlgorithmParameterException paramètres de l'algorithme .
     */
    private void initCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
    }

    /**
     * Envoie un fichier dans la sortie passée en paramètre.
     * @param filename le nom du fichier.
     * @param out la sortie du serveur.
     * @return vrai (ou faux)
     */
    public boolean sendFile(String filename, OutputStream out) {
        BufferedInputStream bisFile;

        try {
            File f = new File(String.format("%s/%s", path, filename));
            long fileSize = f.length();
            if(f.exists()) {
                byte[] buffer = new byte[DEFAULT_BUFFER];
                bisFile = new BufferedInputStream(new FileInputStream(f));
                long currentOffset = 0;

                byte[] decryptedByte;
                writeBytes(out, bisFile, fileSize, buffer, currentOffset);

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

    private void writeBytes(OutputStream out, BufferedInputStream bisFile, long fileSize, byte[] buffer, long currentOffset) throws IOException {
        byte[] decryptedByte;
        int bytesReaded;
        while((currentOffset < fileSize) && (bytesReaded = bisFile.read(buffer)) > 0) {

            decryptedByte = cipher.update(buffer);
            out.write(decryptedByte, 0, decryptedByte.length);
            out.flush();
            currentOffset += bytesReaded;
        }
    }
}
