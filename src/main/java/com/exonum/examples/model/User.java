package com.exonum.examples.model;

import com.exonum.examples.cryptoowls.model.ModelProtos;

public class User {
  private static final long INITIAL_USER_BALANCE = 1000;

  private final String name;
  private long balance;
  private long reserved;

  public User(String name) {
    this.name = name;
    this.balance = INITIAL_USER_BALANCE;
    this.reserved = 0;
  }

  public User(ModelProtos.User user) {
    this.name = user.getName();
    this.balance = user.getBalance();
    this.reserved = user.getReserved();
  }

  public void increaseBalance(long amount) {
    assert amount >= 0;
    this.balance += amount;
  }

  public void decreaseBalance(long amount) {
    assert amount >= 0;
    this.balance -= amount;
  }

  public void reserveMoney(long amount) {
    assert amount >= 0;
    this.balance -= amount;
    this.reserved += amount;
  }

  public void releaseMoney(long amount) {
    assert amount >= 0;
    this.balance += amount;
    this.reserved -= amount;
  }

  public ModelProtos.User toProtobuf() {
    return ModelProtos.User.newBuilder()
        .setName(name)
        .setBalance(balance)
        .setReserved(reserved)
        .build();
  }

  public long getBalance() {
    return balance;
  }

  public String getName() {
    return name;
  }

  public long getReserved() {
    return reserved;
  }
}
