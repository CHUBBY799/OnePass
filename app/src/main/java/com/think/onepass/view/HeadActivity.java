package com.think.onepass.view;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.think.onepass.R;
import com.think.onepass.model.Secret;
import com.think.onepass.model.SecretModel;
import com.think.onepass.model.SecretModelImpl;
import com.think.onepass.presenter.HeadPresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HeadActivity extends AppCompatActivity implements View.OnClickListener,HeadContract.View{
    public static final String TAGPU="pub";
    private static final String TAG = "HeadActivity";
    private ImageView setting,addSecret;
    private EditText search;
    private HeadContract.Presenter mPresenter;
    private List<Secret> mSecrets;
    private List<Integer> mSecretMode;
    private SecretAdapter secretAdapter;
    private int focusedPosition;

    @Override
    public void setPresenter(HeadContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public void setSecrets(List<Secret> secrets) {
        if(mSecrets==null) mSecrets=new ArrayList<>();
        mSecrets.clear();
        mSecrets.addAll(secrets);
        if(mSecretMode==null) mSecretMode=new ArrayList<>();
        mSecretMode.clear();
        List<Integer> mSecretModeHelp=new ArrayList<>();
        for(int i=0;i<secrets.size();i++){
            mSecretModeHelp.add(SecretAdapter.UPDATE_MODE);
        }
        mSecretMode.addAll(mSecretModeHelp);
        if(secretAdapter!=null){
            secretAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head);
        setting=findViewById(R.id.head_setting);
        setting.setOnClickListener(this);
        addSecret=findViewById(R.id.head_add);
        addSecret.setOnClickListener(this);
        search=findViewById(R.id.head_search);
        TextWatcher watcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: ");

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: ");
                searchSecretByKey(search.getText().toString(),0);
            }
        };
        search.addTextChangedListener(watcher);
        initPresenter();
        initSecrets();
//        loadTest();
        SecretFragment secretFragment=new SecretFragment();
        secretFragment.setSecretAdapter(secretAdapter);
        replaceFragment(secretFragment);
        Log.d(TAGPU, "onCreate: ");
    }
    private void loadTest(){
        mSecrets=new ArrayList<>();
        for(int i=0;i<10;i++){
            Secret secret=new Secret();
            secret.setUser("zepeng"+i+"@pass.com");
            if(i%2==0) {
                secret.setPassword("someone" + i);
            }else {
                secret.setPassword("");
            }
            secret.setLabel("pass");
            Date date=new Date();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            secret.setLastTime(format.format(date));
            mSecrets.add(secret);

    }
        mSecretMode=new ArrayList<>();
        for(int i=0;i<mSecrets.size();i++){
            mSecretMode.add(SecretAdapter.UPDATE_MODE);
        }
        secretAdapter=new SecretAdapter(mSecrets,mSecretMode,this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_setting:
                Intent intent=new Intent(HeadActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.head_add:
                Secret secret=new Secret();
                mSecrets.add(0,secret);
                mSecretMode.add(0,SecretAdapter.ADD_MODE);
                secretAdapter.notifyItemInserted(0);
                secretAdapter.notifyItemRangeChanged(0,mSecrets.size());
                SecretFragment secretFragment=(SecretFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.head_fragment);
                secretFragment.scrollRecyclerView(0);
                break;
        }
    }

    private void initPresenter(){
        new HeadPresenter(this, new SecretModelImpl(this));
    }

    @Override
    public void initSecrets() {
        mPresenter.initSecrets();
//        mSecretMode=new ArrayList<>();
//        for(int i=0;i<mSecrets.size();i++){
//            mSecretMode.add(SecretAdapter.UPDATE_MODE);
//        }
        secretAdapter=new SecretAdapter(mSecrets,mSecretMode,this);
    }
    private void replaceFragment(Fragment fragment){
        Log.d(TAG, "replaceFragment: start");
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.head_fragment,fragment);
        transaction.commit();
    }

    @Override
    public void setClipboardWithString(String text) {
        ClipboardManager clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
    }

    @Override
    public Map<String, Object> addSecrets(Secret secret) {
        return mPresenter.addSecrets(secret);
    }

    @Override
    public String updateSecrets(Secret secret) {
        return mPresenter.updateSecrets(secret);
    }

    @Override
    public void deleteSecret(long id) {
        mPresenter.deleteSecret(id);
    }

    @Override
    public void searchSecretByKey(String key, int deleted) {
         mPresenter.searchSecretByKey(key,deleted);
    }

}
