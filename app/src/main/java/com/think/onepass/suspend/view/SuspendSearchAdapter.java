package com.think.onepass.suspend.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.think.onepass.R;
import com.think.onepass.model.Secret;

import java.util.List;

public class SuspendSearchAdapter extends RecyclerView.Adapter<SuspendSearchAdapter.ViewHolder>{
    private static final String TAG = "SuspendSearchAdapter";
    private List<Secret> mSecretList;
    private Context mContext;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTitle,mUser,mPassword;
        View suspendSearchView;
        public ViewHolder(View view){
            super(view);
            suspendSearchView =view;
            mTitle=view.findViewById(R.id.suspend_search_title);
            mUser=view.findViewById(R.id.suspend_search_user);
            mPassword=view.findViewById(R.id.suspend_search_password);
        }
    }
    SuspendSearchAdapter(List<Secret> secretList,Context context){
        mSecretList=secretList;
        mContext=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.suspend_search_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Secret secret=mSecretList.get(position);
        holder.mTitle.setText(secret.getTitle());
        holder.mUser.setText("ID");
        if(secret.getPassword()==null||secret.getPassword().equals("")){
            holder.mPassword.setText("");
        }else {
            holder.mPassword.setText("......");
        }
        holder.mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboardWithString(mSecretList.get(position).getUser());
                Toast.makeText(mContext,"帐号复制到剪贴板成功",Toast.LENGTH_SHORT).show();
            }
        });
        holder.mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(secret.getPassword()==null||secret.getPassword().equals("")){
                    Toast.makeText(mContext,"密码为空",Toast.LENGTH_SHORT).show();
                }else {
                    setClipboardWithString(secret.getPassword());
                    Toast.makeText(mContext,"密码复制到剪贴板成功",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSecretList.size();
    }

    public void setClipboardWithString(String text) {
        ClipboardManager clipboardManager=(ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(text);
    }
}
