package com.think.onepass.view;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.think.onepass.R;
import com.think.onepass.suspend.SuspendController;
import com.think.onepass.suspend.permission.FloatPermissionManager;

public class SettingActivity extends Activity implements View.OnClickListener{
    private Button openSuspend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        openSuspend=findViewById(R.id.setting_suspend_open);
        openSuspend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_suspend_open:
                boolean isPermission = FloatPermissionManager.getInstance().applyFloatWindow(this);
                //有对应权限或者系统版本小于7.0
                if (isPermission || Build.VERSION.SDK_INT < 24){
                    SuspendController.getInstance().startSuspendService(this);
                }
                break;
        }
    }
}
