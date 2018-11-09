package com.think.onepass.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.think.onepass.R;

public class SetNewPassWordActivity extends Activity {
    private SharedPreferences msharedPreferences;
    private Button mButtonSurePassWord;
    private ImageView mimageViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_new_password);
        //创建一个SharedPreferences实例
        msharedPreferences = this.getSharedPreferences("settings", MODE_PRIVATE);
        mButtonSurePassWord = findViewById(R.id.surepasswordbt);
        mButtonSurePassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText metNewPassWord = findViewById(R.id.newpasswordedittext);
                EditText metSurePassWord = findViewById(R.id.surepasswordedittext);
                SharedPreferences.Editor meditor = msharedPreferences.edit();
                String mNewPassWord = metNewPassWord.getText().toString();
                String mSurePassWord = metSurePassWord.getText().toString();
                if ((mNewPassWord.length() == 4) && mNewPassWord.equals(mSurePassWord)) {
                    meditor.putString("password", mNewPassWord);
                    meditor.commit();
                    Toast.makeText(SetNewPassWordActivity.this, getString(R.string.Password_modification_succeeded), Toast.LENGTH_SHORT).show();
                    finish();
                } else if (mNewPassWord.length() == 0 && mSurePassWord.length() == 0) {
                    return;
                } else {
                    Toast.makeText(SetNewPassWordActivity.this, getString(R.string.Failed_to_modify_password), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mimageViewBack=findViewById(R.id.back);
        mimageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}










