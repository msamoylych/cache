package org.java.cache.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by msamoylych on 27.04.2017.
 */
public final class HashUtils {

    private static final String SHA1 = "SHA1";
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private HashUtils() {
    }

    public static byte[] sha1(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA1);
            return md.digest(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static String sha1Hex(String str) {
        return bytesToHex(sha1(str));
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX[v >>> 4];
            hexChars[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(hexChars);
    }
}
