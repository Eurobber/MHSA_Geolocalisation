package com.ece.iceageophone.main.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1Hasher {

    /**
     * Encrypt a String with SHA-1
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String sha1smsMessage(String str)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = str.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexCode = "";

        for (int i = 0; i < digest.length; i++) {
            hexCode +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring(1);
        }

        return hexCode;
    }

}
