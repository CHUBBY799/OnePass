package com.think.onepass.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.think.onepass.R;
import com.think.onepass.model.Secret;

import java.util.List;

public class SecretAdapter extends RecyclerView.Adapter<SecretAdapter.ViewHolder> {
    private static final String TAG = "SecretAdapter";
    private List<Integer> mSecretMode;
    private List<Secret> mSecretList;
    private Context mContext;
    public static final int NORMAL_MODE=0;
    public static final int ADD_MODE=1;
    public static final int UPDATE_MODE=2;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View secretView;
        TextView secretTime;
        EditText secretUser,secretPassword,secretLabel,secretTitle;
        ImageView secretConfirm,secretDelete,secretUserCopy,secretPasswordCopy;

        public ViewHolder(View view){
            super(view);
            secretView=view;
            secretTitle=view.findViewById(R.id.secret_title);
            secretTime=view.findViewById(R.id.secret_time);
            secretUser=view.findViewById(R.id.secret_user);
            secretPassword=view.findViewById(R.id.secret_password);
            secretLabel=view.findViewById(R.id.secret_label);
            secretConfirm=view.findViewById(R.id.secret_confirm);
            secretDelete=view.findViewById(R.id.secret_delete);
            secretUserCopy=view.findViewById(R.id.secret_copy_user);
            secretPasswordCopy=view.findViewById(R.id.secret_copy_password);
//            secretConfirm.setVisibility(View.GONE);
        }
    }
    public SecretAdapter(List<Secret> secretList, List<Integer> secretMode,Context context){
        mSecretList=secretList;
        mSecretMode=secretMode;
        mContext=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.secret_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        Log.d(TAG, "onCreateViewHolder: "+holder.getAdapterPosition());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Secret secret=mSecretList.get(position);
        setEditText(holder.secretTitle,secret.getTitle(),secret);
        setEditText(holder.secretUser,secret.getUser(),secret);
        setEditText(holder.secretPassword,secret.getPassword(),secret);
        setEditText(holder.secretLabel,secret.getLabel(),secret);
        holder.secretTime.setText(secret.getLastTime());
        Log.d(TAG, "onBindViewHolder: "+mSecretMode.get(position)+"  :  "+holder.getAdapterPosition());
        switch (mSecretMode.get(position)){
            case ADD_MODE:
                holder.secretUserCopy.setVisibility(View.GONE);
                holder.secretPasswordCopy.setVisibility(View.GONE);
                holder.secretTime.setVisibility(View.GONE);
                holder.secretConfirm.setVisibility(View.VISIBLE);
                setImageviewMargin(holder.secretDelete,52);
                break;
            case UPDATE_MODE:
                holder.secretConfirm.setVisibility(View.GONE);
                setImageviewMargin(holder.secretDelete,15);
                break;
        }
        holder.secretUserCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HeadContract.View)mContext).setClipboardWithString(holder.secretUser.getText().toString());
                Toast.makeText(mContext,"已经将用户名复制到粘贴板",Toast.LENGTH_SHORT).show();
            }
        });
        holder.secretPasswordCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HeadContract.View)mContext).setClipboardWithString(holder.secretPassword.getText().toString());
                Toast.makeText(mContext,"已经将密码复制到粘贴板",Toast.LENGTH_SHORT).show();
            }
        });

        holder.secretConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                if(mSecretList.get(position).getUser()==null){
                    Toast.makeText(mContext,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    if(mSecretMode.get(position)==ADD_MODE){
                        holder.secretTime.setVisibility(View.VISIBLE);
                        holder.secretTime.setText(((HeadContract.View)mContext).addSecrets(mSecretList.get(position)));
                        mSecretMode.set(position,UPDATE_MODE);
                    }else if(mSecretMode.get(position)==UPDATE_MODE){
                        holder.secretTime.setVisibility(View.VISIBLE);
                        holder.secretTime.setText(((HeadContract.View)mContext).updateSecrets(mSecretList.get(position)));
                    }
                    holder.secretConfirm.setVisibility(View.GONE);
                    setImageviewMargin(holder.secretDelete,15);
                    holder.secretUserCopy.setVisibility(View.VISIBLE);
                    holder.secretPasswordCopy.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.secretDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                if(mSecretMode.get(position)==UPDATE_MODE){
                    long secretId=mSecretList.get(position).getId();
                    ((HeadContract.View)mContext).deleteSecret(secretId);
                }
                mSecretList.remove(position);
                mSecretMode.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mSecretList.size()-position);

            }
        });
    }
    private void setImageviewMargin(ImageView view,int margin){
        ConstraintLayout.LayoutParams lp=(ConstraintLayout.LayoutParams)view.getLayoutParams();
        float density=mContext.getResources().getDisplayMetrics().density;
        lp.rightMargin=(int)(margin*density);
        view.setLayoutParams(lp);
    }

    private void setEditText(final EditText myEditText, String data, final Secret secret){
        if(myEditText.getTag() instanceof TextWatcher){
            myEditText.removeTextChangedListener((TextWatcher)myEditText.getTag());
        }
        myEditText.setText(data);
        TextWatcher watcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (myEditText.getId()){
                    case R.id.secret_title:
                        secret.setTitle(myEditText.getText().toString());
                        break;
                    case R.id.secret_user:
                        secret.setUser(myEditText.getText().toString());
                        break;
                    case R.id.secret_password:
                        secret.setPassword(myEditText.getText().toString());
                        break;
                    case R.id.secret_label:
                        secret.setLabel(myEditText.getText().toString());
                        break;
                }
            }
        };
        myEditText.addTextChangedListener(watcher);
        myEditText.setTag(watcher);
    }



    @Override
    public int getItemCount() {
        return mSecretList.size();
    }
}
