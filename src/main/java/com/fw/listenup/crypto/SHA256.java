package com.fw.listenup.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//Hash sensitive value that need to be stored in the db
public class SHA256 {
    public static String hash(String plaintext){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));
            String hashedStr = bytesToHex(encodedHash);
            return hashedStr;
        } catch(NoSuchAlgorithmException e){
            System.out.println("No algorithm found: " + e.toString());
            return null;
        }

    }

    //Hashes the plaintext value to compare it to the existing hash value
    public static boolean compareHash(String plaintext, String hashedtext){
        return hashedtext.equals(hash(plaintext));
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);

        for (byte b : hash) {
            // Convert each byte to a hexadecimal representation
            String hex = Integer.toHexString(0xff & b);

            // Ensure two characters for each byte
            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }
}
