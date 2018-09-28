package com.think.onepass.suspend.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.think.onepass.R;
import com.think.onepass.suspend.SuspendControlManager;
import com.think.onepass.suspend.SuspendManager;

public class SuspendControlLayout extends RelativeLayout {
    private static final String TAG = "SuspendControlLayout";
    private WindowManager.LayoutParams mParams;
    private final WindowManager mWindowManager;
    private Context mContext;
    private boolean isClick=false;
    private static boolean mHasShown=false;
    private static float mTouchStartX,mTouchStartY;
    private static long startTime,endTime;
    public SuspendControlLayout(Context context) {
        this(context,null);
        mContext=context;
    }
    public void setmParams(WindowManager.LayoutParams params){
        mParams=params;
    }

    public SuspendControlLayout(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.suspend_control_layout,this);
        mWindowManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isClick=false;
        int action=event.getAction();
        int x=(int)event.getRawX();
        int y=(int)event.getRawY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                mTouchStartX=event.getX();
                mTouchStartY=event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX=event.getX();
                float mMoveStartY=event.getY();
                if(Math.abs(mMoveStartX-mTouchStartX)>3&&Math.abs(mMoveStartY-mTouchStartY)>3){
                    mParams.x=(int)(x-mTouchStartX);
                    mParams.y=(int)(y-mTouchStartY);
                    mWindowManager.updateViewLayout(this,mParams);
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouch: up ");
                endTime = System.currentTimeMillis();
                if ((endTime - startTime) > 0.1 * 1000L) {
                    isClick = false;
                    mParams.x=SuspendControlManager.screenWidth;
                    mWindowManager.updateViewLayout(this,mParams);
                } else {
                    isClick = true;
                }
                if(isClick){
                    SuspendManager.show(mContext,mParams.x,mParams.y);
                    SuspendControlManager.hide();
                }
                break;

        }
        return true;
    }
}
