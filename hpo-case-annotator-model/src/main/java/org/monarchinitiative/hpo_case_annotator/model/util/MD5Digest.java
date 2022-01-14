package org.monarchinitiative.hpo_case_annotator.model.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Calculate HEX MD5 digest for a string. The class uses {@link MessageDigest} to digest the input string and a custom
 * method to convert bytes to a HEX string, with lower-case characters.
 */
public class MD5Digest {

    private static final char[] HEX_VALUES = "0123456789abcdef".toCharArray();

    private MD5Digest() {
    }

    public static String digest(String input) {
        try {
            MessageDigest result = MessageDigest.getInstance("MD5");
            byte[] digest = result.digest(input.getBytes(StandardCharsets.UTF_8));
            char[] chars = convertByteToHexadecimal(digest);
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static char[] convertByteToHexadecimal(byte[] bytes) {
        int len = bytes.length;
        char[] hexs = new char[len * 2];

        for (int i = 0; i < len; i++) {
            int v = bytes[i] & 0xFF;
            hexs[i * 2] = HEX_VALUES[v >>> 4];
            hexs[i * 2 + 1] = HEX_VALUES[v & 0x0F];
        }

        return hexs;
    }

}
