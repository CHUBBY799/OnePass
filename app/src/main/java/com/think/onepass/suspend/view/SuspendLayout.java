package com.think.onepass.suspend.view;

import android.app.Activity;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.ColorSpace;
import android.graphics.Rect;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
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
import android.view.Gravity;
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
import com.think.onepass.util.FingerprintUtils;
import com.think.onepass.util.SharePreferenceUtils;
import com.think.onepass.util.Utils;
import com.think.onepass.view.HeadActivity;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

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
    private boolean initSearchView=true;//判断当前view是否是SearchView
    public final String SUSPEND_TYPE_SEARCH="search";
    public final String SUSPEND_TYPE_ADD="add";
    public String searchKey;

    private float mTouchStartX,mTouchStartY;

    //指纹验证
    private Cipher mCipher;
    private FingerprintManager fingerprintManager;
    private CancellationSignal mCancellationSignal;
    private EditText unlockPassword;
    private Button unlockCancel;
    private View preview;
    public static final long MAX_PASSTIME=5 * 60 * 1000;

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

        //fingerprint
        fingerprintManager=context.getSystemService(FingerprintManager.class);
        mCipher= FingerprintUtils.getCipher();
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
                mSearchAdapter.setmCallback(new SuspendSearchAdapter.Callback() {
                    @Override
                    public void setCliboardWithString(String data) {
                        long currentTime=System.currentTimeMillis();
                        long passTime=currentTime-SharePreferenceUtils.getSuspendpasstimeKey();
                        mdata=data;
                        if(passTime>MAX_PASSTIME) {
                            preview = mSuspendFragment.getChildAt(0);
                            mSuspendFragment.removeAllViews();
                            View unlockView = LayoutInflater.from(mContext).inflate(R.layout.suspend_unlock_fragment, mSuspendFragment, false);
                            startListening(mCipher);
                            mSuspendFragment.addView(unlockView);
                            unlockPassword = unlockView.findViewById(R.id.suspend_unlock_password);
                            unlockPassword.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    String password = unlockPassword.getText().toString();
                                    if (unlockPassword.length() == 4) {
                                        String correctPassword = mContext.getSharedPreferences("settings",Context.MODE_PRIVATE)
                                                .getString("password","1111");
                                        if (password.equals(correctPassword)) {
                                            SharePreferenceUtils.setSuspendpasstimeKey(System.currentTimeMillis());
                                            Utils.setClipboard(mContext.getApplicationContext(),mdata);
                                            Toast toast=Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER,0,0);
                                            toast.show();
                                            backPreSearchView(preview);
                                        }
                                    }
                                }
                            });
                            unlockCancel = unlockView.findViewById(R.id.suspend_unlocak_cancel);
                            unlockCancel.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    backPreSearchView(preview);
                                }
                            });
                        }else {
                            Utils.setClipboard(mContext.getApplicationContext(),mdata);
                            Toast toast=Toast.makeText(mContext, "复制成功", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }

                    }
                });

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
                        InputMethodManager inputMethodManager=(InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mSuspendFragment.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                        Secret secret=new Secret();
                        String title=mAddTitle.getText().toString();
                        String user=mAddUser.getText().toString();
                        if(title==null||title.equals("")){
                            Toast.makeText(mContext,"标题不能为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(user==null||user.equals("")){
                            Toast.makeText(mContext,"用户名不能为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        secret.setTitle(title);
                        secret.setUser(user);
                        secret.setPassword(mAddPassword.getText().toString());
                        mModel.addSecret(secret);
                        Toast.makeText(mContext,"添加账户成功",Toast.LENGTH_SHORT).show();
                        setmSuspendFragment(SUSPEND_TYPE_SEARCH);
                    }
                });
                mAddCancel=findViewById(R.id.suspend_add_cacel);
                mAddCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setmSuspendFragment(SUSPEND_TYPE_SEARCH);
                    }
                });
                break;
        }
    }
    private void backPreSearchView(View view){
        stopListening();
        mSuspendFragment.removeAllViews();
        mSuspendFragment.addView(view);

    }
    private String mdata;
    private void startListening(Cipher cipher){
        Log.d(TAG, "startListening: ");
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
                        Utils.setClipboard(mContext.getApplicationContext(),mdata);
                        Toast.makeText(mContext,"复制成功",Toast.LENGTH_SHORT).show();
                        backPreSearchView(preview);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                    }
                },null);
    }
    private void stopListening(){
        Log.d(TAG, "stopListening: ");
        if(mCancellationSignal!=null){
            mCancellationSignal.cancel();
            mCancellationSignal=null;
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
                        searchKey=myEditText.getText().toString();
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
