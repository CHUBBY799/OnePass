package com.think.onepass.model;

import android.support.v4.app.ListFragment;

import java.util.List;

public interface SecretModel {
      void addSecret(Secret secret);
      List<Secret> searchSecretByLable(String lable);
      List<Secret> getSecretsByLasttimeDesc();
      void updateSecret(Secret secret);
}
