package com.exonum.examples.Api;

import com.exonum.binding.common.crypto.PublicKey;
import com.google.gson.annotations.SerializedName;

public class UserDTO {
  @SerializedName("public_key")
  private PublicKey publicKey;
  private String name;
  private long balance;
  private long reserved;

  public UserDTO(PublicKey publicKey, String name, long balance, long reserved) {
    this.publicKey = publicKey;
    this.name = name;
    this.balance = balance;
    this.reserved = reserved;
  }
}
