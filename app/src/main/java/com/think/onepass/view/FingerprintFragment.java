package com.think.onepass.view;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.think.onepass.R;

import javax.crypto.Cipher;

public class FingerprintFragment extends Fragment{
    private Cipher mCipher;
    private FingerprintManager fingerprintManager;
    private CancellationSignal mCancellationSignal;
    private UnlockActivity mActivty;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fingerprint_unlock,container,false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startListening(mCipher);
    }
    private void startListening(Cipher cipher){
        mCancellationSignal=new CancellationSignal();
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
                        Toast.makeText(mActivty,"success",Toast.LENGTH_SHORT).show();
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
