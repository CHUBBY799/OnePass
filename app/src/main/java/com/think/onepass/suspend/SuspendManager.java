package com.think.onepass.suspend;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;

import com.think.onepass.suspend.view.SuspendLayout;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class SuspendManager {
    private static final String TAG = "SuspendManager";
    private static WindowManager.LayoutParams params;
    private static WindowManager mWindowManager;
    private static SuspendLayout mSuspendLayout;
    private static boolean mHasShown=false;
    private static boolean mInit=false;
    public static boolean getInit(){
        return mInit;
    }
    public static void createSuspendWindow(Context context,int x,int y){
        params=new WindowManager.LayoutParams();
        WindowManager windowManager=getWindowManager(context);
        mSuspendLayout=new SuspendLayout(context);
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
        params.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL & WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.gravity= Gravity.START|Gravity.TOP;
        params.softInputMode=WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        params.softInputMode|=WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
//        DisplayMetrics dm=new DisplayMetrics();
//        mWindowManager.getDefaultDisplay().getMetrics(dm);
//        int screenWidth=dm.widthPixels;
//        int screenHeigiht=dm.heightPixels;
        params.x=x;
        params.y=y;
        params.width=WindowManager.LayoutParams.WRAP_CONTENT;
        params.height=WindowManager.LayoutParams.WRAP_CONTENT;
        mSuspendLayout.setParams(params);
        mHasShown=true;
        windowManager.addView(mSuspendLayout,params);
    }
    public static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
    public static void hide(){
        if(mHasShown){
            mWindowManager.removeViewImmediate(mSuspendLayout);
        }
        mHasShown=false;
    }
    public static void show(Context context,int x,int y){
        if(!mInit){
            createSuspendWindow(context,x,y);
            mInit=true;
            return;
        }
        if(!mHasShown){
            params.x=x;
            params.y=y;
            mWindowManager.addView(mSuspendLayout,params);
            Log.d(TAG, "show: ");
//            if(suspendCache.containsKey("type")){
//                mSuspendLayout.setmSuspendFragment((int)suspendCache.get("type"));
//                Log.d(TAG, "show: "+(int)suspendCache.get("type"));
//            }
        }
        mHasShown=true;
    }
    public static void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = mSuspendLayout.isAttachedToWindow();
        }
        if (mHasShown && isAttach && mWindowManager != null)
            mWindowManager.removeView(mSuspendLayout);
        mWindowManager=null;
        mSuspendLayout=null;
        mHasShown=false;
        mInit=false;
    }



}
