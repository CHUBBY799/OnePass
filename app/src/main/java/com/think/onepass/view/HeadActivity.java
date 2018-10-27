package com.think.onepass.view;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.think.onepass.BaseApplication;
import com.think.onepass.R;
import com.think.onepass.label.LabelFragment;
import com.think.onepass.model.Secret;
import com.think.onepass.model.SecretModelImpl;
import com.think.onepass.presenter.HeadPresenter;
import com.think.onepass.setting.ClearClipboardService;
import com.think.onepass.util.SharePreferenceUtils;
import com.think.onepass.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class HeadActivity extends AppCompatActivity implements View.OnClickListener,HeadContract.View, ScreenLock.OnTimeOutListener {
    public static final String TAGPU="pub";
    private static final String TAG = "HeadActivity";
    private ImageView setting,home,label;
    private EditText search;
    private HeadContract.Presenter mPresenter;
    private SecretFragment mainFragment,searchFragment;
    private SharedPreferences msharedPreferences;
    private Boolean isLock,inSearch=false;
    private TextView searchCancel;

    // SecretFragment Type
    public static final int MAIN=1;
    public static final int LABEL=2;
    public static final int SEARCH=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head);
        initView();
        initPresenter();
        mainFragment=new SecretFragment();
        mainFragment.setmPresenter(mPresenter);
        mainFragment.setAutoInit(true);
        mainFragment.setType(MAIN);
        replaceFragment(mainFragment);
        Log.d(TAGPU, "onCreate: ");

        //创建一个SharedPreferences实例
        msharedPreferences = this.getSharedPreferences("settings", MODE_PRIVATE);
        isLock = msharedPreferences.getBoolean("lock",false);

        BaseApplication.mScreenLock = new ScreenLock(20000); //定时20秒
        BaseApplication.mScreenLock.setOnTimeOutListener(this); //监听
    }
    @Override
    protected void onResume(){
        super.onResume();
        isLock = msharedPreferences.getBoolean("lock",false);
        if (isLock==true && BaseApplication.isUnlockActivity==false){
            BaseApplication.mScreenLock.start(); //开始计时true
        }
        else if(isLock==false|| BaseApplication.isUnlockActivity==true){
            BaseApplication.mScreenLock.stop();
        }

    }
    @Override
    protected void onPause(){
        super.onPause();
        BaseApplication.mScreenLock.stop();
    }

    /** * 当触摸就会执行此方法 */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        isLock = msharedPreferences.getBoolean("lock",false);
        if (isLock==true){
            BaseApplication.mScreenLock.resetTime(); //重置时间
        }
        else {
            BaseApplication.mScreenLock.stop();
        }
        return super.dispatchTouchEvent(ev);
    } /** * 当使用键盘就会执行此方法 */

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        isLock = msharedPreferences.getBoolean("lock",false);
        if (isLock==true){
            BaseApplication.mScreenLock.resetTime(); //重置时间
        }
        else {
            BaseApplication.mScreenLock.stop();
        }
        return super.dispatchKeyEvent(event);
    } /** * 时间到就会执行此方法 */

    @Override
    public void onTimeOut(ScreenLock screensaver) {
        Intent intent = new Intent(this, UnlockActivity.class);
        startActivity(intent);
    }
    private void initView(){
        setting=findViewById(R.id.head_setting);
        setting.setOnClickListener(this);
        home=findViewById(R.id.head_home);
        home.setOnClickListener(this);
        home.setImageResource(R.mipmap.home_icon_onclick);
        label=findViewById(R.id.head_label);
        label.setOnClickListener(this);
        searchCancel = findViewById(R.id.search_cancel);
        searchCancel.setVisibility(View.INVISIBLE);
        searchCancel.setOnClickListener(this);
        search=findViewById(R.id.head_search);
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                 if(hasFocus && !inSearch){
                     inSearch=true;
                     home.setVisibility(View.INVISIBLE);
                     label.setVisibility(View.INVISIBLE);
                     setting.setVisibility(View.INVISIBLE);
                     searchCancel.setVisibility(View.VISIBLE);
                     changeSearchWidth(true);
                     if(searchFragment == null){
                         searchFragment = new SecretFragment();
                         searchFragment.setmPresenter(mPresenter);
                         searchFragment.initData(new ArrayList<Secret>());
                         searchFragment.setType(SEARCH);
                     }
                     searchFragment.setIsFirstSearch(true);
                     replaceFragmentBackStack(searchFragment);
                 }
            }
        });
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
                if(search.getText() == null || search.getText().toString().equals("")){
                    return;
                }
                Log.d(TAG, "afterTextChanged: ");
                searchFragment.setIsFirstSearch(false);
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
            case R.id.head_home:
