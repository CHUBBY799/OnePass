package com.think.onepass.view;

import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import com.think.onepass.R;
import com.think.onepass.util.AppManager;
import com.think.onepass.util.SharePreferenceUtils;

import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class UnlockActivity extends FragmentActivity {
    private static final String TAG = "UnlockActivity";
    public static final String DEFAULT_KEY_NAME="default_key";
    KeyStore keyStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_unlock);
        if(SharePreferenceUtils.getFingerprintopenKey() && supportFingerprint()){
            initKey();
            initCipher();
        }
        else{
            replaceFragment(new NumberFragment());
        }
    }


    /**
     * 用于切换到指纹或数字密码登陆界面
     * @param fragment 用于区分是哪种界面
     */
    private void replaceFragment(Fragment fragment){
        Log.d(TAG, "replaceFragment: start");
        //创建新的片段和事务
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        //用新建的片段替换当前的片段
        transaction.replace(R.id.unlock_layout,fragment);
        //Begin added by deqin
        //将该事务添加到返回栈中
        transaction.addToBackStack(null);
        //End added by deqin
        //执行该事务
        transaction.commit();
    }

    /**
     *判定手机是否支持指纹识别并且已经设置了锁屏指纹
     * @return true：手机支持指纹识别并且已经设置了锁屏指纹  false:手机不支持指纹识别或者没有设置锁屏指纹
     */
    public boolean supportFingerprint(){
        //判定SDK的版本，SDK版本小于23不支持指纹识别
        if(Build.VERSION.SDK_INT<23){
            Toast.makeText(this,"version low",Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            KeyguardManager keyguardManager=getSystemService(KeyguardManager.class);
            //获取FingerManager服务对象
            FingerprintManager fingerprintManager=getSystemService(FingerprintManager.class);
            //函数返回null或者硬件不支持指纹
            if(fingerprintManager==null||!fingerprintManager.isHardwareDetected()){
                Toast.makeText(this,"您的手机不支持指纹功能",Toast.LENGTH_SHORT).show();
                return false;
            }
            else if(!keyguardManager.isKeyguardSecure()){
                Toast.makeText(this,"您还未设置锁屏,请先设置锁屏并添加一个指纹",Toast.LENGTH_SHORT).show();
                return false;
            }
            else if(!fingerprintManager.hasEnrolledFingerprints()){
                Toast.makeText(this,"您至少需要在系统设置中添加一个指纹",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Log.d(TAG, "supportFingerprint: success");
        return true;
    }

    /**
     * 对称加密创建密钥
     */
    private void initKey(){
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
            Log.d(TAG, "initKey: success");
        }catch (Exception e ){
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建并初始化Cipher对象
     */
    private void initCipher(){
        try{
            SecretKey key=(SecretKey)keyStore.getKey(DEFAULT_KEY_NAME,null);
            Cipher cipher=Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"
                    +KeyProperties.BLOCK_MODE_CBC+"/"
                    +KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE,key);
            FingerprintFragment fingerprintFragment=new FingerprintFragment();
            fingerprintFragment.setCipher(cipher);
            replaceFragment(fingerprintFragment);
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
    
    public void onAuthenticated(){
        if(!AppManager.containActivity("HeadActivity")) {
            Intent intent = new Intent(this, HeadActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(event.getAction()==KeyEvent.ACTION_UP){
            if(keyCode==KeyEvent.KEYCODE_BACK){
                moveTaskToBack(true);
                return true;
            }
        }
        return super.onKeyUp(keyCode,event);
    }
}
