package com.think.onepass.view;

import com.think.onepass.model.Secret;

import java.util.List;
import java.util.Map;

public class HeadContract {
    public static interface View{
        public void setClipboardWithString(String text);
        public void setPresenter(Presenter presenter);
        public void searchSecretByKey(String key,int deleted);
        public void addSecret();
    }
    public static interface Presenter{
        public Map<String,Object> addSecrets(Secret secret);
        public String updateSecrets(Secret secret);
        public void deleteSecret(long id);
        public List<Secret> searchSecretByKey(String key,int deleted);
        List<String> selectAllLabel();
        List<List<Secret>> selectSecretsByLabel(List<String> labels);
        List<Secret> getSecretsByLasttimeDesc();
        public boolean isContainById(long id);

    }
}
