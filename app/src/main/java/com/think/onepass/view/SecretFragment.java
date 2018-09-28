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

import java.util.List;

public class SecretFragment extends Fragment{
    private static final String TAG = "SecretFragment";
    private RecyclerView headRecycler;
    private SecretAdapter secretAdapter;
    private Activity mActivity;

    public void setSecretAdapter(SecretAdapter secretAdapter){
        this.secretAdapter=secretAdapter;
    }
    public void scrollRecyclerView(int position){
        headRecycler.scrollToPosition(position);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(HeadActivity.TAGPU, "onCreateView: ");
        View view=inflater.inflate(R.layout.secret_head_fragment,container,false);
        headRecycler=view.findViewById(R.id.head_recycler);
        mActivity=getActivity();
        LinearLayoutManager layoutManager=new LinearLayoutManager(mActivity);
        headRecycler.setLayoutManager(layoutManager);
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
        headRecycler.setAdapter(secretAdapter);
        return view;
    }

}
