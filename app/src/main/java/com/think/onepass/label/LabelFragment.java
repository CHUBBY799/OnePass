package com.think.onepass.label;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.think.onepass.R;
import com.think.onepass.model.Secret;
import com.think.onepass.view.HeadActivity;
import com.think.onepass.view.HeadContract;
import com.think.onepass.view.SecretFragment;

import java.util.ArrayList;
import java.util.List;

public class LabelFragment extends Fragment{
    private static final String TAG = "LabelFragment";
    private RecyclerView mRecycleView;
    private Context mContext;
    private List<String> labelGroup;
    private List<List<Secret>> labelChild;
    private HeadContract.Presenter mPresenter;
    private LabelAdapter mAdapter;
    private View mNodata;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.secret_label_fragment,container,false);
        mRecycleView=view.findViewById(R.id.label_recycler);
        initData();
        mNodata = view.findViewById(R.id.head_no_data);
        return view;
    }
    public void setPresenter(HeadContract.Presenter presenter){
        mPresenter=presenter;
    }
    private void initData(){
        mContext=getActivity();
        labelGroup=mPresenter.selectAllLabel();
        labelChild=mPresenter.selectSecretsByLabel(labelGroup);
        LinearLayoutManager manager=new LinearLayoutManager(mContext);
        mRecycleView.setLayoutManager(manager);
        mAdapter=new LabelAdapter(mContext,labelGroup,labelChild);
        mAdapter.setCallback(new LabelAdapter.Callback() {
            @Override
            public void deleteSecret(final Secret secret) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Delete");
                dialog.setMessage("Do you want to delete "+secret.getTitle()+" ?");
                dialog.setCancelable(true);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.deleteSecret(secret.getId());
                        refreshData();
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

            @Override
            public void showSecret(Secret secret) {
                SecretFragment secretFragment=new SecretFragment();
                secretFragment.setmPresenter(mPresenter);
                List<Secret> secrets=new ArrayList<>();
                secrets.add(secret);
                secretFragment.initData(secrets);
                secretFragment.setType(2); // 2 : label页面
                ((HeadActivity)mContext).replaceFragmentBackStack(secretFragment);
            }
        });
        mRecycleView.setAdapter(mAdapter);
    }
    private void refreshData(){
        labelGroup.clear();
        labelGroup.addAll(mPresenter.selectAllLabel());
        labelChild.clear();
        labelChild.addAll(mPresenter.selectSecretsByLabel(labelGroup));
        Log.d(TAG, "refreshData: "+labelGroup.size());
        mAdapter.notifyDataSetChanged();
        if(labelGroup.size() == 0){
            mNodata.setVisibility(View.VISIBLE);
            ImageView iv = mNodata.findViewById(R.id.main_no_data_iv);
            TextView tvTop = mNodata.findViewById(R.id.main_no_data_tv_top);
            TextView tvBottom = mNodata.findViewById(R.id.main_no_data_tv_bottom);
            iv.setImageResource(R.mipmap.no_label);
            tvTop.setText(getResources().getString(R.string.label_no_data_top));
            tvBottom.setText(getResources().getString(R.string.label_no_data_bottom));
        }else {
            mNodata.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
}
