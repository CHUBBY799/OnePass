package com.think.onepass.suspend;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.think.onepass.R;
import com.think.onepass.suspend.view.SuspendControlLayout;
import com.think.onepass.suspend.view.SuspendLayout;

public class SuspendControlManager {
    private static final String TAG = "SuspendControlManager";
    private static WindowManager.LayoutParams params;
    private static WindowManager mWindowManager;
    private static Context mContext;
    private static SuspendControlLayout suspendControlLayout;
    private static boolean mHasShown=false;
    public static int screenWidth;
    public static void createSuspendWindow(final Context context){
        mContext=context;
        params=new WindowManager.LayoutParams();
        suspendControlLayout=new SuspendControlLayout(context);
        WindowManager windowManager=getWindowManager(context);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            params.type=WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
            params.type=WindowManager.LayoutParams.TYPE_PHONE;

        }else{
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW)!=
                    PackageManager.PERMISSION_GRANTED){
                params.type= WindowManager.LayoutParams.TYPE_TOAST;
            }else {
                params.type=WindowManager.LayoutParams.TYPE_PHONE;
            }
        }
        params.format = PixelFormat.RGBA_8888;
//        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
//        params.flags &= ~WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity= Gravity.START|Gravity.TOP;
//        params.softInputMode=WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
//        params.softInputMode|=WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
        DisplayMetrics dm=new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth=dm.widthPixels;
        int screenHeigiht=dm.heightPixels;
        params.x=screenWidth;
        params.y=screenHeigiht;
        params.width=WindowManager.LayoutParams.WRAP_CONTENT;
        params.height=WindowManager.LayoutParams.WRAP_CONTENT;
        suspendControlLayout.setmParams(params);
        mHasShown=true;
        windowManager.addView(suspendControlLayout,params);
    }
    public static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
    public static void hide(){
        if(mHasShown){
            mWindowManager.removeViewImmediate(suspendControlLayout);
        }
        mHasShown=false;
    }
    public static void show(int x,int y){
        if(!mHasShown){
            params.x=x;
            params.y=y;
            mWindowManager.addView(suspendControlLayout,params);

        }
        mHasShown=true;
    }
    public static void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = suspendControlLayout.isAttachedToWindow();
        }
        if (mHasShown && isAttach && mWindowManager != null)
            mWindowManager.removeView(suspendControlLayout);
        suspendControlLayout=null;
        mWindowManager=null;
        mHasShown=false;
    }



}