//                addSecret();
                replaceFragment(mainFragment);
                home.setImageResource(R.mipmap.home_icon_onclick);
                label.setImageResource(R.mipmap.label_icon_white);
                break;
            case R.id.head_label:
                Fragment fragmentHelp=getSupportFragmentManager()
                        .findFragmentById(R.id.head_fragment);
                if(fragmentHelp instanceof SecretFragment ){
                    int type=((SecretFragment) fragmentHelp).getType();
                    if(type==LABEL){
                        break;
                    }
                }
                if(!(fragmentHelp instanceof  LabelFragment)){
                    LabelFragment labelFragment=new LabelFragment();
                    labelFragment.setPresenter(mPresenter);
                    replaceFragment(labelFragment);
                    label.setImageResource(R.mipmap.label_icon_white_onclick);
                    home.setImageResource(R.mipmap.home_icon);
                }
                break;
            case R.id.search_cancel:
                inSearch=false;
                label.setVisibility(View.VISIBLE);
                home.setVisibility(View.VISIBLE);
                setting.setVisibility(View.VISIBLE);
                searchCancel.setVisibility(View.INVISIBLE);
                changeSearchWidth(false);
                search.clearFocus();
                onBackPressed();
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
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
//        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void replaceFragmentBackStack(Fragment fragment){
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
        Utils.setClipboard(this.getApplicationContext(),text);
    }


    @Override
    public void searchSecretByKey(String key, int deleted) {
//         Fragment fragment=getSupportFragmentManager()
//                .findFragmentById(R.id.head_fragment);
         List<Secret> secrets = mPresenter.searchSecretByKey(key,deleted);
         searchFragment.refreshSecrets(secrets);
         searchFragment.isShowNoData();
    }
    private void changeSearchWidth(boolean inSearch){
        if(!inSearch){
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)search.getLayoutParams();
            lp.width = Utils.dp2px(this,180);
            search.setLayoutParams(lp);
            Log.d(TAG, "changeSearchWidth: 3"+Utils.dp2px(this,180));
            return;
        }
        Log.d(TAG, "changeSearchWidth: 5 "+search.getWidth());
        int searchLeft = search.getLeft();
        int searchCacelLeft = searchCancel.getLeft();
        Log.d(TAG, "changeSearchWidth: 1"+searchLeft);
        Log.d(TAG, "changeSearchWidth: 2 "+searchCacelLeft);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)search.getLayoutParams();
        lp.width = searchCacelLeft-searchLeft-Utils.dp2px(this,10);
        search.setLayoutParams(lp);
    }
//    @Override
//    public void addSecret() {
//        Fragment fragment=getSupportFragmentManager()
//                .findFragmentById(R.id.head_fragment);
//        if(fragment instanceof SecretFragment){
//            ((SecretFragment) fragment).addSecret(new Secret());
//        }else {
//            fragment=new SecretFragment();
//            ((SecretFragment) fragment).setmPresenter(mPresenter);
//            List<Secret> secrets=new ArrayList<Secret>();
//            secrets.add(new Secret());
//            List<Integer> modes=new ArrayList<Integer>();
//            modes.add(SecretAdapter.ADD_MODE);
//            ((SecretFragment) fragment).initData(secrets,modes);
//            replaceFragment(fragment);
//        }
//
//    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if(getSupportFragmentManager().findFragmentById(R.id.head_fragment)==mainFragment){
//            Log.d(TAG, "onKeyUp: ");
//            if(event.getAction()==KeyEvent.ACTION_UP){
//            if(keyCode==KeyEvent.KEYCODE_BACK){
//                    moveTaskToBack(true);
//                return true;
//            }
//        }
//        }
//        return super.onKeyUp(keyCode,event);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(inSearch){
            if(keyCode == KeyEvent.KEYCODE_BACK) {
                Log.d(TAG, "onKeyDown: ");
                inSearch=false;
                label.setVisibility(View.VISIBLE);
                home.setVisibility(View.VISIBLE);
                setting.setVisibility(View.VISIBLE);
                searchCancel.setVisibility(View.INVISIBLE);
                changeSearchWidth(false);
                search.clearFocus();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharePreferenceUtils.setSuspendpasstimeKey(0);
    }

}
