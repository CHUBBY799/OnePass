package com.think.onepass.view;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.think.onepass.R;
import javax.crypto.Cipher;

public class FingerprintFragment extends Fragment {
    private static final String TAG = "FingerprintFragment";
    private Cipher mCipher;
    private FingerprintManager fingerprintManager;
    private CancellationSignal mCancellationSignal;
    private UnlockActivity mActivty;
    private RelativeLayout mrlChangeToNumberFrament;
    public void setCipher(Cipher cipher){
        mCipher=cipher;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivty=(UnlockActivity)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fingerprintManager=getContext().getSystemService(FingerprintManager.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fingerprint_unlock,container,false);
        mrlChangeToNumberFrament = view.findViewById(R.id.changetonumframenrl);
        mrlChangeToNumberFrament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager=getFragmentManager();
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                //用新建的片段替换当前的片段
                transaction.replace(R.id.unlock_layout,new NumberFragment());
                //执行该事务
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startListening(mCipher);
    }

    /**
     * 启动指纹识别
     * @param cipher
     */
    private void startListening(Cipher cipher){
        mCancellationSignal=new CancellationSignal();
        if(mCipher==null){
            Log.d(TAG, "startListening: ciper null");
        }
        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(mCipher), mCancellationSignal
                , 0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        Toast.makeText(mActivty,"success", Toast.LENGTH_SHORT).show();
                        mActivty.onAuthenticated();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                    }
                },null);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopListening();
    }

    private void stopListening(){
        if(mCancellationSignal!=null){
            mCancellationSignal.cancel();
            mCancellationSignal=null;
        }
    }
}
