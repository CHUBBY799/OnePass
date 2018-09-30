package com.think.onepass.suspend.view;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.ColorSpace;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.think.onepass.R;
import com.think.onepass.model.Secret;
import com.think.onepass.model.SecretModel;
import com.think.onepass.model.SecretModelImpl;
import com.think.onepass.suspend.SuspendControlManager;
import com.think.onepass.suspend.SuspendController;
import com.think.onepass.suspend.SuspendManager;
import com.think.onepass.view.HeadActivity;

import java.util.ArrayList;
import java.util.List;

public class SuspendLayout extends FrameLayout {
    private static final String TAG = "SuspendLayout";
    private Context mContext;
    private final WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private SecretModel mModel;

    private ImageView control;
    private View mView;
    private FrameLayout mSuspendFragment;
    private ImageView mSuspendAdd;
    private EditText mAddTitle,mAddUser,mAddPassword,mSearch;
    private Button mAddConfirm,mAddCancel;
//    搜索控件
    private RecyclerView mSearchRecycler;
    private SuspendSearchAdapter mSearchAdapter;
    private List<Secret> secrets;
    private boolean initSearchView=true;//判断
    public final String SUSPEND_TYPE_SEARCH="search";
    public final String SUSPEND_TYPE_ADD="add";


    private float mTouchStartX,mTouchStartY;

    public SuspendLayout(@NonNull Context context) {
        this(context,null);
        mContext=context;
        mModel=new SecretModelImpl(context);
    }

    public SuspendLayout(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        mWindowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mView=LayoutInflater.from(context).inflate(R.layout.suspend_layout,this);

        control=mView.findViewById(R.id.suspend_control);
        control.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SuspendControlManager.show(mParams.x,mParams.y);
                SuspendManager.hide();
            }
        });

        mSearch=mView.findViewById(R.id.suspend_search);
        setEditText(mSearch);

        mSuspendFragment=mView.findViewById(R.id.suspend_fragment);
        mSuspendAdd=mView.findViewById(R.id.suspend_add);
        mSuspendAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setmSuspendFragment(SUSPEND_TYPE_ADD);
                initSearchView=true;
            }
        });

    }

    // 根据类型加载不同布局  搜索：0  添加：1
    public void setmSuspendFragment(String type) {
        switch (type){
            case SUSPEND_TYPE_SEARCH:
                mSuspendFragment.removeAllViews();
                View view=LayoutInflater.from(mContext).inflate(R.layout.suspend_search_fragment,mSuspendFragment,false);
                mSuspendFragment.addView(view);
                mSearchRecycler=findViewById(R.id.suspend_search_recycler);
                secrets=mModel.searchSecretByKey(mSearch.getText().toString(),0);
                mSearchAdapter=new SuspendSearchAdapter(secrets,mContext);
                mSearchRecycler.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));
                LinearLayoutManager manage=new LinearLayoutManager(mContext);
                mSearchRecycler.setLayoutManager(manage);
                mSearchRecycler.setAdapter(mSearchAdapter);
                initSearchView=false;
                break;
            case SUSPEND_TYPE_ADD:
                mSuspendFragment.removeAllViews();
                View view1=LayoutInflater.from(mContext).inflate(R.layout.suspend_add_fragment,mSuspendFragment,false);
                mSuspendFragment.addView(view1);
                mAddTitle=findViewById(R.id.suspend_add_title);
                mAddUser=findViewById(R.id.suspend_add_user);
                mAddPassword=findViewById(R.id.suspend_add_password);
                mAddConfirm=findViewById(R.id.suspend_add_confirm);
                mAddConfirm.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Secret secret=new Secret();
                        secret.setTitle(mAddTitle.getText().toString());
                        secret.setUser(mAddUser.getText().toString());
                        secret.setPassword(mAddPassword.getText().toString());
                        mModel.addSecret(secret);
                        Toast.makeText(mContext,"添加账户成功",Toast.LENGTH_SHORT).show();
                    }
                });
                mAddCancel=findViewById(R.id.suspend_add_cacel);
                mAddCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setmSuspendFragment(SUSPEND_TYPE_ADD);
                    }
                });
                break;
        }
    }

    //为Edittext内容改变保存值
    private void setEditText(final EditText myEditText){
        if(myEditText.getTag() instanceof TextWatcher){
            myEditText.removeTextChangedListener((TextWatcher)myEditText.getTag());
        }
        TextWatcher watcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (myEditText.getId()){
                    case R.id.suspend_search:
                        if(initSearchView){
                            setmSuspendFragment(SUSPEND_TYPE_SEARCH);
                        }else {
                            secrets.clear();
                            secrets.addAll(mModel.searchSecretByKey(myEditText.getText().toString(),0));
                            mSearchAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        };
        myEditText.addTextChangedListener(watcher);
        myEditText.setTag(watcher);
    }


    public void setParams(WindowManager.LayoutParams params){
        mParams=params;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
       int action=ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mTouchStartX=ev.getX();
                mTouchStartY=ev.getY();
                Rect rect=new Rect();
                this.getGlobalVisibleRect(rect);
                if(!rect.contains((int)mTouchStartX,(int)mTouchStartY)){
                    SuspendControlManager.show(mParams.x,mParams.y);
                    SuspendManager.hide();
                    return true;
                }
                Log.d(TAG, "onInterceptTouchEvent: down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent: move");
                float mMoveStartX=ev.getX();
                float mMoveStartY=ev.getY();
                if(Math.abs(mMoveStartX-mTouchStartX)>6&&Math.abs(mMoveStartY-mTouchStartY)>6){
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onInterceptTouchEvent: up");
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        int x=(int)event.getRawX();
        int y=(int)event.getRawY();
        switch (action){
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: move");
                mParams.x=(int)(x-mTouchStartX);
                mParams.y=(int)(y-mTouchStartY);
                mWindowManager.updateViewLayout(this,mParams);
                return true;
            case MotionEvent.ACTION_UP:
                mParams.x=SuspendControlManager.screenWidth;
                mWindowManager.updateViewLayout(this,mParams);
        }
        return false;
    }
}
