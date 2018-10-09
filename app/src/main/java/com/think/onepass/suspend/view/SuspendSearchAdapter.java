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
    private Callback mCallback;
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
            holder.mPassword.setText("123456");
        }
        holder.mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.setCliboardWithString(mSecretList.get(position).getUser());
            }
        });
        holder.mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(secret.getPassword()==null||secret.getPassword().equals("")){
                    Toast.makeText(mContext,"密码为空",Toast.LENGTH_SHORT).show();
                }else {
                    mCallback.setCliboardWithString(secret.getPassword());
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

    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public static interface Callback{
        public void setCliboardWithString(String data);
    }
}
