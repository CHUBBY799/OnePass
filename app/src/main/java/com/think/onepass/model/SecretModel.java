package com.think.onepass.model;

import android.support.v4.app.ListFragment;

import java.util.List;
import java.util.Map;

public interface SecretModel {
      Map<String,Object> addSecret(Secret secret);
      List<Secret> searchSecretByKey(String key,int deleted);
      List<Secret> getSecretsByLasttimeDesc();
      String updateSecret(Secret secret);
      void deleteSecretById(long id);
      List<String> selectAllLabel();
      List<Secret> selectSecretByLabel(String label);
      public boolean isContainById(long id);
}
