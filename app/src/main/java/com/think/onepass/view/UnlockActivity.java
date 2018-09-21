package com.think.onepass.view;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.think.onepass.R;
import com.think.onepass.util.AppManager;


import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class UnlockActivity extends FragmentActivity implements View.OnClickListener{
    private static final String TAG = "UnlockActivity";
    public static final String DEFAULT_KEY_NAME="default_key";
    KeyStore keyStore;
    private  Button switchFinger,switchNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_unlock);
        switchFinger=findViewById(R.id.switch_fingerprint);
        switchNumber=findViewById(R.id.switch_number);
        switchFinger.setOnClickListener(this);
        switchNumber.setOnClickListener(this);
        if(supportFingerprint()){
            initKey();
            initCipher();
        }else{
            replaceFragment(new NumberFragment());
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.switch_fingerprint:
                replaceFragment(new FingerprintFragment());
                break;
            case R.id.switch_number:
                replaceFragment(new NumberFragment());
                break;
        }
    }
    private void replaceFragment(Fragment fragment){
        Log.d(TAG, "replaceFragment: start");
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.unlock_layout,fragment);
        transaction.commit();
    }

    public boolean supportFingerprint(){
        if(Build.VERSION.SDK_INT<23){
            Toast.makeText(this,"version low",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            KeyguardManager keyguardManager=getSystemService(KeyguardManager.class);
            FingerprintManager fingerprintManager=getSystemService(FingerprintManager.class);
            if(fingerprintManager==null||!fingerprintManager.isHardwareDetected()){
                Toast.makeText(this,"您的手机不支持指纹功能",Toast.LENGTH_SHORT).show();
                return false;
            }else if(!keyguardManager.isKeyguardSecure()){
                Toast.makeText(this,"您还未设置锁屏,请先设置锁屏并添加一个指纹",Toast.LENGTH_SHORT).show();
                return false;
            }else if(!fingerprintManager.hasEnrolledFingerprints()){
                Toast.makeText(this,"您至少需要在系统设置中添加一个指纹",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Log.d(TAG, "supportFingerprint: success");
        return true;
    }
    private void initKey(){
        try{
            keyStore=KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
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
