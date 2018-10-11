package com.think.onepass.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.think.onepass.R;
import com.think.onepass.suspend.SuspendControlManager;
import com.think.onepass.suspend.SuspendController;
import com.think.onepass.suspend.permission.FloatPermissionManager;
import com.think.onepass.util.ServiceUtils;
import com.think.onepass.util.SharePreferenceUtils;

public class SettingActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private RelativeLayout rlUpdatePassword;
    private RelativeLayout rlStartFloatWindow;
    private SharedPreferences msharedPreferences;
    private Switch openFinger,openSuspend,openAutoClearClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        rlUpdatePassword = findViewById(R.id.secure_passwordupdate);
        rlUpdatePassword.setOnClickListener(this);
        rlStartFloatWindow = findViewById(R.id.secure_floatwindowstart);
        rlStartFloatWindow.setOnClickListener(this);
        openFinger=findViewById(R.id.secure_fingerprintsatrt_switch);
        openFinger.setOnCheckedChangeListener(this);
        openSuspend=findViewById(R.id.secure_floatwindowstart_switch);
        openSuspend.setOnCheckedChangeListener(this);
        openAutoClearClip=findViewById(R.id.secure_clear_switch);
        openAutoClearClip.setOnCheckedChangeListener(this);

        //创建一个SharedPreferences实例
        msharedPreferences = this.getSharedPreferences("password", MODE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.secure_floatwindowstart:
                boolean isPermission = FloatPermissionManager.getInstance().applyFloatWindow(this);
                //有对应权限或者系统版本小于7.0
                if (isPermission || Build.VERSION.SDK_INT < 24){
                    SuspendController.getInstance().startSuspendService(this);
                }
                break;
            case R.id.secure_passwordupdate:
                final AlertDialog.Builder builderupdatepassword= new AlertDialog.Builder(this);
                final LinearLayout linearlayoutUpdatePassword = (LinearLayout)getLayoutInflater().inflate
                        (R.layout.set_update_password,null);
                builderupdatepassword.setTitle("设置/修改密码");
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
                            Toast.makeText(SettingActivity.this,"设置/修改密码成功",Toast.LENGTH_SHORT).show();
                        }
                        else if (mNewPassWord.length()==0 && mSurePassWord.length()==0){
                            return;
                        }
                        else {
                            Toast.makeText(SettingActivity.this,"设置/修改密码失败",Toast.LENGTH_SHORT).show();
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
