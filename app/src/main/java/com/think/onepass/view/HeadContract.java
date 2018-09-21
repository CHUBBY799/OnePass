package com.think.onepass.view;

import com.think.onepass.model.Secret;

import java.util.List;

public class HeadContract {
    public static interface View{
        public void setClipboardWithString(String text);
        public void setPresenter(Presenter presenter);
        public void setSecrets(List<Secret> secrets);
        public void initSecrets();
        public String addSecrets(Secret secret);
        public String updateSecrets(Secret secret);
        public void deleteSecret(long id);
    }
    public static interface Presenter{
        public void initSecrets();
        public String addSecrets(Secret secret);
        public String updateSecrets(Secret secret);
        public void deleteSecret(long id);
    }
}
