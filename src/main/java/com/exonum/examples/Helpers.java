package com.exonum.examples;

import com.exonum.examples.cryptoowls.model.ModelProtos;

public class Helpers {
  private static final long INITIAL_USER_BALANCE = 1000;

  public static ModelProtos.User createUser(String name) {
    return ModelProtos.User.newBuilder()
        .setName(name)
        .setBalance(INITIAL_USER_BALANCE)
        .setReserved(0)
        .build();
  }
}
