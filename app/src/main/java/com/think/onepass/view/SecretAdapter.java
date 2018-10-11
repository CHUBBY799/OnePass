package com.think.onepass.view;

import android.content.Context;
import android.content.DialogInterface;
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
import com.think.onepass.util.DialogUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SecretAdapter extends RecyclerView.Adapter<SecretAdapter.ViewHolder> {
    private static final String TAG = "SecretAdapter";
    private List<Integer> mSecretMode;
    private List<Secret> mSecretList;
    private Context mContext;
    private Callback mCallback;
    public static final int NORMAL_MODE=0;
    public static final int ADD_MODE=1;
    public static final int UPDATE_MODE=2;
    public static Map<Integer,Secret> updateCache=new HashMap<>();
//    private Set<Secret> UPDATE_ITEMS=new HashSet<>();
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
//        Log.d(TAG, "onCreateViewHolder: "+holder.getAdapterPosition());
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+holder.getLayoutPosition()+" : "+position + " : "+ mSecretMode.get(holder.getLayoutPosition()));
        Secret secret=mSecretList.get(holder.getLayoutPosition());
        // 当文本框内容发生变化时,将文本内容更新至mSecrets
        setEditText(holder.secretTitle,secret.getTitle(),secret,holder);
        setEditText(holder.secretUser,secret.getUser(),secret,holder);
        setEditText(holder.secretPassword,secret.getPassword(),secret,holder);
        setEditText(holder.secretLabel,secret.getLabel(),secret,holder);
        holder.secretTime.setText(secret.getLastTime());
        switch (mSecretMode.get(holder.getLayoutPosition())){
            case NORMAL_MODE:
                holder.secretTime.setVisibility(View.VISIBLE);
                holder.secretUserCopy.setVisibility(View.VISIBLE);
                holder.secretPasswordCopy.setVisibility(View.VISIBLE);
                holder.secretConfirm.setVisibility(View.GONE);
                setImageviewMargin(holder.secretDelete,15);
                break;
            case ADD_MODE:
                holder.secretTime.setVisibility(View.INVISIBLE);
                holder.secretUserCopy.setVisibility(View.GONE);
                holder.secretPasswordCopy.setVisibility(View.GONE);
                holder.secretConfirm.setVisibility(View.VISIBLE);
                setImageviewMargin(holder.secretDelete,52);
                break;
            case UPDATE_MODE:
                holder.secretTime.setVisibility(View.VISIBLE);
                holder.secretUserCopy.setVisibility(View.VISIBLE);
                holder.secretPasswordCopy.setVisibility(View.VISIBLE);
                holder.secretConfirm.setVisibility(View.VISIBLE);
                setImageviewMargin(holder.secretDelete,52);
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
                int position=holder.getLayoutPosition();
                if(mSecretList.get(position).getUser()==null||mSecretList.get(position).getUser().equals("")){
                    Toast.makeText(mContext,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }else if(mSecretList.get(position).getTitle()==null||mSecretList.get(position).getTitle().equals("")){
                    Toast.makeText(mContext,"标题不能为空",Toast.LENGTH_SHORT).show();
                } else {
                    String time;
                    if(mSecretMode.get(position)==ADD_MODE){
                        Map<String,Object> response=mCallback.addSecrets(mSecretList.get(position));
                        holder.secretTime.setVisibility(View.VISIBLE);
                        time=(String)response.get("lastTime");
                        holder.secretTime.setText(time);
                        mSecretList.get(position).setLastTime(time);
                        mSecretList.get(position).setId((long)response.get("id"));
                    }else if(mSecretMode.get(position)==UPDATE_MODE){
                        holder.secretTime.setVisibility(View.VISIBLE);
                        time=mCallback.updateSecrets(mSecretList.get(position));
                        holder.secretTime.setText(time);
                        mSecretList.get(position).setLastTime(time);
                    }
                    mSecretMode.set(position,NORMAL_MODE);
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
                final int position=holder.getLayoutPosition();
                int mode=mSecretMode.get(position);
                String title;
                String message;
                switch (mode){
                    case NORMAL_MODE:
                        title="Delete";
                        message="Do you want to delete the "+mSecretList.get(position).getTitle();
                        DialogUtils.showDialog(mContext, title, message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onNormalDelete(position);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        break;
                    case UPDATE_MODE:
                        title="Revoke";
                        message="Do you want to revoking ?";
                        DialogUtils.showDialog(mContext, title, message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onUpdateDelete(position, holder);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        break;
                    case ADD_MODE:
                        title="Delete";
                        message="Will not save your information.";
                        DialogUtils.showDialog(mContext, title, message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onAddDelete(position);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        break;
                }
            }
        });
    }
    private void onUpdateDelete(int position,ViewHolder holder){
        if(updateCache.containsKey(position)){
            Secret cacheSecret=updateCache.get(position);
            Secret trueSecret=mSecretList.get(position);
            trueSecret.setTitle(cacheSecret.getTitle());
            trueSecret.setUser(cacheSecret.getUser());
            trueSecret.setPassword(cacheSecret.getPassword());
            trueSecret.setLabel(cacheSecret.getLabel());
            mSecretMode.set(position,NORMAL_MODE);
            holder.secretConfirm.setVisibility(View.GONE);
            setImageviewMargin(holder.secretDelete,15);
            notifyDataSetChanged();
        }
    }
    private void onAddDelete(int position){
        mSecretList.remove(position);
        mSecretMode.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mSecretList.size()-position);
    }
    private void onNormalDelete(int position){
        long secretId=mSecretList.get(position).getId();
        mCallback.deleteSecret(secretId);
        mSecretList.remove(position);
        mSecretMode.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,mSecretList.size()-position);
    }
    private void setImageviewMargin(ImageView view,int margin){
        ConstraintLayout.LayoutParams lp=(ConstraintLayout.LayoutParams)view.getLayoutParams();
        float density=mContext.getResources().getDisplayMetrics().density;
        lp.rightMargin=(int)(margin*density);
        view.setLayoutParams(lp);
    }

    private void setEditText(final EditText myEditText, String data, final Secret secret, final ViewHolder holder){
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
                Log.d(TAG, "afterTextChanged: "+s.toString());
                int position=holder.getLayoutPosition();
                if(myEditText.getId() == R.id.secret_password &&  s.toString().equals(mSecretList.get(position).getPassword())){
                    return;
                }
                if(mSecretMode.get(position)==NORMAL_MODE){
                    mSecretMode.set(position,UPDATE_MODE);
                    Secret cacheSecret=new Secret();
                    cacheSecret.setTitle(secret.getTitle());
                    cacheSecret.setUser(secret.getUser());
                    cacheSecret.setPassword(secret.getPassword());
                    cacheSecret.setLabel(secret.getLabel());
                    updateCache.put(position,cacheSecret);
                }
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
                if(holder.secretConfirm.getVisibility()==View.GONE){
                    holder.secretConfirm.setVisibility(View.VISIBLE);
                    setImageviewMargin(holder.secretDelete,52);
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

    public void setCallBack(Callback callBack){
        mCallback=callBack;
    }
    public static interface Callback{
        public Map<String, Object> addSecrets(Secret secret);
        public String updateSecrets(Secret secret);
        public void deleteSecret(long id);
    }
}
