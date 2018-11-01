package com.think.onepass.setting.backup;

import com.think.onepass.model.Secret;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MyBackUp implements Serializable {
    private String password;
    private String publicKey;
    private String privateKey;
    private ArrayList<Secret> mSecrets;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public ArrayList<Secret> getSecrets() {
        return mSecrets;
    }

    public void setSecrets(ArrayList<Secret> secrets) {
        mSecrets = secrets;
    }
}
