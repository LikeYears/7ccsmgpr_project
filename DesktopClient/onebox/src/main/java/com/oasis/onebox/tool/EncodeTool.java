package com.oasis.onebox.tool;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Base64;

public class EncodeTool {
    public static final SimpleDateFormat FORMMATTER = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    public static final byte[] decoderBASE64(String key) {
        return Base64.getDecoder().decode(key);
    }

    public static final String encoderBASE64(byte[] key) {
        return Base64.getEncoder().encodeToString(key);
    }

    public static final byte[] decoderURLBASE64(String key) {
        return Base64.getUrlDecoder().decode(key);
    }

    public static final String encoderURLBASE64(byte[] key) {
        return Base64.getUrlEncoder().encodeToString(key);
    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static String MD5(String str, int i) {
        try {
            i = i == 0 ? 10 : i;
            MessageDigest md = MessageDigest.getInstance("MD5");
            int x = 0;
            while (x < i) {
                md.update(str.getBytes());
                str = new BigInteger(1, md.digest()).toString(16);
                x++;
            }
        } catch (Exception e) {

        }
        return str;
    }
}
