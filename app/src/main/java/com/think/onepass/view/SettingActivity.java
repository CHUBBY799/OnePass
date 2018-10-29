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
import android.widget.ImageView;
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
    private RelativeLayout rlUpdatePassword,secureSavepassword,secure_autofill,secure_securitylevel;
    private SharedPreferences msharedPreferences;
    private Boolean isLock,isOpenFinger,isOpenSuspend,isopenAutoClearClip;
    private Switch mswitchLock,openFinger,openSuspend,openAutoClearClip;
    private ImageView imageViewBackToHeadActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        imageViewBackToHeadActivity = findViewById(R.id.backimagview);
        imageViewBackToHeadActivity.setOnClickListener(this);
        //创建一个SharedPreferences实例
        msharedPreferences = this.getSharedPreferences("settings", MODE_PRIVATE);

        //20秒无动作锁定
        mswitchLock = findViewById(R.id.secure_lock_switch);
        mswitchLock.setOnCheckedChangeListener(this);
        BaseApplication.mScreenLock = new ScreenLock(20000); //定时20秒
        BaseApplication.mScreenLock.setOnTimeOutListener(this); //监听


        //修改密码
        rlUpdatePassword = findViewById(R.id.secure_passwordupdate);
        rlUpdatePassword.setOnClickListener(this);

        //指纹
        openFinger=findViewById(R.id.secure_fingerprintsatrt_switch);
        openFinger.setOnCheckedChangeListener(this);

        //悬浮窗
        openSuspend=findViewById(R.id.secure_floatwindowstart_switch);
        openSuspend.setOnCheckedChangeListener(this);


        //清除剪切板
        openAutoClearClip=findViewById(R.id.secure_clear_switch);
        openAutoClearClip.setOnCheckedChangeListener(this);

        //
    }
    @Override
    protected void onResume(){
        super.onResume();
        isLock = msharedPreferences.getBoolean("lock",false);
        isOpenSuspend = msharedPreferences.getBoolean("suspend",false);
        isOpenFinger = msharedPreferences.getBoolean("finger",false);
        isopenAutoClearClip = msharedPreferences.getBoolean("clear",false);

        mswitchLock.setChecked(isLock);
        openFinger.setChecked(isOpenFinger);
        openSuspend.setChecked(isOpenSuspend);
        openAutoClearClip.setChecked(isopenAutoClearClip);
        if (isLock==true && BaseApplication.isUnlockActivity==false){
            BaseApplication.mScreenLock.start(); //开始计时
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
        isLock = msharedPreferences.getBoolean("lock",false);
        if (isLock==true){
            BaseApplication.mScreenLock.resetTime(); //重置时间
        }
        else {
            BaseApplication.mScreenLock.stop();
        }
        return super.dispatchTouchEvent(ev);
    } /** * 当使用键盘就会执行此方法 */

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        isLock = msharedPreferences.getBoolean("lock",false);
        if (isLock==true){
            BaseApplication.mScreenLock.resetTime(); //重置时间
        }
        else {
            BaseApplication.mScreenLock.stop();
        }
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
            case R.id.backimagview:{
//                try{
//                    Runtime runtime=Runtime.getRuntime();
//                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
//                }catch(IOException e){
//                }
                finish();
                break;
            }
            //修该密码
            case R.id.secure_passwordupdate:
                final AlertDialog.Builder builderupdatepassword= new AlertDialog.Builder(this);
                final LinearLayout linearlayoutUpdatePassword = (LinearLayout)getLayoutInflater().inflate
                        (R.layout.set_update_password,null);
                builderupdatepassword.setTitle("确认原密码");
                builderupdatepassword.setView(linearlayoutUpdatePassword);
                builderupdatepassword.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText metoldPassWord = linearlayoutUpdatePassword.findViewById(R.id.oldpasswordedittext);
                        String mOldPassWordfromshared =  msharedPreferences.getString("password","");
                        String mOldPassWord = metoldPassWord.getText().toString();
                        if(mOldPassWordfromshared.equals(mOldPassWord)){
                            Intent intent = new Intent(SettingActivity.this,SetNewPassWordActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(SettingActivity.this,"密码有误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builderupdatepassword.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builderupdatepassword.create().show();
            default:break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.secure_lock_switch:{
                SharedPreferences.Editor meditor_lock = msharedPreferences.edit();
                meditor_lock.putBoolean("lock",isChecked);
                meditor_lock.commit();
                isLock = msharedPreferences.getBoolean("lock",false);
                if (isLock==true && BaseApplication.isUnlockActivity==false){
                    BaseApplication.mScreenLock.resetTime(); //重新开始计时
                }
                else {
                    BaseApplication.mScreenLock.stop();
                }
                break;
            }
            case R.id.secure_fingerprintsatrt_switch:
                SharedPreferences.Editor meditor_finger = msharedPreferences.edit();
                meditor_finger.putBoolean("finger",isChecked);
                meditor_finger.commit();
                if(isChecked){
                    SharePreferenceUtils.setFigerprintopenKey(true);
                }else {
                    SharePreferenceUtils.setFigerprintopenKey(false);
                }
                break;
            case R.id.secure_floatwindowstart_switch:
                SharedPreferences.Editor meditor_suspend = msharedPreferences.edit();
                meditor_suspend.putBoolean("suspend",isChecked);
                meditor_suspend.commit();
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
                SharedPreferences.Editor meditor_clear = msharedPreferences.edit();
                meditor_clear.putBoolean("clear",isChecked);
                meditor_clear.commit();
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
