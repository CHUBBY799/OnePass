package com.think.onepass.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.think.onepass.R;
import com.think.onepass.suspend.SuspendController;
import com.think.onepass.suspend.permission.FloatPermissionManager;

public class SettingActivity extends Activity implements View.OnClickListener{
    private Button openSuspend;
    private Button mbtSet_Update_PassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        openSuspend=findViewById(R.id.setting_suspend_open);
        mbtSet_Update_PassWord = findViewById(R.id.set_update_password_button);
        openSuspend.setOnClickListener(this);
        mbtSet_Update_PassWord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_suspend_open:{
                boolean isPermission = FloatPermissionManager.getInstance().applyFloatWindow(this);
                //有对应权限或者系统版本小于7.0
                if (isPermission || Build.VERSION.SDK_INT < 24){
                    SuspendController.getInstance().startSuspendService(this);
                }
                break;
            }

            case R.id.set_update_password_button:{
                Intent intent = new Intent(this,SetUpdatePW.class);
                startActivity(intent);
                break;
            }
            default:break;
        }
    }
}
