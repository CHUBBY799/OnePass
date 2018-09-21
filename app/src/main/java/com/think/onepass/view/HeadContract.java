package com.think.onepass.view;

import com.think.onepass.model.Secret;

import java.util.List;
import java.util.Map;

public class HeadContract {
    public static interface View{
        public void setClipboardWithString(String text);
        public void setPresenter(Presenter presenter);
        public void setSecrets(List<Secret> secrets);
        public void initSecrets();
        public Map<String,Object> addSecrets(Secret secret);
        public String updateSecrets(Secret secret);
        public void deleteSecret(long id);
        public void searchSecretByKey(String key,int deleted);
    }
    public static interface Presenter{
        public void initSecrets();
        public Map<String,Object> addSecrets(Secret secret);
        public String updateSecrets(Secret secret);
        public void deleteSecret(long id);
        public void searchSecretByKey(String key,int deleted);
    }
}
