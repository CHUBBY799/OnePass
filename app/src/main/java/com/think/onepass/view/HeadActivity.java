package com.think.onepass.view;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.think.onepass.R;

public class HeadActivity extends AppCompatActivity implements View.OnClickListener,HeadContract.view{
    private ImageView setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head);
        setting=findViewById(R.id.head_setting);
        setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_setting:
                Intent intent=new Intent(HeadActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void setClipboardWithString(String text) {
        ClipboardManager clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
    }
}
