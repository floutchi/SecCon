package secCon.PickLayaDeti.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Hasher {

    public Hasher() { }

    public String clearTextToHash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-384");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);

        StringBuilder hashtext = new StringBuilder(no.toString(16));

        while (hashtext.length() < 32) {
            hashtext.insert(0, "0");
        }

        return hashtext.toString();
    }
}
