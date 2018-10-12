package com.think.onepass.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import com.think.onepass.BaseApplication;
import com.think.onepass.R;
import com.think.onepass.suspend.SuspendController;
import com.think.onepass.suspend.permission.FloatPermissionManager;
import com.think.onepass.util.ServiceUtils;
import com.think.onepass.util.SharePreferenceUtils;

public class SettingActivity extends Activity implements View.OnClickListener,ScreenLock.OnTimeOutListener,CompoundButton.OnCheckedChangeListener{
    private Switch mswitchLock;
    private RelativeLayout rlStartFloatWindow;
    private RelativeLayout rlUpdatePassword;
    private SharedPreferences msharedPreferences;
    private Boolean isLock;
    private Switch openFinger,openSuspend,openAutoClearClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //创建一个SharedPreferences实例
        msharedPreferences = this.getSharedPreferences("settings", MODE_PRIVATE);
        isLock = msharedPreferences.getBoolean("lock",false);
        //20秒无动作锁定
        mswitchLock = findViewById(R.id.secure_lock_switch);
        mswitchLock.setChecked(isLock);
        mswitchLock.setOnClickListener(this);
        BaseApplication.mScreenLock = new ScreenLock(20000); //定时20秒
        BaseApplication.mScreenLock.setOnTimeOutListener(this); //监听
        //开启悬浮窗
        rlStartFloatWindow = findViewById(R.id.secure_floatwindowstart);
        rlStartFloatWindow.setOnClickListener(this);
        //修改密码
        rlUpdatePassword = findViewById(R.id.secure_passwordupdate);
        rlUpdatePassword.setOnClickListener(this);

        openFinger=findViewById(R.id.secure_fingerprintsatrt_switch);
        openFinger.setOnCheckedChangeListener(this);
        openSuspend=findViewById(R.id.secure_floatwindowstart_switch);
        openSuspend.setOnCheckedChangeListener(this);
        openAutoClearClip=findViewById(R.id.secure_clear_switch);
        openAutoClearClip.setOnCheckedChangeListener(this);
    }
    @Override
    protected void onResume(){
        super.onResume();
        isLock = msharedPreferences.getBoolean("lock",false);
        if (isLock==true && BaseApplication.isUnlockActivity==false){
            BaseApplication.mScreenLock.start(); //开始计时
        }
        else if(isLock==false|| BaseApplication.isUnlockActivity==true){
            BaseApplication.mScreenLock.stop();
        }

    }
    @Override
    protected void onPause(){
        super.onPause();
        BaseApplication.mScreenLock.stop();
    }
    /** * 当触摸就会执行此方法 */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        BaseApplication.mScreenLock.resetTime(); //重置时间
        return super.dispatchTouchEvent(ev);
    } /** * 当使用键盘就会执行此方法 */

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        BaseApplication.mScreenLock.resetTime(); //重置时间
        return super.dispatchKeyEvent(event);
    } /** * 时间到就会执行此方法 */

    @Override
    public void onTimeOut(ScreenLock screensaver) {
        Intent intent = new Intent(this, UnlockActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //20秒无动作锁定
            case R.id.secure_lock_switch:{
                SharedPreferences.Editor meditor = msharedPreferences.edit();
                meditor.putBoolean("lock",mswitchLock.isChecked());
                meditor.commit();
                isLock = msharedPreferences.getBoolean("lock",false);
                if (isLock==true && BaseApplication.isUnlockActivity==false){

                    BaseApplication.mScreenLock.start(); //开始计时
                }
                else if(isLock==false|| BaseApplication.isUnlockActivity==true){
                    BaseApplication.mScreenLock.stop();
                }
                break;
            }
            //开启悬浮窗
            case R.id.secure_floatwindowstart:
                boolean isPermission = FloatPermissionManager.getInstance().applyFloatWindow(this);
                //有对应权限或者系统版本小于7.0
                if (isPermission || Build.VERSION.SDK_INT < 24){
                    SuspendController.getInstance().startSuspendService(this);
                }
                break;
            //修该密码
            case R.id.secure_passwordupdate:
                final AlertDialog.Builder builderupdatepassword= new AlertDialog.Builder(this);
                final LinearLayout linearlayoutUpdatePassword = (LinearLayout)getLayoutInflater().inflate
                        (R.layout.set_update_password,null);
                builderupdatepassword.setTitle("修改密码");
                builderupdatepassword.setView(linearlayoutUpdatePassword);
                builderupdatepassword.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText metNewPassWord = linearlayoutUpdatePassword.findViewById(R.id.newpasswordedittext);
                        EditText metSurePassWord = linearlayoutUpdatePassword.findViewById(R.id.surepasswordedittext);
                        SharedPreferences.Editor meditor = msharedPreferences.edit();
                        String mNewPassWord = metNewPassWord.getText().toString();
                        String mSurePassWord = metSurePassWord.getText().toString();
                        if((mNewPassWord.length()==4) && mNewPassWord.equals(mSurePassWord)){
                            meditor.putString("password",mNewPassWord);
                            meditor.commit();
                            Toast.makeText(SettingActivity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                        }
                        else if (mNewPassWord.length()==0 && mSurePassWord.length()==0){
                            return;
                        }
                        else {
                            Toast.makeText(SettingActivity.this,"修改密码失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builderupdatepassword.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builderupdatepassword.create().show();
                break;
            default:break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.secure_fingerprintsatrt_switch:
                if(isChecked){
                    SharePreferenceUtils.setFigerprintopenKey(true);
                }else {
                    SharePreferenceUtils.setFigerprintopenKey(false);
                }
                break;
            case R.id.secure_floatwindowstart_switch:
                if(isChecked){
                    boolean isPermission = FloatPermissionManager.getInstance().applyFloatWindow(this);
                    if(isPermission){
                        ((Switch)buttonView).setChecked(true);
                    }else {
                        ((Switch)buttonView).setChecked(false);
                    }
                    //有对应权限或者系统版本小于7.0
                    if (isPermission || Build.VERSION.SDK_INT < 24){
                        SuspendController.getInstance().startSuspendService(this);
                    }
                }else {
                    SuspendController.getInstance().stopSuspendService(this);
                }
                break;
            case R.id.secure_clear_switch:
                if(isChecked){
                    SharePreferenceUtils.setAutoclearKey(true);
                }else {
                    SharePreferenceUtils.setAutoclearKey(false);
                    //判断自动清除服务是否正在开启 如果是马上停止
                    if(ServiceUtils.isServiceWork(this.getApplicationContext(),
                            "com.think.onepass.setting.ClearClipboardService")){
                        sendBroadcast(new Intent("com.think.onepass.clearclipboardservice"));
                    }
                }

                break;
        }
    }

}
