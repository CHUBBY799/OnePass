package com.think.onepass.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ImageView;
import android.widget.TextView;

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
    private boolean autoInit=false,isSearchFirst=false; //判断是否需要自己初始化数据.
    private int type;//主页面 ：1    label项的页面 ：2  search :3
    private FloatingActionButton add;
    private View mNodataTv,mSearchNoData;

    public void setmPresenter(HeadContract.Presenter presenter){
        mPresenter=presenter;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(HeadActivity.TAGPU, "onCreateView:1 ");
        View view=inflater.inflate(R.layout.secret_head_fragment,container,false);
        add=(FloatingActionButton)(view.findViewById(R.id.secret_add_item));
        mNodataTv=view.findViewById(R.id.head_no_data);
        mSearchNoData = view.findViewById(R.id.search_no_data);
        if(type != 1){
            add.hide();
        }else {
            add.show();
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addSecret(new Secret());
                }
            });
        }
        headRecycler=view.findViewById(R.id.head_recycler);
        if(autoInit){
            autoInitData();
        }

        initRecycler();
        //当点击外部时，内部的输入框失去焦点
        headRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                headRecycler.setFocusable(false);
                headRecycler.clearFocus();
//                headRecycler.setFocusableInTouchMode(true);
//                headRecycler.requestFocus();
                InputMethodManager inputMethodManager=(InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        return view;
    }
    public void setType(int type){
        this.type=type;
    }
    public int getType(){
        return type;
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
    public void setIsFirstSearch(boolean is){
        isSearchFirst = is;
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

            @Override
            public void showNodata() {
                isShowNoData();
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

    @Override
    public void onResume() {
        super.onResume();
        isShowNoData();
    }
    public void isShowNoData(){
        if(mSecretList.size() == 0){
            if(type == 1){
                mSearchNoData.setVisibility(View.INVISIBLE);
                mNodataTv.setVisibility(View.VISIBLE);
                ImageView iv = mNodataTv.findViewById(R.id.main_no_data_iv);
                TextView tvTop = mNodataTv.findViewById(R.id.main_no_data_tv_top);
                TextView tvBottom = mNodataTv.findViewById(R.id.main_no_data_tv_bottom);
                iv.setImageResource(R.mipmap.no_passport_icon);
                tvTop.setText(getResources().getString(R.string.main_no_data_top));
                tvBottom.setText(getResources().getString(R.string.main_no_data_bottom));
            }else if(type == 3 ){
                mNodataTv.setVisibility(View.INVISIBLE);
                mSearchNoData.setVisibility(View.VISIBLE);
                ImageView iv = mSearchNoData.findViewById(R.id.search_no_data_iv);
                TextView tv = mSearchNoData.findViewById(R.id.search_no_data_tv);
                if(isSearchFirst){
                    iv.setImageResource(R.mipmap.please_search_icon);
                    tv.setText(getResources().getString(R.string.search_first));
                }else {
                    iv.setImageResource(R.mipmap.no_search_result_icon);
                    tv.setText(getResources().getString(R.string.search_no_data));
                }

            }
        }else {
            mNodataTv.setVisibility(View.INVISIBLE);
            mSearchNoData.setVisibility(View.INVISIBLE);
        }
    }
}
