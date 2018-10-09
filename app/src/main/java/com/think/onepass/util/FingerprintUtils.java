package com.think.onepass.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.Toast;

import com.think.onepass.view.FingerprintFragment;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class FingerprintUtils {
    private static Context mContext;
    public static final String DEFAULT_KEY_NAME="default_key";
    private static KeyStore keyStore;
    private static Cipher mCipher;

    public static void setContext(Context context){
        mContext=context;
    }
    public static Cipher getCipher(){
        return mCipher;
    }

    public static boolean supportFingerprint(){
        //判定SDK的版本，SDK版本小于23不支持指纹识别
        if(Build.VERSION.SDK_INT<23){
            Toast.makeText(mContext,"version low",Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            KeyguardManager keyguardManager=mContext.getSystemService(KeyguardManager.class);
            //获取FingerManager服务对象
            FingerprintManager fingerprintManager=mContext.getSystemService(FingerprintManager.class);
            //函数返回null或者硬件不支持指纹
            if(fingerprintManager==null||!fingerprintManager.isHardwareDetected()){
                Toast.makeText(mContext,"您的手机不支持指纹功能",Toast.LENGTH_SHORT).show();
                return false;
            }
            else if(!keyguardManager.isKeyguardSecure()){
                Toast.makeText(mContext,"您还未设置锁屏,请先设置锁屏并添加一个指纹",Toast.LENGTH_SHORT).show();
                return false;
            }
            else if(!fingerprintManager.hasEnrolledFingerprints()){
                Toast.makeText(mContext,"您至少需要在系统设置中添加一个指纹",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
//        Log.d(TAG, "supportFingerprint: success");
        return true;
    }
    private static void initKey(){
        try{
            //KeyStore 是用于存储、获取密钥（Key）的容器
            keyStore=KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            //对称加密需要创建KeyGenerator对象
            KeyGenerator keyGenerator=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
            KeyGenParameterSpec.Builder builder=new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
//            Log.d(TAG, "initKey: success");
        }catch (Exception e ){
            throw new RuntimeException(e);
        }
    }
    private static void initCipher(){
        try{
            SecretKey key=(SecretKey)keyStore.getKey(DEFAULT_KEY_NAME,null);
             mCipher=Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"
                    +KeyProperties.BLOCK_MODE_CBC+"/"
                    +KeyProperties.ENCRYPTION_PADDING_PKCS7);
             mCipher.init(Cipher.ENCRYPT_MODE,key);
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
    public static void init(){
        if(supportFingerprint()){
            initKey();
            initCipher();
        }
    }
}
