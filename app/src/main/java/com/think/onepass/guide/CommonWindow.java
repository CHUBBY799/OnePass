package com.think.onepass.guide;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.think.onepass.BaseApplication;
import com.think.onepass.R;

public abstract class CommonWindow {
    private static final int BACKGROUND_COLOR = 0xb3000000;
    public static final String TAG="CommonGuideWindow";

    protected Context mContext;
    protected ViewGroup contentView;
    protected PopupWindow mInstance;


    public CommonWindow(Context c, int layoutRes) {
        mContext = c;
        contentView = (ViewGroup) LayoutInflater.from(c).inflate(layoutRes, null, false);
    }

    protected void createWindow(){
        mInstance = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, false);
        mInstance.setAnimationStyle(R.style.popwin_anim_style);
    }

    public PopupWindow getPopupWindow() { return mInstance; }


    abstract void initView();

    abstract void initEvent();

    protected void initWindow() {
        mInstance.setBackgroundDrawable(new ColorDrawable(BACKGROUND_COLOR));
        mInstance.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mInstance.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        mInstance.setClippingEnabled(false);
    }

    public void showAtLocation(View parent, int gravity, int x, int y){
        mInstance.showAtLocation(parent,gravity,x,y);
    }

    public void dismiss(){
        if(mInstance.isShowing()){
            Log.d(BaseApplication.TAG, "dismiss: ");
            mInstance.dismiss();
        }
    }


}
