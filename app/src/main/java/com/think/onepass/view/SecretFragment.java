package com.think.onepass.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.think.onepass.R;
import com.think.onepass.model.Secret;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SecretFragment extends Fragment{
    private static final String TAG = "SecretFragment";
    private RecyclerView headRecycler;
    private SecretAdapter secretAdapter;
    private List<Integer> mSecretMode;
    private List<Secret> mSecretList;
    private Activity mActivity;
    private HeadContract.Presenter mPresenter;
    private boolean autoInit=false; //判断是否需要自己初始化数据.

    public void setmPresenter(HeadContract.Presenter presenter){
        mPresenter=presenter;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(HeadActivity.TAGPU, "onCreateView: ");
        View view=inflater.inflate(R.layout.secret_head_fragment,container,false);
        headRecycler=view.findViewById(R.id.head_recycler);
        if(autoInit){
            autoInitData();
        }
        initRecycler();
        //当点击外部时，内部的输入框失去焦点
        headRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                headRecycler.setFocusable(true);
                headRecycler.setFocusableInTouchMode(true);
                headRecycler.requestFocus();
                InputMethodManager inputMethodManager=(InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        return view;
    }
    public void setAutoInit(boolean autoInit){
        this.autoInit=autoInit;
    }
    private void autoInitData(){
        mSecretList=mPresenter.getSecretsByLasttimeDesc();
        mSecretMode=new ArrayList<>();
        for(Secret secret:mSecretList){
            mSecretMode.add(SecretAdapter.NORMAL_MODE);
        }
    }
    private void initRecycler(){
        mActivity=getActivity();
        LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity);
        headRecycler.setLayoutManager(layoutManager);
        secretAdapter=new SecretAdapter(mSecretList,mSecretMode,mActivity);
        secretAdapter.setCallBack(new SecretAdapter.Callback() {
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
        });
        headRecycler.setAdapter(secretAdapter);
    }
    public void initData(List<Secret> secrets){
        mSecretList=secrets;
        mSecretMode=new ArrayList<>();
        for(Secret secret:secrets){
            mSecretMode.add(SecretAdapter.NORMAL_MODE);
        }
    }
    public void initData(List<Secret> secrets,List<Integer> mode){
        this.mSecretList=secrets;
        this.mSecretMode=mode;
    }

    public void addSecret(Secret secret){
        mSecretList.add(0,secret);
        mSecretMode.add(0,SecretAdapter.ADD_MODE);
        secretAdapter.notifyDataSetChanged();
    }
    public void refreshSecrets(List<Secret> secrets){
        mSecretList.clear();
        mSecretList.addAll(secrets);
        mSecretMode.clear();
        for (Secret secret:mSecretList){
            mSecretMode.add(SecretAdapter.NORMAL_MODE);
        }
        secretAdapter.notifyDataSetChanged();
    }

}
