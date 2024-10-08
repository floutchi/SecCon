package secCon.PickLayaDeti.security;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class Hasher {

    public Hasher() { }

    public String clearTextToHash(String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-384");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);

        StringBuilder hashtext = new StringBuilder(no.toString(16));

        while (hashtext.length() < 32) {
            hashtext.insert(0, "0");
        }

        return hashtext.toString();
    }

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
