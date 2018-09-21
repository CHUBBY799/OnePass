package com.think.onepass.model;

import android.support.v4.app.ListFragment;

import java.util.List;

public interface SecretModel {
      String addSecret(Secret secret);
      List<Secret> searchSecretByLable(String lable,int deleted);
      List<Secret> getSecretsByLasttimeDesc();
      String updateSecret(Secret secret);
      void deleteSecretById(long id);
}
