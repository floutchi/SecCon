package secCon.PickLayaDeti.security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class Hasher {

    public Hasher() { }

    /**
     * Permet de hasher le contenu d'un fichier
     * @param input Fichier à hasher
     * @return      Le hash du contenu du fichier
     */
    public String clearFileToHash(File input) {
        byte[] buffer = new byte[8192];
        int count;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            BufferedInputStream bis = new BufferedInputStream((new FileInputStream(input)));

            while((count = bis.read(buffer)) < 0) {
                digest.update(buffer, 0, count);
            }
            bis.close();

            byte[] hash = digest.digest();
            return Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return "";
        }

    }

}
