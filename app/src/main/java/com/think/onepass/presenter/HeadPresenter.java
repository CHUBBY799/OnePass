package com.think.onepass.presenter;

import android.content.Context;

import com.think.onepass.model.Secret;
import com.think.onepass.model.SecretModel;
import com.think.onepass.view.HeadActivity;
import com.think.onepass.view.HeadContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HeadPresenter implements HeadContract.Presenter{
    private SecretModel mModel;
    private HeadContract.View mView;

    public HeadPresenter(HeadContract.View view,SecretModel model){
        mView=view;
        mModel=model;
        mView.setPresenter(this);
    }



    @Override
    public Map<String,Object> addSecrets(Secret secret) {
       return mModel.addSecret(secret);
    }

    @Override
    public String updateSecrets(Secret secret) {
       return mModel.updateSecret(secret);
    }

    @Override
    public void deleteSecret(long id) {
        mModel.deleteSecretById(id);
    }

    @Override
    public List<Secret> searchSecretByKey(String key, int deleted) {
        return mModel.searchSecretByKey(key,deleted);
    }

    @Override
    public List<String> selectAllLabel() {
        return mModel.selectAllLabel();
    }

    @Override
    public List<List<Secret>> selectSecretsByLabel(List<String> labels) {
        List<List<Secret>> lists=new ArrayList<>();
        for(String label:labels){

            lists.add(mModel.selectSecretByLabel(label));
        }
        return lists;
    }

    @Override
    public List<Secret> getSecretsByLasttimeDesc() {
        return mModel.getSecretsByUseDesc();
    }

    @Override
    public boolean isContainById(long id) {
        return mModel.isContainById(id);
    }

    public void addUse(long id){
        mModel.addUse(id);
    }
}
