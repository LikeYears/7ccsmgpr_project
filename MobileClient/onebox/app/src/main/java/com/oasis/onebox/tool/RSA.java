package com.oasis.onebox.tool;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by tehaoye on 2019/3/19.
 */

public class RSA {
    private static final String TAG = "RSA";
    private final String KEY_ALGORITHM = "RSA";

    public String encryptByPubKey(String pKey, String data) {
        try {
            // get public key
            byte[] pkeyBytes = EncodeTool.decoderBASE64(pKey);
            Log.e(TAG,pkeyBytes.toString());
            Log.e(TAG,EncodeTool.encoderBASE64(pkeyBytes));
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pkeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);

            // encrypt data
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedData = data.getBytes("utf-8");
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;

            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 117) {
                    cache = cipher.doFinal(encryptedData, offSet, 117);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * 117;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return EncodeTool.encoderBASE64(decryptedData);
        } catch (Exception e) {
            Log.e(TAG,"error in encrypt public key! :"+ e.getMessage());
        }
        return null;
    }
}
