package com.assignment.crypto.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHashUtil {

    public static String calculateFileHash(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
}
