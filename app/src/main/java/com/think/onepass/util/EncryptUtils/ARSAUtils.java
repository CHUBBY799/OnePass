package com.think.onepass.util.EncryptUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ARSAUtils {
    private static final String TAG = "ARSAUtils";

    // 构建Cipher实例时所传入的的字符串，默认为"RSA/NONE/PKCS1Padding"
    private static String RSA_TRANSFORM = "RSA/NONE/PKCS1Padding";
    // 进行Base64转码时的flag设置，默认为Base64.DEFAULT
    private static int BASE64_MODE = Base64.DEFAULT;

    private static final String SP_NAME = "arsa";

    private static final String INIT = "init";

    private static final String PUBLIC_KEY = "public_key";

    private static final String PRIVATE_KEY = "private_key";

    private static Context context;

    private static SharedPreferences sp;
    /**
     * 自己随机产生RSA密钥对
     *
     * @param keyLength
     *            密钥长度，1024 or 2048, 小于1024长度的密钥已经被证实是不安全的
     */
    public static KeyPair generateRSAKeyPair(int keyLength) {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keyLength); // 设置密钥长度
            keyPair = keyPairGenerator.generateKeyPair(); // 产生密钥对
        } catch (NoSuchAlgorithmException e) {
// e.printStackTrace();
            Log.e(TAG, "generateRSAKeyPair: " + e, e);
        }
        return keyPair;
    }

    public static void setContext(Context scontext){
        context = scontext;
        sp = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        if(sp.getBoolean(INIT,true)){
            init();
        }
    }
    public static void init(){
        KeyPair kp = generateRSAKeyPair(1024);
        RSAPrivateKey privateKey = (RSAPrivateKey)kp.getPrivate();
        String privateKeyString = Base64.encodeToString(privateKey.getEncoded(),Base64.DEFAULT);
        RSAPublicKey publicKey = (RSAPublicKey)kp.getPublic();
        String publicKeyString = Base64.encodeToString(publicKey.getEncoded(),Base64.DEFAULT);
        sp.edit().putString(PUBLIC_KEY,publicKeyString)
                .putString(PRIVATE_KEY,privateKeyString)
                .putBoolean(INIT,false)
                .apply();

    }

    /**
     * 将字符串形式的公钥转换为公钥对象
     *
     * @param publicKeyStr
     * @return
     */
    public static PublicKey keyStrToPublicKey(String publicKeyStr) {
        PublicKey publicKey = null;
        byte[] keyBytes = Base64.decode(publicKeyStr, BASE64_MODE);//未经过Base64编码的就省略，直接getBytes()
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "keyStrToPublicKey: "+(publicKey == null));
        return publicKey;
    }

    /**
     * 将字符串形式的私钥，转换为私钥对象
     *
     * @param privateKeyStr
     * @return
     */
    public static PrivateKey keyStrToPrivate(String privateKeyStr) {
        PrivateKey privateKey = null;
        byte[] keyBytes = Base64.decode(privateKeyStr, BASE64_MODE);//未经过Base64编码的就省略，直接getBytes()
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * 加密或解密数据的通用方法
     *
     * @param srcData
     *            待处理的数据 明文 or 密文原始数据
     * @param key
     *            公钥或者私钥
     * @param mode
     *            指定是加密还是解密，值为Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
     * @return
     */
    private static byte[] processData(byte[] srcData, Key key, int mode) {
        byte[] destData = null;
        try {
// 获取Cipher实例
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORM);
// 初始化Cipher，mode指定是加密还是解密，key为公钥或私钥
            cipher.init(mode, key);
            destData = cipher.doFinal(srcData); // 处理数据
        } catch (NoSuchAlgorithmException e) {
// e.printStackTrace();
            Log.e(TAG, "processData: " + e, e);
        } catch (NoSuchPaddingException e) {
// e.printStackTrace();
            Log.e(TAG, "processData: " + e, e);
        } catch (InvalidKeyException e) {
// e.printStackTrace();
            Log.e(TAG, "processData: " + e, e);
        } catch (BadPaddingException e) {
// e.printStackTrace();
            Log.e(TAG, "processData: " + e, e);
        } catch (IllegalBlockSizeException e) {
// e.printStackTrace();
            Log.e(TAG, "processData: " + e, e);
        }
        return destData;
    }

    /**
     * 使用公钥加密数据，结果用Base64转码
     *
     * @param srcData
     *
     * @return
     */
    public static String encryptDataByPublicKey(byte[] srcData) {
        String publicKeyString = sp.getString(PUBLIC_KEY,"none");
        Log.d(TAG, "encryptDataByPublicKey: "+ publicKeyString);
        RSAPublicKey publicKey = (RSAPublicKey)keyStrToPublicKey(publicKeyString);
        byte[] resultBytes = processData(srcData, publicKey, Cipher.ENCRYPT_MODE);
        return Base64.encodeToString(resultBytes, BASE64_MODE);
    }

    /**
     * 使用私钥解密，返回解码数据
     *
     * @param encryptedData
     *
     * @return
     */
    public static byte[] decryptDataByPrivate(String encryptedData) {
        String privateKeyString = sp.getString(PRIVATE_KEY,"none");
        Log.d(TAG, "encryptDataByPublicKey: "+ privateKeyString);
        RSAPrivateKey privateKey = (RSAPrivateKey)keyStrToPrivate(privateKeyString);
        byte[] bytes = Base64.decode(encryptedData, BASE64_MODE);
        return processData(bytes, privateKey, Cipher.DECRYPT_MODE);
    }

    /**
     * 使用私钥进行解密，解密数据转换为字符串，使用utf-8编码格式
     *
     * @param encryptedData
     *
     * @return
     */
    public static String decryptedToStrByPrivate(String encryptedData) {
        return new String(decryptDataByPrivate(encryptedData));
    }

    /**
     * 使用私钥解密，解密数据转换为字符串，并指定字符集
     *
     * @param encryptedData
     * @param privateKey
     * @param charset
     * @return
     */
//    public static String decryptedToStrByPrivate(String encryptedData, PrivateKey privateKey, String charset) {
//        try {
//            return new String(decryptDataByPrivate(encryptedData, privateKey), charset);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 使用私钥加密，结果用Base64转码
     *
     * @param srcData
     * @param privateKey
     * @return
     */
    public static String encryptDataByPrivateKey(byte[] srcData, PrivateKey privateKey) {
        byte[] resultBytes = processData(srcData, privateKey, Cipher.ENCRYPT_MODE);
        return Base64.encodeToString(resultBytes, BASE64_MODE);
    }

    /**
     * 使用公钥解密，返回解密数据
     *
     * @param encryptedData
     * @param publicKey
     * @return
     */
    public static byte[] decryptDataByPublicKey(String encryptedData, PublicKey publicKey) {
        byte[] bytes = Base64.decode(encryptedData, BASE64_MODE);
        return processData(bytes, publicKey, Cipher.DECRYPT_MODE);
    }

    /**
     * 使用公钥解密，结果转换为字符串，使用默认字符集utf-8
     *
     * @param encryptedData
     * @param publicKey
     * @return
     */
    public static String decryptedToStrByPublicKey(String encryptedData, PublicKey publicKey) {
        return new String(decryptDataByPublicKey(encryptedData, publicKey));
    }

    /**
     * 使用公钥解密，结果转换为字符串，使用指定字符集
     *
     * @param encryptedData
     * @param publicKey
     * @param charset
     * @return
     */
    public static String decryptedToStrByPublicKey(String encryptedData, PublicKey publicKey, String charset) {
        try {
            return new String(decryptDataByPublicKey(encryptedData, publicKey), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
