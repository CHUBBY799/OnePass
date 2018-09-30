package com.think.onepass.label;

import android.content.Context;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.secret_label_fragment,container,false);
        mRecycleView=view.findViewById(R.id.label_recycler);
        initData();
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
            public void deleteSecret(long id) {
                mPresenter.deleteSecret(id);
                refreshData();
            }

            @Override
            public void showSecret(Secret secret) {
                SecretFragment secretFragment=new SecretFragment();
                secretFragment.setmPresenter(mPresenter);
                List<Secret> secrets=new ArrayList<>();
                secrets.add(secret);
                secretFragment.initData(secrets);
                ((HeadActivity)mContext).replaceFragment(secretFragment);
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
    }

}
