package com.googolmo.fanfou.utils.codec;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {

    /**
     * 计算md5签名
     * 
     * @param str
     * @return
     */
    public static String md5Hex(String str) {
        try {
            return md5Hex(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    /**
     * 计算md5签名
     * 
     * @param data
     * @return
     */
    public static String md5Hex(byte[] data) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            byte messageDigest[] = md5.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String t = Integer.toHexString(0xFF & messageDigest[i]);
                if (t.length() == 1) {
                    hexString.append("0" + t);
                } else {
                    hexString.append(t);
                }
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;

    }
}
