package com.think.onepass.label;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.think.onepass.R;
import com.think.onepass.model.Secret;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.ViewHolder>{
    private static final String TAG = "LabelAdapter";
    private Context mContext;
    private LabelFragment fragment;
    private List<String> labelGroup;
    private List<List<Secret>> labelChild;
    private Map<TitleView,LinearLayout> findLinear;
    private Callback mCallback;


    static class ViewHolder extends RecyclerView.ViewHolder{
    View labelView;
    TextView label;
    LinearLayout titleView;
    public ViewHolder(View view){
        super(view);
        labelView=view;
        label=view.findViewById(R.id.secret_label_label);
        titleView=view.findViewById(R.id.secret_label_title);
    }
    }
    public LabelAdapter(Context context,List<String> labelGroup,List<List<Secret>> labelChild) {
        mContext = context;
        this.labelGroup = labelGroup;
        this.labelChild = labelChild;
        findLinear= new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.secret_label_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        String label=labelGroup.get(position);
        List<Secret> secrets=labelChild.get(position);
        holder.titleView.removeAllViews();
        for(Secret secret:secrets) {
            TitleView view=new TitleView(mContext,null,secret);
            view.setCallBack(new TitleView.CallBack() {
                @Override
                public void deleteSecret(Secret secret, View view) {
                    mCallback.deleteSecret(secret.getId());
                }

                @Override
                public void showSecret(Secret secret) {
                    mCallback.showSecret(secret);
                }
            });
            holder.titleView.addView(view);
            findLinear.put(view,holder.titleView);
        }
        if(label==null||label.equals("")){
            holder.label.setText("空标题");
        }else {
            holder.label.setText(label);
        }

    }

    @Override
    public int getItemCount() {
        return labelGroup.size();
    }

    public void setCallback(Callback callback){
        mCallback=callback;
    }
    public static interface Callback{
        public void deleteSecret(long id);
        public void showSecret(Secret secret);
    }
}
