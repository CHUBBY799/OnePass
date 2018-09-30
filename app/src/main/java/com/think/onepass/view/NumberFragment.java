package com.think.onepass.view;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.think.onepass.R;

public class NumberFragment extends Fragment{
    private UnlockActivity mActivty;
    private TextView mtvChangeToFingerPrintFrament;
    private EditText metPassWord;
    private SharedPreferences msharePreferences;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivty = (UnlockActivity)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.number_unlock,container,false);
        mtvChangeToFingerPrintFrament = (TextView)view.findViewById(R.id.changetofingerprintframenttv);
        mtvChangeToFingerPrintFrament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    FragmentManager fragmentManager=getFragmentManager();
                    FragmentTransaction transaction=fragmentManager.beginTransaction();
                    //用新建的片段替换当前的片段
                    transaction.replace(R.id.unlock_layout,new FingerprintFragment());
                    //执行该事务
                    transaction.commit();
            }
        });
        metPassWord = (EditText)view.findViewById(R.id.passwordeditText);
        //屏蔽系统的软键盘
        metPassWord.setInputType(InputType.TYPE_NULL);
        KeyboardUtil mKeyBoard = new KeyboardUtil(view, metPassWord);

        metPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                msharePreferences = mActivty.getSharedPreferences("password",Context.MODE_PRIVATE);
                String mPassWord =msharePreferences.getString("password","密码");
                if (metPassWord.getText().toString().equals(mPassWord)){
                    Toast.makeText(mActivty,"success",Toast.LENGTH_SHORT).show();
                    mActivty.onAuthenticated();
                }
                else if (!(metPassWord.getText().toString().equals(mPassWord)) && metPassWord.getText().toString().length()==4){
                    Toast.makeText(mActivty,"密码错误，请重新输入",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}
