package com.think.onepass.suspend;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.think.onepass.suspend.view.SuspendLayout;

import static android.content.ContentValues.TAG;

public class SuspendManager {
    private static final String TAG = "SuspendManager";
    private static WindowManager.LayoutParams params;
    private static WindowManager mWindowManager;
    private static SuspendLayout mSuspendLayout;
    public static void createSuspendWindow(Context context){
        params=new WindowManager.LayoutParams();
        WindowManager windowManager=getWindowManager(context);
        mSuspendLayout=new SuspendLayout(context);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
            params.type=WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//            params.type=WindowManager.LayoutParams.TYPE_PHONE; 
            Log.d(TAG, "createSuspendWindow: ");
        }else{
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW)!=
                    PackageManager.PERMISSION_GRANTED){
                params.type= WindowManager.LayoutParams.TYPE_TOAST;
            }else {
                params.type=WindowManager.LayoutParams.TYPE_PHONE;
                Log.d(TAG, "createSuspendWindow: ");
            }
        }
//        params.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        params.flags|=WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        params.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.gravity= Gravity.START|Gravity.TOP;
        params.softInputMode=WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        params.softInputMode|=WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
        DisplayMetrics dm=new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth=dm.widthPixels;
        int screenHeigiht=dm.heightPixels;
        params.x=screenWidth;
        params.y=screenHeigiht;
        params.width=WindowManager.LayoutParams.WRAP_CONTENT;
        params.height=WindowManager.LayoutParams.WRAP_CONTENT;
        mSuspendLayout.setParams(params);
        windowManager.addView(mSuspendLayout,params);

    }
    public static WindowManager getWindowManager(Context context){
        if(mWindowManager==null){
            mWindowManager=(WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

}
