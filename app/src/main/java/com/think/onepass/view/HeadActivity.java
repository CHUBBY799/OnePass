package com.think.onepass.view;
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

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.think.onepass.R;
import com.think.onepass.label.LabelFragment;
import com.think.onepass.model.Secret;
import com.think.onepass.model.SecretModelImpl;
import com.think.onepass.presenter.HeadPresenter;
import java.util.ArrayList;
import java.util.List;

public class HeadActivity extends AppCompatActivity implements View.OnClickListener,HeadContract.View{
    public static final String TAGPU="pub";
    private static final String TAG = "HeadActivity";
    private ImageView setting,addSecret,label;
    private EditText search;
    private HeadContract.Presenter mPresenter;
    private SecretFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head);
        initView();
        initPresenter();
        mainFragment=new SecretFragment();
        mainFragment.setmPresenter(mPresenter);
        mainFragment.setAutoInit(true);
        replaceFragment(mainFragment);
        Log.d(TAGPU, "onCreate: ");

    }
    private void initView(){
        setting=findViewById(R.id.head_setting);
        setting.setOnClickListener(this);
        addSecret=findViewById(R.id.head_add);
        addSecret.setOnClickListener(this);
        label=findViewById(R.id.head_label);
        label.setOnClickListener(this);
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
    }

    private void initPresenter(){
        new HeadPresenter(this, new SecretModelImpl(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_setting:
                Intent intent=new Intent(HeadActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.head_add:
                addSecret();
                break;
            case R.id.head_label:
                Fragment fragmentHelp=getSupportFragmentManager()
                        .findFragmentById(R.id.head_fragment);
                if(!(fragmentHelp instanceof  LabelFragment)){
                    LabelFragment labelFragment=new LabelFragment();
                    labelFragment.setPresenter(mPresenter);
                    replaceFragment(labelFragment);
                }
                break;
        }
    }

    @Override
    public void setPresenter(HeadContract.Presenter presenter) {
        mPresenter=presenter;
    }
    public void replaceFragment(Fragment fragment){
        Log.d(TAG, "replaceFragment: start");
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.head_fragment,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
//    private void replaceFragment(Fragment fragment){
//        Log.d(TAG, "replaceFragment: start");
//        FragmentManager fragmentManager=getSupportFragmentManager();
//        FragmentTransaction transaction=fragmentManager.beginTransaction();
//        if(!fragment.isAdded()){
//            if(currentFragment!=null){
//                transaction.hide(currentFragment);
//                Log.d(TAG, "replaceFragment: true hide");
//            }
//            Log.d(TAG, "replaceFragment: true");
//            transaction.add(R.id.head_fragment,fragment);
//        }else {
//            Log.d(TAG, "replaceFragment:  false");
//            transaction.hide(currentFragment).show(fragment);
//        }
//        transaction.addToBackStack(null);
//        currentFragment=fragment;
//        transaction.commit();
//    }

    @Override
    public void setClipboardWithString(String text) {
        ClipboardManager clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
    }


    @Override
    public void searchSecretByKey(String key, int deleted) {
         Fragment fragment=getSupportFragmentManager()
                .findFragmentById(R.id.head_fragment);
         List<Secret> secrets = mPresenter.searchSecretByKey(key,deleted);
         if(!(fragment instanceof SecretFragment)){
             fragment=new SecretFragment();
             ((SecretFragment)fragment).setmPresenter(mPresenter);
             ((SecretFragment)fragment).initData(secrets);
             replaceFragment(fragment);
         }else{
             ((SecretFragment)fragment).refreshSecrets(secrets);
         }

    }

    @Override
    public void addSecret() {
        Fragment fragment=getSupportFragmentManager()
                .findFragmentById(R.id.head_fragment);
        if(fragment instanceof SecretFragment){
            ((SecretFragment) fragment).addSecret(new Secret());
        }else {
            fragment=new SecretFragment();
            ((SecretFragment) fragment).setmPresenter(mPresenter);
            List<Secret> secrets=new ArrayList<Secret>();
            secrets.add(new Secret());
            List<Integer> modes=new ArrayList<Integer>();
            modes.add(SecretAdapter.ADD_MODE);
            ((SecretFragment) fragment).initData(secrets,modes);
            replaceFragment(fragment);
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(getSupportFragmentManager().findFragmentById(R.id.head_fragment)==mainFragment){
            Log.d(TAG, "onKeyUp: ");
            if(event.getAction()==KeyEvent.ACTION_UP){
            if(keyCode==KeyEvent.KEYCODE_BACK){
                    moveTaskToBack(true);
                return true;
            }
        }
        }
        return super.onKeyUp(keyCode,event);
    }
}
