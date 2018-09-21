package com.think.onepass.presenter;

import android.content.Context;

import com.think.onepass.model.Secret;
import com.think.onepass.model.SecretModel;
import com.think.onepass.view.HeadActivity;
import com.think.onepass.view.HeadContract;

public class HeadPresenter implements HeadContract.Presenter {
    private SecretModel mModel;
    private HeadContract.View mView;

    public HeadPresenter(HeadContract.View view,SecretModel model){
        mView=view;
        mModel=model;
        mView.setPresenter(this);
    }

    @Override
    public void initSecrets() {
        mView.setSecrets(mModel.getSecretsByLasttimeDesc());
    }

    @Override
    public String addSecrets(Secret secret) {
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
}
