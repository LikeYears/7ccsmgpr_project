package com.oasis.onebox.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {
    private final Log logger = LogFactory.getLog(EncodeTool.class);

    private static String pk = null;
    private final String KEY_ALGORITHM = "RSA";
    private byte[] pubKey;
    private byte[] priKey;

    private RSA() {
        initKeyPair();
    }


    public String getPubKey() {
        if (pk != null) {
            return pk;
        }
        try {
            logger.info("pubKeyDecode::"+pubKey);
            pk = EncodeTool.encoderBASE64(pubKey);
            logger.info("PubKeyEncode::"+pk);
            return pk;
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }


    private void initKeyPair() {
        try {
            // get random key pair
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            // public key
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            this.pubKey = publicKey.getEncoded();

            // private key
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            this.priKey = privateKey.getEncoded();
            logger.info("KEY PAIR::INITIAL SUCCESS");
        } catch (Exception e) {
            logger.error("KEY PAIR::INITIAL FAIL", e);
            System.exit(1);
        }
    }


    public String encryptByPubKey(String data) {
        try {
            // get public key
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(this.pubKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);

            // encrypt data
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
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
            logger.error("error in encrypt public key!", e);
        }
        return null;
    }


    //decrypt by private key
    public String decryptByPriKey(String data) {
        try {
            // get private key
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(this.priKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

            // decrypt
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedData = EncodeTool.decoderBASE64(data);

            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;

            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 128) {
                    cache = cipher.doFinal(encryptedData, offSet, 128);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * 128;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return new String(decryptedData, "utf-8");
        } catch (Exception e) {
            logger.error("error in decrypt private key!", e);
        }
        return null;
    }

    private static RSA encrypt = null;

    public static RSA getInstance() {
        if (encrypt == null) {
            encrypt = new RSA();
        }
        return encrypt;
    }
}
