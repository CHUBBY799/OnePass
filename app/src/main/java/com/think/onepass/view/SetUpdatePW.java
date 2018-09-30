package com.think.onepass.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.think.onepass.R;

public class SetUpdatePW extends Activity implements View.OnClickListener{
    private EditText metNewPassWord;
    private EditText metSurePassWord;
    private Button mbtSurePassWord;
    private SharedPreferences msharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_update_password);
        metNewPassWord = findViewById(R.id.newpasswordedittext);
        metSurePassWord = findViewById(R.id.surepasswordedittext);
        mbtSurePassWord = findViewById(R.id.surepasswordbutton);
        //创建一个SharedPreferences实例
        msharedPreferences = SetUpdatePW.this.getSharedPreferences("password",MODE_PRIVATE);
        mbtSurePassWord.setOnClickListener(SetUpdatePW.this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.surepasswordbutton:{
                    SharedPreferences.Editor meditor = msharedPreferences.edit();
                    String mNewPassWord = metNewPassWord.getText().toString();
                    String mSurePassWord = metSurePassWord.getText().toString();
                    if((mNewPassWord.length()==4) && mNewPassWord.equals(mSurePassWord)){
                        meditor.putString("password",mNewPassWord);
                        meditor.commit();
                        Toast.makeText(SetUpdatePW.this,"设置/修改密码成功",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(SetUpdatePW.this,"设置/修改密码失败",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            default:break;
        }
    }
}
