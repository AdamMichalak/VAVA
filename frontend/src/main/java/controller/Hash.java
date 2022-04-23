package controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    public static String kod(String input) throws NoSuchAlgorithmException {
        MessageDigest message = MessageDigest.getInstance("SHA-1");
        byte[] messageDigest = message.digest(input.getBytes());

        BigInteger mess = new BigInteger(1, messageDigest);

        String hash = mess.toString(16);
        int dlzka = hash.length();

        while (dlzka < 32) {
            hash = "0" + hash;
        }
        return hash;
    }
}
