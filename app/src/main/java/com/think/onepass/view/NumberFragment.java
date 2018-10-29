package com.think.onepass.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.think.onepass.R;
import com.think.onepass.util.SharePreferenceUtils;

public class NumberFragment extends Fragment {
    private static final String TAG = "NumberFragment";
    private UnlockActivity mActivty;
    private RelativeLayout mrlChangeToFingerPrintFrament;
    private RelativeLayout relativeLayout;
    private EditText metPassWord;
    private SharedPreferences msharePreferences;
    private TextView mtvPasswordInput;
    private ImageView mimageviewone,mimageviewtwo,mimageviewthree,mimageviewfour;
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
        relativeLayout=view.findViewById(R.id.changetofingerprintframentrl);
        mtvPasswordInput=view.findViewById(R.id.textView);
        msharePreferences = mActivty.getSharedPreferences("settings", Context.MODE_PRIVATE);
        metPassWord = (EditText)view.findViewById(R.id.passwordeditText);
        //屏蔽系统的软键盘
        metPassWord.setInputType(InputType.TYPE_NULL);
        String mPassWord =msharePreferences.getString("password","");
        if(mPassWord.length()==0){
            mtvPasswordInput.setText(getResources().getString(R.string.head_first_password));
        }
        else {
            mtvPasswordInput.setText(getResources().getString(R.string.head_password));
        }
        if(!SharePreferenceUtils.getFingerprintopenKey()) {
            relativeLayout.setVisibility(View.GONE);
        }
        else {
            mrlChangeToFingerPrintFrament = view.findViewById(R.id.changetofingerprintframentrl);
            mrlChangeToFingerPrintFrament.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: ");
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    //用新建的片段替换当前的片段
                    transaction.replace(R.id.unlock_layout, new FingerprintFragment());
                    //执行该事务
                    transaction.commit();
                }
            });
        }

        KeyboardUtil mKeyBoard = new KeyboardUtil(view, metPassWord);
        mimageviewone = view.findViewById(R.id.dotimageview1);
        mimageviewtwo = view.findViewById(R.id.dotimageview2);
        mimageviewthree = view.findViewById(R.id.dotimageview3);
        mimageviewfour = view.findViewById(R.id.dotimageview4);
        SetImageSize(mimageviewone,14);
        SetImageSize(mimageviewtwo,14);
        SetImageSize(mimageviewthree,14);
        SetImageSize(mimageviewfour,14);

        metPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String mPassWord =msharePreferences.getString("password","");
                if (metPassWord.getText().length() == 0){
                    SetImageSize(mimageviewone,14);
                    SetImageSize(mimageviewtwo,14);
                    SetImageSize(mimageviewthree,14);
                    SetImageSize(mimageviewfour,14);
                }
                else if (metPassWord.getText().length() == 1){
                    SetImageSize(mimageviewone,30);
                    SetImageSize(mimageviewtwo,14);
                    SetImageSize(mimageviewthree,14);
                    SetImageSize(mimageviewfour,14);
                }
                else if (metPassWord.getText().length() == 2){
                    SetImageSize(mimageviewone,30);
                    SetImageSize(mimageviewtwo,30);
                    SetImageSize(mimageviewthree,14);
                    SetImageSize(mimageviewfour,14);
                }
                else if (metPassWord.getText().length() == 3){
                    SetImageSize(mimageviewone,30);
                    SetImageSize(mimageviewtwo,30);
                    SetImageSize(mimageviewthree,30);
                    SetImageSize(mimageviewfour,14);
                }
                else if (metPassWord.getText().length() == 4){
                    SetImageSize(mimageviewone,30);
                    SetImageSize(mimageviewtwo,30);
                    SetImageSize(mimageviewthree,30);
                    SetImageSize(mimageviewfour,30);
                }

                if (mPassWord.length()==0){
                    if(metPassWord.getText().length()==4){
                        SharedPreferences.Editor meditor = msharePreferences.edit();
                        meditor.putString("password",metPassWord.getText().toString());
                        meditor.commit();
                        Toast.makeText(mActivty,getResources().getString(R.string.password_correct), Toast.LENGTH_SHORT).show();
                        mActivty.onAuthenticated();
                    }
                }
                else{
                    if (metPassWord.getText().toString().equals(mPassWord)){
                        Toast.makeText(mActivty,getResources().getString(R.string.password_correct), Toast.LENGTH_SHORT).show();
                        mActivty.onAuthenticated();
                    }
                    else if (!(metPassWord.getText().toString().equals(mPassWord)) && metPassWord.getText().toString().length()==4){
                        Toast.makeText(mActivty,getResources().getString(R.string.password_incorrect), Toast.LENGTH_SHORT).show();
                    }
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
    private void SetImageSize(ImageView imageView,int size){
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.height= size;
        params.width = size;
        imageView.setLayoutParams(params);
    }

}
