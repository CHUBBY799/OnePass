package com.think.onepass.model;

import java.util.List;

public interface SecretModel {
      void addSecret(Secret secret);
      List<Secret> searchSecretByLable(String lable);

}
