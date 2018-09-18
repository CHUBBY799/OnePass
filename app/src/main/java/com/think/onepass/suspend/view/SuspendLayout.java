package com.think.onepass.suspend.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.think.onepass.R;

public class SuspendLayout extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "SuspendLayout";
    private Context mContext;
    private final WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;

    private ImageView pop,search;
    private EditText data;

    private float mTouchStartX,mTouchStartY;

    public SuspendLayout(@NonNull Context context) {
        this(context,null);
        mContext=context;
    }

    public SuspendLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mWindowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        View view=LayoutInflater.from(context).inflate(R.layout.suspend_layout,this);
        pop=findViewById(R.id.tan);
        search=findViewById(R.id.search);
        data=view.findViewById(R.id.datatext);
        pop.setOnClickListener(this);
        search.setOnClickListener(this);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: ");
        int x=(int)event.getRawX();
        int y=(int)event.getRawY();
        int action=event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mTouchStartX=event.getX();
                mTouchStartY=event.getY();
                Log.d(TAG, "onTouchEvent: ");
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX=event.getX();
                float mMoveStartY=event.getY();
                if(Math.abs(mMoveStartX-mTouchStartX)>3&&Math.abs(mMoveStartY-mTouchStartY)>3){
                    Log.d(TAG, "onTouchEvent: ");
                    mParams.x=(int)(x-mTouchStartX);
                    mParams.y=(int)(y-mTouchStartY);
                    mWindowManager.updateViewLayout(this,mParams);
                    return false;
                }
                break;
        }
        return true;

    }
    public void setParams(WindowManager.LayoutParams params){
        mParams=params;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tan:
                search.setVisibility(VISIBLE);
                break;
            case R.id.search:
                data.setVisibility(VISIBLE);
                break;
        }
    }
}
